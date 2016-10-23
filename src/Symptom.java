
/**
 *
 * @author Fabian Witt
 */
public class Symptom {
    
    /** Symptom-ID */  
    private int id;
    /** Symptom-firstName */  
    private String firstName;
    /** Symptom-secondName */  
    private String secondName;
    /** Symptom-beschreibung */  
    private String beschreibung;

    /** Initialisiert das Symptoms */
    public Symptom(int id, String firstName, String secondName, String beschreibung) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.beschreibung = beschreibung;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }
    
    public String getSecondName() {
        return secondName;
    }

    public String getBeschreibung() {
        return beschreibung;
    }
    
    /** gibt Symptomals String zurück */
    public String getSymptom(){
        
        String ausgabe = firstName + secondName;
        
        if(!beschreibung.isEmpty())
            ausgabe += '_' + beschreibung;
        
        return ausgabe;
    }
    
    /** Gleichheit zweier Symptome testen */
    public boolean isEqual(Symptom s) {

        return this.firstName.equals(s.getFirstName()) &&
               this.secondName.equals(s.getSecondName()) &&
               this.beschreibung.equals(s.getBeschreibung());
    }
    
}
