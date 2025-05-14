
// File: Documento.java
package model;

import java.time.LocalDate;
import java.io.File;

/**
 * Rappresenta un documento caricato per un hackathon.
 */
public class Documento {
    private LocalDate data;
    private String contenuto;
    private File file;
    private Hackathon hackathon;

    /**
     * Crea un documento a partire da un file.
     */
    public Documento(File file) {
        this.file = file;
        this.data = LocalDate.now();
        this.contenuto = "";
    }

    public Documento(LocalDate data, String contenuto) {
        this.data = data;
        this.contenuto = contenuto;
    }

    public void modificaDocumento(String nuovo) {
        this.contenuto = nuovo;
        this.data = LocalDate.now();
    }

    public LocalDate getData() { return data; }
    public String getContenuto() { return contenuto; }
    public File getFile() { return file; }

    public void setHackathon(Hackathon hackathon) { this.hackathon = hackathon; }
    public Hackathon getHackathon() { return hackathon; }

    @Override
    public String toString() {
        return data + ": " + (file != null ? file.getName() : contenuto);
    }
}
