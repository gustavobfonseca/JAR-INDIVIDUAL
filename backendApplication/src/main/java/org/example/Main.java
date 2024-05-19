package org.example;
import javax.swing.*;
import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.sistema.Sistema;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Usuario usuario = new Usuario();

        Scanner perguntaUser = new Scanner(System.in);
        System.out.println("Qual seria o e-mail?");
        String email = perguntaUser.nextLine();
        System.out.println("Qual seria a senha?");
        String senha = perguntaUser.nextLine();

        String respostaBanco = usuario.validarUser(email, senha);

        System.out.println(respostaBanco);

        

    }
}