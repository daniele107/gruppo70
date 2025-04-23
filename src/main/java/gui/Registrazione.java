package gui;

import controller.Controller;
import javax.swing.*;
public class Registrazione {
    private JPanel mainPanel;
    private static JFrame frameRegistrazione;
    private Controller controller;

    public static void main(String[] args) {
        frameRegistrazione = new JFrame("Registrazione");
        frameRegistrazione.setContentPane(new Registrazione().mainPanel);
        frameRegistrazione.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameRegistrazione.pack();
        frameRegistrazione.setVisible(true);


    }

}
