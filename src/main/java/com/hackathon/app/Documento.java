package com.hackathon.app;

import java.time.LocalDate;

public class Documento {
  protected LocalDate data;
  protected String documento;

  public Documento(LocalDate data, String documento);
  public Documento modifica_documento(LocalDate data, String documento);

}
