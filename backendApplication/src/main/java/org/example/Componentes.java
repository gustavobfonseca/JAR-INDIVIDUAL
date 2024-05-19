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

public class Componentes {
    Conexao conexao = new Conexao();
    Looca looca = new Looca();

    public void Memoria() {
        RedeParametros redeParametros = looca.getRede().getParametros();
        Memoria memoria = looca.getMemoria();
        Processador processador = looca.getProcessador();
        ServicoGrupo servicoGrupo = looca.getGrupoDeServicos();
        //Janela, tem titulo do da janela
        List<Janela> janelaGrupo = looca.getGrupoDeJanelas().getJanelas();
        //Disco do computador
        List<Disco> discoGrupo = looca.getGrupoDeDiscos().getDiscos();

        //Sistema
        Sistema sistema = looca.getSistema();
        //Hostname da maquina
        String hostName = looca.getRede().getParametros().getHostName();

        //Lista de processos
        ProcessoGrupo processoGrupo = looca.getGrupoDeProcessos();
        List<Processo> listaDeProcessos = processoGrupo.getProcessos();

        Integer pid = 0;
        String IP = "";
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

        String respostaConexaoHost = conexao.verificarMaquina(hostName);
        if(respostaConexaoHost.equals("Maquina não existe")) {
            conexao.cadastrarMaquina(hostName);
        }else {
            while (respostaConexaoHost.equals("Maquina existe")) {
                conexao.ComponenteMemoria(memoria, processador, servicoGrupo, janelaGrupo, discoGrupo, sistema, pid,IP, hostName);
                try {
                    Thread.sleep(5000);  // Pausa de 5 segundos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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


