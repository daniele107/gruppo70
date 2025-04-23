package model;

public class Voto {
  protected int voto;
    
  public Voto(){
  }
  public int controllo_voto(int voto) {
    if(voto >=0 && voto <= 10){
      return voto;
    } else {
      System.out.println("Errore");
      return voto=-1;
    }
  }
  public  int media_Voto(){
    return 0;
  }
  public String classifica(){
    return "nessuna classifica";
  }
}
