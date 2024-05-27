package org.example;
import javax.swing.*;
import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.sistema.Sistema;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Usuario usuario = new Usuario();
        System.out.println("""
                BEM-VINDOS AO SOFTWARE DE MONITORAMENTO SISGUARD
                
                ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                ⠀⠀⠀⠀⠀⢀⣠⣤⣤⣤⣤⣀⠀⠀⠀⠀⠀⠀⠀⣤⣤⣤⣤⣤⣤⣤⣤⣤⣀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣤⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                ⠀⠀⠀⢠⣾⣿⣿⣿⣿⣿⣿⣿⣿⣦⠀⠀⠀ ⠀⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣦⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀
                ⠀⠀⢠⣿⣿⣿⡿⠛⠉⠙⠻⣿⣿⣿⣧⠀⠀⠀⣿⣿⣿⣿⠉⠉⠉⠛⢿⣿⣿⣿⣧⠀⠀⠀⠀⠀⠀⠀⠀⣀⣤⣾⣿⣟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                ⠀⠀⣼⣿⣿⣿⠃⠀⠀⠀⠀⠀⣿⣿⣿⣿⠀⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⠀⠀⠀⣾⣿⡿⠿⠿⠿⣿⣦⠀⠀⠀⠀⠀⠀⠀⠀
                ⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⠀⠀⠛⠛⠛⠛⠀⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⠀⠀⢸⣿⣿⡇⢠⡄⢀⣿⣿⡄⠀⠀⠀⠀⠀⠀⠀
                ⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ ⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⠀⠀⠀⠀⠀⠀⣿⡟⣿⣿⣿⠃⣸⣿⣿⣧⠀⠀⠀⠀⠀⠀⠀
                ⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ ⠀⠀⣿⣿⣿⣿⣤⣤⣤⣤⣾⣿⣿⣿⡏⠀⠀⠀⠀⠀⢸⣿⠁⣸⣿⡟⠀⣿⣿⡌⢿⣧⠀⠀⠀⠀⠀⠀
                ⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ ⠀⠀⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠋⠀⠀⠀⠀⠀⠀⣾⡇⠀⣿⣿⣃⣸⣿⣿⣿⠈⠻⣷⣄⡀⠀⠀⠀
                ⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ ⠀⠀⣿⣿⣿⣿⠛⠛⢻⣿⣿⣿⡆⠀⠀⠀⠀⠀⠀⢠⡟⠀⢠⣿⣿⣿⣿⣿⣿⣿⣧⠀⠈⢿⡦⠀⠀⠀
                ⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ ⠀⠀⣿⣿⣿⣿⠀⠀⠈⣿⣿⣿⣿⡀⠀⠀⠀⠀⠀⠀⠀⠀⣾⣿⣿⣿⠛⠛⣿⣿⣿⣧⠀⠀⠀⠀⠀⠀
                ⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⠀⢠⣤⣤⣤⡄⠀ ⠀⣿⣿⣿⣿⠀⠀⠀⠸⣿⣿⣿⣧⠀⠀⠀⠀⠀⠀⠀⢸⣿⡿⠋⠁⠀⠀⠀⠈⠻⣿⣿⡄⠀⠀⠀⠀⠀
                ⠀⠀⢹⣿⣿⣿⣇⠀⠀⠀⢀⣿⣿⣿⣿⠀ ⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⢻⣿⣿⣿⡆⠀⠀⠀⠀⠀⣰⣿⠟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠘⣿⣧⠀⠀⠀⠀⠀
                ⠀⠀⠀⢻⣿⣿⣿⣷⣶⣶⣿⣿⣿⣿⠃⠀⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⠘⣿⣿⣿⣿⡀⠀⠀⠀⣼⣿⡏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢹⣿⣧⠀⠀⠀⠀
                ⠀⠀⠀⠀⠙⠻⢿⣿⣿⣿⣿⠿⠛⠁⠀⠀⠀⠀⣿⣿⣿⣿⠀⠀⠀⠀⠀⢹⣿⣿⣿⣧⠀⠀⣸⡿⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⢿⣇⠀⠀⠀
                ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                """);
        Scanner perguntaUser = new Scanner(System.in);
        System.out.println("Insira o e-mail?");
        String email = perguntaUser.nextLine();
        System.out.println("");
        System.out.println("Insira a senha?");
        String senha = perguntaUser.nextLine();
        System.out.println("");

        String respostaBanco = usuario.validarUser(email, senha);

        System.out.println(respostaBanco);

        

    }
}