package com.hackathon.app;

import java.time.LocalDate;
import java.util.Scanner;
public class Hackathon {
    protected String titolo;
    protected String sede;
    protected LocalDate dataInizio;
    protected LocalDate dataFine;
    protected int massimoPartecipanti;
    protected int dimensioneTeam;
    public static void  main(String[] args){
        Utente a = new Organizzatore();
        Scanner scanner = new Scanner(System.in);
        System.out.println("inserisci un nome");
       a.nome =scanner.nextLine();
       System.out.println(a.nome);
    }
}
