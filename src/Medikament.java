
/**
 *
 * @author Fabian
 */
public class Medikament {
    
    /** Medikamenten-ID */  
    private int id;
    /** Medikamentenname */  
    private String name;

    /** Initialisiert das Medikaments */
    public Medikament(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }
}
