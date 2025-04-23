package model;

import java.time.LocalDate;

public class Documento {
  protected LocalDate data;
  protected String documento;

  public Documento(){}

  public Documento modifica_documento( String documento){
    return Documento.this.modifica_documento(documento);
  }

}
