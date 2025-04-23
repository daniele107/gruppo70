package model;
import java.time.LocalDate;

public class Organizzatore extends Utente {
  public Organizzatore() {
    super();
  }

  public LocalDate AperturaIscrizioni(LocalDate data){
    return data;
  }
  public LocalDate ChiusuraIscrizioni(LocalDate data){
    return data;
  }
}
