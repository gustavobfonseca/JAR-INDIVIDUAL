package org.example;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MetricasAlerta {
    ConexaoLocal conexaoLocal = new ConexaoLocal();
    Slack slack = Slack.getInstance();

    private String mensagemCPU = null;
    private String mensagemDisco = null;
    private String mensagemRAM = null;

    private Double alertaPadrao = null;
    private Double criticoPadrao = null;
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

    public Double capturarAlertaPadrao() {
        try (Connection conexaoBanco = DriverManager.getConnection(conexaoLocal.getLocalhost(), conexaoLocal.getUserL(),
                conexaoLocal.getPasswordL())) {
            ResultSet respostaServer = conexaoBanco.createStatement().executeQuery(
                    "select alertaPadrao from metrica_ideal where fkDarkStore = " + idDark + ";");

            if (respostaServer.next()) {
                alertaPadrao = respostaServer.getDouble("alertaPadrao");
            } else {
                System.out.println("Não consegui capturar as metricas do alertaPadrao");
                return alertaPadrao;
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar ao banco de dados: " + ex.getMessage());
        }
        return alertaPadrao;
    }

    public Double capturarCriticoPadrao() {
        try (Connection conexaoBanco = DriverManager.getConnection(conexaoLocal.getLocalhost(), conexaoLocal.getUserL(),
                conexaoLocal.getPasswordL())) {
            ResultSet respostaServer = conexaoBanco.createStatement().executeQuery(
                    "select criticoPadrao from metrica_ideal where fkDarkStore = " + idDark + ";");

            if (respostaServer.next()) {
                criticoPadrao = respostaServer.getDouble("criticoPadrao");
            } else {
                System.out.println("Não consegui capturar as metricas do criticoPadrao");
                return criticoPadrao;
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar ao banco de dados: " + ex.getMessage());
        }
        return criticoPadrao;
    }


    public Double capturarAlertaRam() {
        try (Connection conexaoBanco = DriverManager.getConnection(conexaoLocal.getLocalhost(), conexaoLocal.getUserL(),
                conexaoLocal.getPasswordL())) {
            ResultSet respostaServer = conexaoBanco.createStatement().executeQuery(
                    "select alertaRAM from metrica_ideal where fkDarkStore = " + idDark + ";");

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

    public Double capturarCriticoRam() {
        try (Connection conexaoBanco = DriverManager.getConnection(conexaoLocal.getLocalhost(), conexaoLocal.getUserL(),
                conexaoLocal.getPasswordL())) {
            ResultSet respostaServer = conexaoBanco.createStatement().executeQuery(
                    "select criticoRAM from metrica_ideal where fkDarkStore = " + idDark + ";");

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

    public Double capturarAlertaCpu() {
        try (Connection conexaoBanco = DriverManager.getConnection(conexaoLocal.getLocalhost(), conexaoLocal.getUserL(),
                conexaoLocal.getPasswordL())) {
            ResultSet respostaServer = conexaoBanco.createStatement().executeQuery(
                    "select alertaCPU from metrica_ideal where fkDarkStore = " + idDark + ";");

            if (respostaServer.next()) {
                alertaCPU = respostaServer.getDouble("alertaCPU");
            } else {
                System.out.println("Não consegui capturar as metricas do alertaCPU");
                return alertaCPU;
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar ao banco de dados: " + ex.getMessage());
        }
        return alertaCPU;
    }

    public Double capturarCriticoCpu() {
        try (Connection conexaoBanco = DriverManager.getConnection(conexaoLocal.getLocalhost(), conexaoLocal.getUserL(),
                conexaoLocal.getPasswordL())) {
            ResultSet respostaServer = conexaoBanco.createStatement().executeQuery(
                    "select criticoCPU from metrica_ideal where fkDarkStore = " + idDark + ";");

            if (respostaServer.next()) {
                criticoCPU = respostaServer.getDouble("criticoCPU");
            } else {
                System.out.println("Não consegui capturar as metricas do criticoCPU");
                return criticoCPU;
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar ao banco de dados: " + ex.getMessage());
        }
        return criticoCPU;
    }

    public Double capturarAlertaDisco() {
        try (Connection conexaoBanco = DriverManager.getConnection(conexaoLocal.getLocalhost(), conexaoLocal.getUserL(),
                conexaoLocal.getPasswordL())) {
            ResultSet respostaServer = conexaoBanco.createStatement().executeQuery(
                    "select alertaDisco from metrica_ideal where fkDarkStore = " + idDark + ";");

            if (respostaServer.next()) {
                alertaDisco = respostaServer.getDouble("alertaDisco");
            } else {
                System.out.println("Não consegui capturar as metricas do alertaDisco");
                return alertaDisco;
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar ao banco de dados: " + ex.getMessage());
        }
        return alertaDisco;
    }

    public Double capturarCriticoDisco() {
        try (Connection conexaoBanco = DriverManager.getConnection(conexaoLocal.getLocalhost(), conexaoLocal.getUserL(),
                conexaoLocal.getPasswordL())) {
            ResultSet respostaServer = conexaoBanco.createStatement().executeQuery(
                    "select criticoDisco from metrica_ideal where fkDarkStore = " + idDark + ";");

            if (respostaServer.next()) {
                criticoDisco = respostaServer.getDouble("criticoDisco");
            } else {
                System.out.println("Não consegui capturar as metricas do criticoDisco");
                return criticoDisco;
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar ao banco de dados: " + ex.getMessage());
        }
        return criticoDisco;
    }

    public Boolean esperar5Minutos(Boolean esperar, String mensagem) {
        if (!esperar) {
            new Thread(() -> {
                try {
                    Thread.sleep(60000);
                    if ( mensagem != null && mensagem.contains("RAM") ) {
                        esperarRAM = false;
                    } else if (mensagem != null && mensagem.contains("CPU") ) {
                        esperarCPU = false;
                    } else if ( mensagem != null && mensagem.contains("Disco")) {
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
                    System.out.println("Mensagem enviada para o slack no canal: " + conexaoLocal.getCHANNEL() + ", "
                            + conexaoLocal.getUSERNAME());
                    System.out.println(mensagem);
                    System.out.println("");
                } else {
                    System.out.println("NÃO CONSEGUI MANDAR MSG NO SLACK");
                }
            } catch (IOException e) {
                System.err.println("Erro nas config. da mensagem para o Slack: " + e.getMessage());
            }
        }
    }

    public void verificarMetricaAlertas(Memoria memoria, Processador processador) {
        capturarAlertaPadrao();
        capturarAlertaCpu();
        capturarAlertaDisco();
        capturarAlertaRam();

        capturarCriticoPadrao();
        capturarCriticoCpu();
        capturarCriticoDisco();
        capturarCriticoRam();

        double ramUso = ((double) memoria.getEmUso() / memoria.getTotal()) * 100;
        double cpuUso = processador.getUso();
        double discoUso = 40;
        System.out.println("ram uso agr");
        System.out.println(ramUso);
        System.out.println("cpu uso");
        System.out.println(cpuUso);

        if (alertaRAM != null) {
            if (ramUso >= alertaRAM && ramUso < criticoRAM) {
                String mensagemRAM = "memória RAM em alerta, fique de olho! Você está usando " + ramUso + "%";
                if (esperar5Minutos(esperarRAM, mensagemRAM) == false) {
                    enviarMensagem(mensagemRAM);
                    System.out.println(mensagemRAM + " caso o problema persistir voltaremos em 5 minutos");
                    esperarRAM = true;
                }

            } else if (ramUso >= criticoRAM) {
                String mensagemRAM = "memória RAM em estado crítico, fique de olho ! Você está usando " + ramUso + "%";
                if (esperar5Minutos(esperarRAM, mensagemRAM) == false) {
                    enviarMensagem(mensagemRAM);
                    System.out.println(mensagemRAM + " caso o problema persistir voltaremos em 5 minutos");
                    esperarRAM = true;
                }
            } else {
                if (ramUso >= alertaPadrao && ramUso < criticoPadrao) {
                    String mensagemRAM = "Memória RAM em alerta Você está usando " + ramUso + "%";
                    if (esperar5Minutos(esperarRAM, mensagemRAM) == false) {
                        enviarMensagem(mensagemRAM);
                        System.out.println(mensagemRAM + " caso o problema persistir voltaremos em 5 minutos");
                        esperarRAM = true;
                    }

                } else if (ramUso >= criticoPadrao) {
                    String mensagemRAM = "memória RAM em estado crítico Você está usando " + ramUso + "%";
                    if (esperar5Minutos(esperarRAM, mensagemRAM) == false) {
                        enviarMensagem(mensagemRAM);
                        System.out.println(mensagemRAM + " caso o problema persistir voltaremos em 5 minutos");
                        esperarRAM = true;
                    }
                }
            }
        }

        if (alertaCPU != null) {
            if (cpuUso >= alertaCPU && cpuUso < criticoCPU) {
                String mensagemCPU = "CPU em alerta, fique de olho ! Você está usando " + cpuUso + "%";
                if (esperar5Minutos(esperarCPU, mensagemCPU) == false) {
                    enviarMensagem(mensagemCPU);
                    System.out.println(mensagemCPU + " caso o problema persistir voltaremos em 5 minutos");
                    esperarCPU = true;
                }

            } else if (cpuUso >= criticoCPU) {
                String mensagemCPU = "CPU em estado crítico, fique de olho ! Você está usando" + cpuUso + "%";

                if (esperar5Minutos(esperarCPU, mensagemCPU) == false) {
                    enviarMensagem(mensagemCPU);
                    System.out.println(mensagemCPU + " caso o problema persistir voltaremos em 5 minutos");
                    esperarCPU = true;
                }

            }
        } else {
            if (cpuUso >= alertaPadrao && cpuUso < criticoPadrao) {
                String mensagemCPU = "CPU em alerta Você está usando" + cpuUso + "%";
                if (esperar5Minutos(esperarCPU, mensagemCPU) == false) {
                    enviarMensagem(mensagemCPU);
                    System.out.println(mensagemCPU + " caso o problema persistir voltaremos em 5 minutos");
                    esperarCPU = true;
                }


            } else if (cpuUso >= criticoPadrao) {
                String mensagemCPU = "CPU em estado crítico Você está usando" + cpuUso + "%";

                if (esperar5Minutos(esperarCPU, mensagemCPU) == false) {
                    enviarMensagem(mensagemCPU);
                    System.out.println(mensagemCPU + " caso o problema persistir voltaremos em 5 minutos");
                    esperarCPU = true;
                }

            }
        }

        if (alertaDisco != null) {
            if (discoUso >= alertaDisco && discoUso < criticoDisco) {
                String mensagemDisco = "Disco em alerta Você está usando" + discoUso + "%";


                if (esperar5Minutos(esperarDisco, mensagemDisco) == false) {
                    enviarMensagem(mensagemDisco);
                    System.out.println(mensagemDisco + " caso o problema persistir voltaremos em 5 minutos");
                    esperarDisco = true;
                }

            } else if (discoUso >= criticoDisco) {
                String mensagemDisco = "Disco em estado crítico Você está usando" + discoUso + "%";


                if (esperar5Minutos(esperarDisco, mensagemDisco) == false) {
                    enviarMensagem(mensagemDisco);
                    System.out.println(mensagemDisco + " caso o problema persistir voltaremos em 5 minutos");
                    esperarDisco = true;
                }
            }
        } else {
            if (discoUso >= alertaPadrao && discoUso < criticoPadrao) {
                String mensagemDisco = "Disco em alerta Você está usando" + discoUso + "%";


                if (esperar5Minutos(esperarDisco, mensagemDisco) == false) {
                    enviarMensagem(mensagemDisco);
                    System.out.println(mensagemDisco + " caso o problema persistir voltaremos em 5 minutos");

                } else {
                    esperarDisco = true;
                }

            } else if (discoUso >= criticoPadrao) {
                String mensagemDisco = "Disco em estado crítico Você está usando" + discoUso + "%";


                if (esperar5Minutos(esperarDisco, mensagemDisco) == false) {
                    enviarMensagem(mensagemDisco);
                    System.out.println(mensagemDisco + " caso o problema persistir voltaremos em 5 minutos");

                } else {
                    esperarDisco = true;
                }
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
