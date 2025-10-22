package model;
import java.time.LocalDateTime;
import java.util.Objects;
/**
 * Rappresenta una richiesta di creazione di un evento.
 * Contiene tutti i dati necessari inseriti dall'utente nella form.
 * Immutable record-like class per garantire la sicurezza dei dati.
 */
public final class EventRequest {
    private final String nome;
    private final LocalDateTime dataInizio;
    private final LocalDateTime dataFine;
    private final String sede;
    private final boolean virtuale;
    private final int maxPartecipanti;
    private final int maxTeam;
    private final String descrizioneProblema;
    /**
     * Costruttore per creare una nuova EventRequest (deprecato - usare Builder)
     * @deprecated Usare {@link Builder} per evitare troppi parametri
     */
    @Deprecated
    public EventRequest(String nome, LocalDateTime dataInizio, LocalDateTime dataFine,
                       String sede, boolean virtuale, int maxPartecipanti, int maxTeam,
                       String descrizioneProblema) {
        this.nome = nome;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.sede = sede;
        this.virtuale = virtuale;
        this.maxPartecipanti = maxPartecipanti;
        this.maxTeam = maxTeam;
        this.descrizioneProblema = descrizioneProblema;
    }
    
    /**
     * Builder pattern per EventRequest
     */
    public static class Builder {
        private String nome;
        private LocalDateTime dataInizio;
        private LocalDateTime dataFine;
        private String sede;
        private boolean virtuale;
        private int maxPartecipanti;
        private int maxTeam;
        private String descrizioneProblema;
        
        public Builder(String nome, LocalDateTime dataInizio, LocalDateTime dataFine) {
            this.nome = nome;
            this.dataInizio = dataInizio;
            this.dataFine = dataFine;
        }
        
        public Builder sede(String sede) {
            this.sede = sede;
            return this;
        }
        
        public Builder virtuale(boolean virtuale) {
            this.virtuale = virtuale;
            return this;
        }
        
        public Builder maxPartecipanti(int maxPartecipanti) {
            this.maxPartecipanti = maxPartecipanti;
            return this;
        }
        
        public Builder maxTeam(int maxTeam) {
            this.maxTeam = maxTeam;
            return this;
        }
        
        public Builder descrizioneProblema(String descrizioneProblema) {
            this.descrizioneProblema = descrizioneProblema;
            return this;
        }
        
        public EventRequest build() {
            return new EventRequest(nome, dataInizio, dataFine, sede, virtuale, 
                                  maxPartecipanti, maxTeam, descrizioneProblema);
        }
    }
    // Getters
    public String nome() { return nome; }
    public LocalDateTime dataInizio() { return dataInizio; }
    public LocalDateTime dataFine() { return dataFine; }
    public String sede() { return sede; }
    public boolean virtuale() { return virtuale; }
    public int maxPartecipanti() { return maxPartecipanti; }
    public int maxTeam() { return maxTeam; }
    public String descrizioneProblema() { return descrizioneProblema; }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EventRequest that = (EventRequest) obj;
        return virtuale == that.virtuale &&
               maxPartecipanti == that.maxPartecipanti &&
               maxTeam == that.maxTeam &&
               Objects.equals(nome, that.nome) &&
               Objects.equals(dataInizio, that.dataInizio) &&
               Objects.equals(dataFine, that.dataFine) &&
               Objects.equals(sede, that.sede) &&
               Objects.equals(descrizioneProblema, that.descrizioneProblema);
    }
    @Override
    public int hashCode() {
        return Objects.hash(nome, dataInizio, dataFine, sede, virtuale, 
                          maxPartecipanti, maxTeam, descrizioneProblema);
    }
    @Override
    public String toString() {
        return "EventRequest{" +
                "nome='" + nome + '\'' +
                ", dataInizio=" + dataInizio +
                ", dataFine=" + dataFine +
                ", sede='" + sede + '\'' +
                ", virtuale=" + virtuale +
                ", maxPartecipanti=" + maxPartecipanti +
                ", maxTeam=" + maxTeam +
                '}';
    }
}
