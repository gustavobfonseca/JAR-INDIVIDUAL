package org.example;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.janelas.Janela;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import com.github.britooo.looca.api.group.processos.Processo;
import com.github.britooo.looca.api.group.rede.RedeParametros;
import com.github.britooo.looca.api.group.servicos.ServicoGrupo;
import com.github.britooo.looca.api.group.sistema.Sistema;
import com.slack.api.Slack;

public class Componentes {
    MetricasAlerta metricasAlerta = new MetricasAlerta(new ConexaoLocal());
    Usuario usuario = new Usuario();
    ConexaoLocal conexaoLocal = new ConexaoLocal();
    Looca looca = new Looca();
    private volatile String IP = "";
    private volatile Boolean parar = false;
    private Thread inputThread;




    public void capturarDados() {
        // Sistema
        Sistema sistema = looca.getSistema();
        // Hostname da máquina
        String hostName = looca.getRede().getParametros().getHostName();

        RedeParametros redeParametros = looca.getRede().getParametros();
        Memoria memoria = looca.getMemoria();
        Processador processador = looca.getProcessador();
        ServicoGrupo servicoGrupo = looca.getGrupoDeServicos();
        List<Janela> janelaGrupo = looca.getGrupoDeJanelas().getJanelas();
        List<Disco> discoGrupo = looca.getGrupoDeDiscos().getDiscos();

        String memoriaEmUso = extrairProcessadorEmUso(processador);
        String identificador = extrairIdentificadorMaquina(processador);

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ipAddress = inetAddress.getHostAddress();
            IP = ipAddress;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Lista de processos
        String respostaConexaoHost = conexaoLocal.verificarMaquina(hostName);
        if (respostaConexaoHost.equals("Maquina não existe")) {
            conexaoLocal.cadastrarMaquina(hostName);
        } else {
            if (respostaConexaoHost.equals("Maquina existe") && !parar) {
                Thread capturaThread = new Thread(() -> {
                    final Integer[] contador = {0};
                    while (respostaConexaoHost.equals("Maquina existe") && !parar) {

                        if (contador[0] < 1) {
                            System.out.println("O monitoramendo do seu hardwere foi executado com sucesso!");
                        }
                        contador[0]++;

                        Integer pid = 0;
                        long maiorMemoriaVirtual = 0;
                        int pidMaiorMemoriaVirtual = 0;

                        // Atualizar a lista de processos a cada iteração
                        List<Processo> listaDeProcessos = looca.getGrupoDeProcessos().getProcessos();

                        for (Processo processo : listaDeProcessos) {
                            long memoriaVirtual = extrairMemoriaVirtual(processo);
                            if (memoriaVirtual > maiorMemoriaVirtual) {
                                maiorMemoriaVirtual = memoriaVirtual;
                                pidMaiorMemoriaVirtual = processo.getPid();
                                pid = pidMaiorMemoriaVirtual;
                            }
                        }
                        conexaoLocal.ComponenteMemoria(memoria, processador, servicoGrupo, janelaGrupo, discoGrupo, sistema, pid, IP, hostName);
                        metricasAlerta.verificarMetricaAlertas(memoria, processador);
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                capturaThread.start();

                // Iniciar o inputThread apenas uma vez
                if (inputThread == null || !inputThread.isAlive()) {
                    iniciarCapturaDeParada();
                }
            }
        }
    }

    public void iniciarCapturaDeParada() {
        inputThread = new Thread(() -> {
            Scanner input = new Scanner(System.in);
            String cParar;
            while (!parar) {
                System.out.println("Para encerrar o monitoramento, digite 'c'");
                cParar = input.nextLine();
                if (cParar.equalsIgnoreCase("c")) {
                    parar = true;
                    System.out.println("Sua captura foi encerrada.");
                    voltarMonitoramento();
                }
            }
        });
        inputThread.start();
    }

    public void voltarMonitoramento() {
        System.out.println("Para retornar o monitoramento do sofware, digite 'v'");
        Scanner input = new Scanner(System.in);
        String cParar = input.nextLine();
        if (cParar.equalsIgnoreCase("v")) {
            System.out.println("Estamos retornando as capturas, isso pode levar algum tempo ...");
            System.out.println("");
            parar = false;
            capturarDados();
        } else {
            voltarMonitoramento();
        }
    }

    private long extrairMemoriaVirtual(Processo processo) {
        String[] partes = processo.toString().split("Memória virtual utilizada: ");
        if (partes.length > 1) {
            String numero = partes[1].split(" ")[0].trim();
            try {
                return Long.parseLong(numero);
            } catch (NumberFormatException e) {
                System.err.println("Formato inválido para número: " + numero);
            }
        }
        return 0;
    }

    public String extrairProcessadorEmUso(Processador processador) {
        String linhasTeste = processador.toString();
        String[] linhas = linhasTeste.split("\\r?\\n");
        for (String linha : linhas) {
            if (linha.contains("Em Uso:")) {
                String[] partes = linha.split(":");
                if (partes.length > 1) {
                    return partes[1].trim();
                } else {
                    System.out.println("Formato incorreto para 'Em Uso:'");
                }
            }
        }
        System.out.println("'Em Uso:' não encontrado");
        return "";
    }

    public String extrairIdentificadorMaquina(Processador processador) {
        String linhasTeste = processador.toString();
        String[] linhas = linhasTeste.split("\\r?\\n");
        for (String linha : linhas) {
            if (linha.contains("ID:")) {
                String[] partes = linha.split(":");
                if (partes.length > 1) {
                    return partes[1].trim();
                } else {
                    System.out.println("Formato incorreto para 'ID:'");
                }
            }
        }
        System.out.println("'ID:' não encontrado");
        return "";
    }


    }


