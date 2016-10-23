
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Fabian Witt
 */
public class Zustand {
    
    /** Symptom-ID */  
    private int id;
    /** Tumorbeschreibung */  
    private String tumor;
    /** Symptomliste */  
    private ArrayList<Symptom> symptome;

    /** Initialisiert das Zustands */
    public Zustand(int id, String tumor, ArrayList<Symptom> symptome) {
        this.id = id;
        this.tumor = tumor;
        this.symptome = symptome;
    }
    
    public String getZustandName(){
        return tumor;
    }

    public String getTumor() {
        return tumor;
    }

    public ArrayList<Symptom> getSymptome() {
        return symptome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public boolean isEqualId(Zustand z) {
        return z.getId() == this.id;
    }
    
    /** Gleichheit zweier Zustaende testen */
    public boolean isEqual(Zustand z) {
        
        boolean equal_1 = false, equal_2 = false, equal_3 = false;
        
        if(this.tumor.equals(z.getTumor()))
            equal_1 = true;

        if(this.symptome.size() == z.getSymptome().size())
            equal_2 = true;
        
        if(this.symptome.size() > 0 && z.getSymptome().size() > 0){
        
            ArrayList<Symptom> temp_symptome = new ArrayList<>(symptome);
            
            Iterator<Symptom> iter_2 = z.getSymptome().iterator();

            while(iter_2.hasNext()) {

                Symptom akt_iter_2 = iter_2.next();
                
                Iterator<Symptom> iter_1 = temp_symptome.iterator();

                while(iter_1.hasNext()) {

                    Symptom akt_iter_1 = iter_1.next();
                    
                    if(akt_iter_1 == null && akt_iter_2 == null){
                        iter_1.remove(); break;
                    } else if(akt_iter_1 == null || akt_iter_2 == null){
                         break; // nichts tun
                    } else if(akt_iter_1.isEqual(akt_iter_2)){
                        iter_1.remove(); break;
                    }
                }
            }

            if(temp_symptome.isEmpty())
                equal_3 = true;
        } else {
            equal_3 = true;
        }
        
        return equal_1 && equal_2 && equal_3;
    }
    
    /** gibt Symptomliste als String zurück */
    public String getSymptomString() {
        
        String erg = "";
        
        Iterator<Symptom> iter = symptome.iterator();
        
        while(iter.hasNext()) {
            
            Symptom next = iter.next();
            
            erg += next.getSymptom();
            
            if(iter.hasNext())
                erg = erg + ", ";
        }
        
        return erg;
    }  
    
    /** gibt tumorbeschreibung als String zurück, inkl id */
    public String getZustandBeschreibung() {
        return this.id + ": " + this.tumor;
    }
}
