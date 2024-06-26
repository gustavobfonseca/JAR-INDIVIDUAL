package org.example;

import javax.swing.*;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.sistema.Sistema;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Usuario usuario = new Usuario();
        String respostaBanco = null;
        Boolean conectado = false;
        System.out.println("""
                                                             ,+--------+
                        ,-----------------------,          ,"        ,"|          
                      ,"                      ,"|        ,"        ,"  |          
                     +-----------------------+  |      ,"        ,"    |         
                     |  .-----------------.  |  |     +---------+      |          
                     |  |                 |  |  |     | -==----'|      |          
                     |  | BEM-VINDOS  AO  |  |  |     |         |      |          
                     |  |   SOFTWARE DE   |  |  |/----|`---=    |      |          
                     |  |  MONITORAMENTO  |  |  |     |         |      |
                     |  |    SISGUARD!    |  |  |   ,/|==== ooo |      ;          
                     |  |                 |  |  |  // |(((( [33]|    ,"          
                     |  `-----------------'  |," .;'| |((((     |  ,"             
                     +-----------------------+  ;;  | |         |,"    \s
                        /_)______________(_/  //'   | +---------+                 
                   ___________________________/___  `,                            
                  /  oooooooooooooooo  .o.  oooo /,   \\,"-----------              
                 / ==ooooooooooooooo==.o.  ooo= //   ,`\\--{)B     ,"              
                /_==__==========__==_ooo__ooo=_/'   /___________,"                
                `-----------------------------'
                           """);
            Scanner perguntaUser = new Scanner(System.in);
            System.out.println("Insira o e-mail:");
            String email = perguntaUser.nextLine();
            System.out.println("");
            System.out.println("Insira a senha:");
            String senha = perguntaUser.nextLine();
            System.out.println("");

            respostaBanco = usuario.validarUser(email, senha);
            conectado = usuario.getConectado();
            System.out.println(respostaBanco);
        }
    }
