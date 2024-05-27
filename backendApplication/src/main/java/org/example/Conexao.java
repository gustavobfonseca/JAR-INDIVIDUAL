package org.example;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.janelas.Janela;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import com.github.britooo.looca.api.group.servicos.ServicoGrupo;
import com.github.britooo.looca.api.group.sistema.Sistema;

import java.sql.*;
import java.util.List;

public class Conexao {
    Looca looca = new Looca();
    public Integer idDark;
    public Integer idEmpresa;
    public Integer idMaquina = null;
    private final String localhost = "jdbc:mysql://localhost/sisguard";
    private final String userL = "root";
    private final String passwordL = "sptech";
    private String canal = null;

    private String WEBHOOK_URL = "https://hooks.slack.com/services/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";
    private String USERNAME = looca.getRede().getParametros().getHostName();
    private String CHANNEL = obterCanalDoBancoDeDados();
    private String MESSAGE_TEXT = "!!! Alerta automático de 5 em 5 segundos!!!";

    public Conexao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao carregar o driver JDBC: " + e.getMessage());
        }
    }

    public void inicializarParametros(String hostname) {
//        this.hostname = hostname;
        this.idMaquina = pegarIdMaquina(hostname);

        if (this.idMaquina != null) {
            this.USERNAME = this.idMaquina.toString();
        } else {
            System.err.println("ID da máquina não encontrado.");
        }

        this.CHANNEL = obterCanalDoBancoDeDados();
        if (this.CHANNEL == null) {
            System.err.println("Canal Slack não encontrado.");
        }
    }

    public static void logarUser(String email, String senha) {
        if (email.isEmpty() || senha.isEmpty()) {
            System.out.println("Login inválido");
            return;
        }

        try (Connection conexaoBanco = DriverManager.getConnection("jdbc:mysql://localhost/sisguard", "root", "sptech")) {
            ResultSet respostaServer = conexaoBanco.createStatement().executeQuery(
                    "select * from empresa where email = '" + email + "' and senha = '" + senha + "'"
            );

            if (respostaServer.next()) {
                Usuario usuario = new Usuario();
                usuario.respostaUser(respostaServer, email, senha);
            } else {
                System.out.println("E-mail ou senha incorretos");
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao conectar ao banco de dados: " + ex.getMessage());
        }
    }

    public String verificarMaquina(String hostname) {
        if (hostname.isEmpty()) {
            return "host vazio";
        }

        try (Connection conexao = DriverManager.getConnection("jdbc:mysql://localhost/sisguard", "root", "sptech")) {
            ResultSet respostaMaquina = conexao.createStatement().executeQuery(
                    "SELECT * FROM maquina where hostname = '" + hostname + "'"
            );

            if (respostaMaquina.next()) {
                return "Maquina existe";
            } else {
                return "Maquina não existe";
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public String cadastrarMaquina(String hostname) {
        try (Connection conexao = DriverManager.getConnection("jdbc:mysql://localhost/sisguard", "root", "sptech")) {
            int respostaBanco = conexao.createStatement().executeUpdate(
                    "INSERT INTO maquina VALUES(NULL, '" + hostname + "', 1)"
            );

            if (respostaBanco == 1) {
                Componentes componentes = new Componentes();
                componentes.Memoria();
                return "Maquina cadastrada com sucesso";
            } else {
                return "Maquina não cadastrada";
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public Memoria ComponenteMemoria(Memoria memoria, Processador processador, ServicoGrupo servicoGrupo,
                                     List<Janela> janelaGrupo, List<Disco> discoGrupo, Sistema sistema, Integer pid, String IP, String hostName) {
        String dadosMemoria = String.valueOf(memoria);
        String dadosProcessador = String.valueOf(processador);
        String dadosServico = String.valueOf(servicoGrupo);
        String dadosJanela = String.valueOf(janelaGrupo);
        String dadosSistema = String.valueOf(sistema);
        String dadosDisco = String.valueOf(discoGrupo);
        Integer idMaquina = pegarIdMaquina(hostName);

        try (Connection conexaoBanco = DriverManager.getConnection("jdbc:mysql://localhost/sisguard", "root", "sptech")) {
            int respostaServer = conexaoBanco.createStatement().executeUpdate(
                    "insert into registro(cpuPorcentagem, ramPorcentagem, discoPorcentagem, pid, fkMaquinaDarksore, fkMaquina) " +
                            "values('" + dadosProcessador + "','" + dadosMemoria + "','" + dadosDisco + "'," + pid + "," + idDark + "," + idMaquina + ")"
            );

            if (respostaServer != 1) {
                System.out.println("Erro ao cadastrar os dados");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return memoria;
    }

    public Integer pegarIdMaquina(String hostname) {
        try (Connection conexao = DriverManager.getConnection("jdbc:mysql://localhost/sisguard", "root", "sptech")) {
            ResultSet respostaServer = conexao.createStatement().executeQuery(
                    "SELECT * from empresa as e join darkstore as d on e.idEmpresa = d.fkEmpresa " +
                            "join maquina as m on d.idDarkstore = m.fkDarkstore where m.hostname = '" + hostname + "'"
            );

            if (respostaServer.next()) {
                idMaquina = respostaServer.getInt("idMaquina");
                idDark = respostaServer.getInt("idDarkstore");
                idEmpresa = respostaServer.getInt("idEmpresa");
            } else {
                System.out.println("Nenhum registro encontrado para hostname: " + hostname);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ou consultar: " + e.getMessage());
        }

        return idMaquina;
    }

    public String obterCanalDoBancoDeDados() {
        String query = "SELECT canal FROM slack";
        try (Connection conn = DriverManager.getConnection(localhost, userL, passwordL);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                canal = rs.getString("canal");
                return canal;
            } else {
                System.err.println("Nenhum canal encontrado no banco de dados.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            return null;
        }
    }

    public Integer getIdDark() {
        return idDark;
    }

    public void setIdDark(Integer idDark) {
        this.idDark = idDark;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public Integer getIdMaquina() {
        return idMaquina;
    }

    public void setIdMaquina(Integer idMaquina) {
        this.idMaquina = idMaquina;
    }

    public String getLocalhost() {
        return localhost;
    }

    public String getUserL() {
        return userL;
    }

    public String getPasswordL() {
        return passwordL;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getWEBHOOK_URL() {
        return WEBHOOK_URL;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public String getCHANNEL() {
        return CHANNEL;
    }

    public String getMESSAGE_TEXT() {
        return MESSAGE_TEXT;
    }
}
