package org.example;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.discos.DiscoGrupo;
import com.github.britooo.looca.api.group.discos.Volume;
import com.github.britooo.looca.api.group.janelas.Janela;
import com.github.britooo.looca.api.group.janelas.JanelaGrupo;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import com.github.britooo.looca.api.group.processos.Processo;
import com.github.britooo.looca.api.group.processos.ProcessoGrupo;
import com.github.britooo.looca.api.group.rede.RedeInterfaceGroup;
import com.github.britooo.looca.api.group.rede.RedeParametros;
import com.github.britooo.looca.api.group.servicos.ServicoGrupo;
import com.github.britooo.looca.api.group.sistema.Sistema;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Componentes {
    Conexao conexao = new Conexao();
    Looca looca = new Looca();
   private volatile Integer pid = 0;
   private volatile String IP = "";
   private volatile Boolean parar = false;
   private Scanner input = new Scanner(System.in);
   private String cParar;


    public void Memoria() {
        Thread thread = new Thread(() -> {
        //Sistema
        Sistema sistema = looca.getSistema();
        //Hostname da maquina
        String hostName = looca.getRede().getParametros().getHostName();

        //Lista de processos
            String respostaConexaoHost = conexao.verificarMaquina(hostName);
            if (respostaConexaoHost.equals("Maquina não existe")) {
                conexao.cadastrarMaquina(hostName);
            } else {
                if (respostaConexaoHost.equals("Maquina existe") && parar==false){
                    System.out.println("""
                 O monitoramendo do seu hardwere foi executado com sucesso!
                            """);
                    pararCaptura();
                }
                while (respostaConexaoHost.equals("Maquina existe") && !parar) {

                    RedeParametros redeParametros = looca.getRede().getParametros();
                    Memoria memoria = looca.getMemoria();
                    Processador processador = looca.getProcessador();
                    ServicoGrupo servicoGrupo = looca.getGrupoDeServicos();
                    //Janela, tem titulo do da janela
                    List<Janela> janelaGrupo = looca.getGrupoDeJanelas().getJanelas();
                    //Disco do computador
                    List<Disco> discoGrupo = looca.getGrupoDeDiscos().getDiscos();

                    ProcessoGrupo processoGrupo = looca.getGrupoDeProcessos();
                    List<Processo> listaDeProcessos = processoGrupo.getProcessos();


                    String memoriaEmUso = extrairProcessadorEmUso(processador);
                    String identificador = extrairIdentificadorMaquina(processador);

                    try {
                        InetAddress inetAddress = InetAddress.getLocalHost();
                        String ipAddress = inetAddress.getHostAddress();
                        IP = ipAddress;
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    long maiorMemoriaVirtual = 0;
                    int pidMaiorMemoriaVirtual = 0;

                    for (Processo processo : listaDeProcessos) {
                        long memoriaVirtual = extrairMemoriaVirtual(processo);
                        if (memoriaVirtual > maiorMemoriaVirtual) {
                            maiorMemoriaVirtual = memoriaVirtual;
                            pidMaiorMemoriaVirtual = processo.getPid();
                            pid = pidMaiorMemoriaVirtual;
                        }
                    }
                    List dadosInformados = new ArrayList<>();
                    dadosInformados.add(maiorMemoriaVirtual);
                    dadosInformados.add(pidMaiorMemoriaVirtual);

                    conexao.ComponenteMemoria(memoria, processador, servicoGrupo, janelaGrupo, discoGrupo, sistema, pid, IP, hostName);

                    try {
                        Thread.sleep(5000);  // Pausa de 5 segundos
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public void pararCaptura(){
        System.out.println("""
               Para encerrar o monitoramento, digite 'c'
               """);
        cParar = input.nextLine();

        if (cParar .equalsIgnoreCase("c")){
            parar = true;
            System.out.println("""
                   Encerrando captura ...
                   
                   Sua captura foi encerrada.
                   """);
            voltarMonitoramento();
        }else{
            pararCaptura();
        }
    }

    public void voltarMonitoramento (){
        System.out.println("""
               Para retornar o monitoramento do sofware, digite 'v'
               """);
        cParar = input.nextLine();
        if (cParar.equalsIgnoreCase("v")){
            System.out.println("""
                    Estamos retornando as capturas, isso pode levar algum tempo ...
                    """);
            parar = false;
            Memoria();

        }else {
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
                        String valorEmUso = partes[1].trim();
                        return valorEmUso;
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
                    String valorEmUso = partes[1].trim();
                    return valorEmUso;
                } else {
                    System.out.println("Formato incorreto para 'ID:'");
                }
            }
        }

        System.out.println("'ID:' não encontrado");
        return "";
    }
    }


