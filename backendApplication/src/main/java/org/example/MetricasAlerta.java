package org.example;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Volume;
// import com.github.britooo.looca.api.group.discos.VolumeGrupo;
import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MetricasAlerta {
    Componentes componentes = new Componentes();
    ConexaoLocal conexaoLocal = new ConexaoLocal();
    Looca looca = new Looca();
    Slack slack = Slack.getInstance();
    private String mensagem;

    private final Double alertaPadrao = 10.0;
    private final Double criticoPadrao = 20.0;
    private Double alertaRAM = null;
    private Double alertaCPU = null;
    private Double alertaDisco = null;
    private Double criticoRAM = null;
    private Double criticoCPU = null;
    private Double criticoDisco = null;
    private Integer idDark = conexaoLocal.getIdDark();

    private boolean esperarRAM = false;
    private boolean esperarCPU = false;
    private boolean esperarDisco = false;

    public MetricasAlerta(ConexaoLocal conexaoLocal) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao carregar o driver JDBC: " + e.getMessage());
        }
    }

    public Double capturarAlertaRam(String fkDarkstore) {
        if (fkDarkstore.isEmpty()) {
            System.out.println("Darkstore inválida");
            return null;
        }

        try (Connection conexaoBanco = DriverManager.getConnection(conexaoLocal.getLocalhost(), conexaoLocal.getUserL(), conexaoLocal.getPasswordL())) {
            ResultSet respostaServer = conexaoBanco.createStatement().executeQuery(
                    "select alertaRAM from metrica_ideal where fkDarkStore = " + idDark + ";"
            );

            if (respostaServer.next()) {
                alertaRAM = respostaServer.getDouble("alertaRAM");
            } else {
                System.out.println("Não consegui capturar as metricas do alertaRAM");
                return alertaRAM;
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar ao banco de dados: " + ex.getMessage());
        }
        return alertaRAM;
    }

    public Double capturarCriticoRam(String fkDarkstore) {
        if (fkDarkstore.isEmpty()) {
            System.out.println("Darkstore inválida");
            return null;
        }

        try (Connection conexaoBanco = DriverManager.getConnection(conexaoLocal.getLocalhost(), conexaoLocal.getUserL(), conexaoLocal.getPasswordL())) {
            ResultSet respostaServer = conexaoBanco.createStatement().executeQuery(
                    "select criticoRAM from metrica_ideal where fkDarkStore = " + idDark + ";"
            );

            if (respostaServer.next()) {
                criticoRAM = respostaServer.getDouble("criticoRAM");
            } else {
                System.out.println("Não consegui capturar as metricas do criticoRAM");
                return criticoRAM;
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar ao banco de dados: " + ex.getMessage());
        }
        return criticoRAM;
    }

    public Boolean esperar5Minutos(Boolean esperar) {
        if (!esperar) {
            enviarMensagem(mensagem);
            System.out.println(mensagem + " caso o problema persistir voltaremos em 5 minutos");
            new Thread(() -> {
                try {
                    Thread.sleep(300000);
                    if (mensagem.contains("RAM")) {
                        esperarRAM = false;
                    } else if (mensagem.contains("CPU")) {
                        esperarCPU = false;
                    } else if (mensagem.contains("Disco")) {
                        esperarDisco = false;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        return esperar;
    }

    public void enviarMensagem(String mensagem) {
        if (conexaoLocal.getUSERNAME() != null && conexaoLocal.getCHANNEL() != null) {
            Payload payload = Payload.builder()
                    .channel(conexaoLocal.getCHANNEL())
                    .username(conexaoLocal.getUSERNAME())
                    .text(mensagem)
                    .build();
            try {
                WebhookResponse response = slack.send(conexaoLocal.getWEBHOOK_URL(), payload);
                if (response.getCode() == 200) {
                } else {
                    System.err.println("Erro ao enviar mensagem para o Slack: " + response.getMessage());
                }
            } catch (IOException e) {
                System.err.println("Erro ao enviar mensagem para o Slack: " + e.getMessage());
            }
        } else {
            System.out.println("Erro de credencial na configuração do Slack");
        }
    }

    public void verificarMetricaAlertas() {
        double ramUso = ((double) looca.getMemoria().getEmUso() / looca.getMemoria().getTotal()) * 100;
        double cpuUso = looca.getProcessador().getUso();
        double discoUso = 0;

        // Double grupoDeVolumes = looca.getGrupoDeVolumes();
        // List<Volume> volumes = grupoDeVolumes.getVolumes();

        // for (Volume volume : volumes) {
        //     double total = volume.getTotal();
        //     double disponivel = volume.getDisponivel();
        //     double usado = total - disponivel;
        //     discoUso += (usado / total) * 100 / volumes.size();
        // }

        if (alertaRAM != null) {
            if (ramUso >= alertaRAM && ramUso < criticoRAM) {
                mensagem = "memória RAM em alerta, fique de olho !";
                esperar5Minutos(esperarRAM);
                esperarRAM = true;

            } else if (ramUso >= criticoRAM) {
                mensagem = "memória RAM em estado crítico, fique de olho !";
                esperar5Minutos(esperarRAM);
                esperarRAM = true;
            }
        } else {
            if (ramUso >= alertaPadrao && ramUso < criticoPadrao) {
                mensagem = "memória RAM em alerta";
                esperar5Minutos(esperarRAM);
                esperarRAM = true;

            } else if (ramUso >= criticoPadrao) {
                mensagem = "memória RAM em estado crítico";
                esperar5Minutos(esperarRAM);
                esperarRAM = true;
            }
        }

        if (alertaCPU != null) {
            if (cpuUso >= alertaCPU && cpuUso < criticoCPU) {
                mensagem = "CPU em alerta, fique de olho !";
                esperar5Minutos(esperarCPU);
                esperarCPU = true;

            } else if (cpuUso >= criticoCPU) {
                mensagem = "CPU em estado crítico, fique de olho !";
                esperar5Minutos(esperarCPU);
                esperarCPU = true;
            }
        } else {
            if (cpuUso >= alertaPadrao && cpuUso < criticoPadrao) {
                mensagem = "CPU em alerta";
                esperar5Minutos(esperarCPU);
                esperarCPU = true;

            } else if (cpuUso >= criticoPadrao) {
                mensagem = "CPU em estado crítico";
                esperar5Minutos(esperarCPU);
                esperarCPU = true;
            }
        }

        if (alertaDisco != null) {
            if (discoUso >= alertaDisco && discoUso < criticoDisco) {
                mensagem = "Disco em alerta, fique de olho !";
                esperar5Minutos(esperarDisco);
                esperarDisco = true;

            } else if (discoUso >= criticoDisco) {
                mensagem = "Disco em estado crítico, fique de olho !";
                esperar5Minutos(esperarDisco);
                esperarDisco = true;
            }
        } else {
            if (discoUso >= alertaPadrao && discoUso < criticoPadrao) {
                mensagem = "Disco em alerta";
                esperar5Minutos(esperarDisco);
                esperarDisco = true;

            } else if (discoUso >= criticoPadrao) {
                mensagem = "Disco em estado crítico";
                esperar5Minutos(esperarDisco);
                esperarDisco = true;
            }
        }
    }

    public ConexaoLocal getConexaoLocal() {
        return conexaoLocal;
    }

    public Double getAlertaPadrao() {
        return alertaPadrao;
    }

    public Double getCriticoPadrao() {
        return criticoPadrao;
    }

    public Double getAlertaRAM() {
        return alertaRAM;
    }

    public Double getcriticoRAM() {
        return criticoRAM;
    }

    public Integer getIdDark() {
        return idDark;
    }

}
