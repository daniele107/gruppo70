package model;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Utente a = new Giudice();
             a.nome="Marco";
             System.out.println(a.nome);
        Scanner scanner = new Scanner(System.in);
        System.out.println("inserisci un nome");
        a.nome =scanner.nextLine();
        System.out.println(a.nome);
        Team ercole = new Team();
          ercole.nome_team="forza napoli";
        System.out.println(ercole.nome_team);

        Voto b = new Voto();
        b.voto=30;
        System.out.println("Voto assegnato: " + b.controllo_voto(b.voto));
    }
}
