package com.hackathon.app;

import java.time.LocalDate;

public class Documento {
  private LocalDate data;
  private String documento;

  public Documento(LocalDate data, String documento);
  public LocalDate getData();
  public void setData(LocalDate data);
  public String getDocumento();
  public void setDocumento(String documento);
}
