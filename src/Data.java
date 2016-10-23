
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Data {
    
    private final int anzPatienten;
    private int nextZustandId;
    
    private final HashSet<String> gesMediStringList;
    private final HashSet<String> gesSymptomStringList;
    
    private final List<Medikament> gesMediList;
    private final List<Symptom> gesSymptomList;
    private final List<Zustand> gesZustandList;
    
    private final Map<Integer, ArrayList<Medikament>> patientMediList;
    private final Map<Integer, ArrayList<Zustand>> patientZustandList;
    
    private final Map<Integer, ArrayList<Integer>> patientMediListNum;
    private final Map<Integer, ArrayList<Integer>> patientZustandListNum;

    public Data(int anzPatienten) {
        
        this.anzPatienten = anzPatienten;
        this.nextZustandId = 0;
        
        this.gesMediStringList = new HashSet<>();
        this.gesSymptomStringList = new HashSet<>();
        this.gesMediList = new ArrayList<>();
        this.gesSymptomList = new ArrayList<>();
        this.gesZustandList = new ArrayList<>();
        this.patientMediList = new HashMap<>();
        this.patientZustandList = new HashMap<>();
        this.patientMediListNum = new HashMap<>();
        this.patientZustandListNum = new HashMap<>();
    }
    
    public void init(){
        loadMediData();
        
        loadSymptomData();
        
        loadZustandData();
        
        createPatientMediList();

        createPatientZustandList();

        convertData();
    }
    
    public void addZustand(String tumor, ArrayList<Symptom> symptome){
        gesZustandList.add(new Zustand(nextZustandId, tumor, symptome));
                
        nextZustandId++;
    }
    
    private void createPatientMediList() {
        
        for(int p = 1; p <= this.anzPatienten; p++){
            
            ArrayList<String> patientMediStringList = new ArrayList<>();
            ArrayList<Medikament> patientMidikamentList = new ArrayList<>();
            
            patientMediStringList.addAll(readMidikamentData("uebergaenge_p_" + p + ".csv"));
            
            Iterator<String> iter_1 = patientMediStringList.iterator();
            
            while(iter_1.hasNext()){
                
                String akt_iter_1 = iter_1.next();
            
                Iterator<Medikament> iter_2 = gesMediList.iterator();

                while(iter_2.hasNext()){
                    Medikament akt_iter_2 = iter_2.next();

                    if(akt_iter_2.getName().equals(akt_iter_1)){
                        patientMidikamentList.add(akt_iter_2);
                    }
                }
            }
            this.patientMediList.put(p, patientMidikamentList);
        } 
    }
    
    private void createPatientZustandList() {
            
        for(int p = 1; p <= this.anzPatienten; p++){
            
            ArrayList<Zustand> patientZustandList = new ArrayList<>();
            
            ArrayList<String[]> patientenZustandString = readZustandData("uebergaenge_p_" + p + ".csv");
            
            Iterator<String[]> iter_elemente = patientenZustandString.iterator();
            
            
            while(iter_elemente.hasNext()){
                
                String[] akt_element = iter_elemente.next();
                
                ArrayList<Symptom> symptomList = new ArrayList<>();
                
                for(int s = 1; s < akt_element.length; s++){
                    
                    if(!akt_element[s].isEmpty()){
                        String[] splitSymptom = splitSymptom(akt_element[s]); 
                        symptomList.add(getSymptomByParameter(splitSymptom[0], splitSymptom[1], splitSymptom[2]));
                    }
                }
                
                Zustand tempZustand = new Zustand(0, akt_element[0], symptomList);

                Iterator<Zustand> iter_zust = gesZustandList.iterator();
                
                while(iter_zust.hasNext()){
                    
                    Zustand akt_zust = iter_zust.next();
                    
                    if(akt_zust.isEqual(tempZustand))
                        patientZustandList.add(akt_zust);
                }
            }
            this.patientZustandList.put(p, patientZustandList);
        }    
    }
    
    private void convertData() {
        
        for(int p = 1; p <= this.anzPatienten; p++){
            
            ArrayList<Integer> zustandListNum = new ArrayList<>();
            
            Iterator<Zustand> iter = patientZustandList.get(p).iterator();
            
            while(iter.hasNext()){
                Zustand akt_zustand = iter.next();
                
                zustandListNum.add(akt_zustand.getId()); 
            }
            
            patientZustandListNum.put(p, zustandListNum);
            
            
            ArrayList<Integer> mediListNum = new ArrayList<>();
            
            ArrayList<Medikament> mediList = patientMediList.get(p);
            
            Iterator<Medikament> iter_medis = mediList.iterator();
            
            while(iter_medis.hasNext()){
                Medikament akt_medi = iter_medis.next();
                
                mediListNum.add(akt_medi.getId()); 
            }
            
            patientMediListNum.put(p, mediListNum);            
        } 
    }
   
    private Symptom getSymptomByParameter(String firstName, String secondName, String beschreibung){
        
        Iterator<Symptom> iter = gesSymptomList.iterator();
        
        while(iter.hasNext()){
            Symptom akt_symptom = iter.next();
            
            if(akt_symptom.getFirstName().equals(firstName) && 
                akt_symptom.getSecondName().equals(secondName) &&
                    akt_symptom.getBeschreibung().equals(beschreibung)){
                return akt_symptom;
            }
        }
        
        return null;
    }
    
    private String[] splitSymptom(String symptom){
        
        String[] erg = new String[3];
        
        String[] splitSymptom_1 = symptom.split("_");
        
        String[] splitSymptom_2 = splitSymptom_1[0].split(":");
        
        erg[0] = splitSymptom_2[0];
        
        if(splitSymptom_2.length == 1){
            erg[1] = "";
        } else {
            erg[1] = splitSymptom_2[1];
        }
        
        erg[2] = splitSymptom_1[1];
        
        return erg;
    }

    private void loadSymptomData() {
        
        for(int p = 1; p <= this.anzPatienten; p++){
            
            ArrayList<String[]> symptomList = readSymptomData("uebergaenge_p_" + p + ".csv");
            
            Iterator<String[]> iter_symptome = symptomList.iterator();
            
            while(iter_symptome.hasNext()){
                String[] akt_symptom_array = iter_symptome.next();
                
                for(int s = 0; s < akt_symptom_array.length; s++){
                    
                    if(!akt_symptom_array[s].isEmpty()){
                        gesSymptomStringList.add(akt_symptom_array[s]);
                    }
                }
            }
        }
        
        Iterator<String> symptom_iter = gesSymptomStringList.iterator();
        
        int aktSymptomId = 0;
        while(symptom_iter.hasNext()){
            String akt_symptom_iter = symptom_iter.next();
            
            String[] splitSymptom = akt_symptom_iter.split("_");
            
            gesSymptomList.add(new Symptom(aktSymptomId, splitSymptom[0], "", splitSymptom[1]));
            
            aktSymptomId++;
        }
    }

    private void loadMediData() {
        
        for(int p = 1; p <= this.anzPatienten; p++){
            gesMediStringList.addAll(readMidikamentData("uebergaenge_p_" + p + ".csv"));
        }
        
        Iterator<String> iter = gesMediStringList.iterator();
        
        int aktMediId = 0;
        while(iter.hasNext()){
            String akt_iter = iter.next();
            
            gesMediList.add(new Medikament(aktMediId, akt_iter));
            
            aktMediId++;
        }
    }
    
    private void loadZustandData() {
        
        for(int p = 1; p <= this.anzPatienten; p++){
            
            ArrayList<String[]> patientenZustandString = readZustandData("uebergaenge_p_" + p + ".csv");
            
            Iterator<String[]> iter_elemente = patientenZustandString.iterator();
            
            while(iter_elemente.hasNext()){
                
                String[] akt_element = iter_elemente.next();
                        
                ArrayList<Symptom> symptomList = new ArrayList<>();
                
                for(int s = 1; s < akt_element.length; s++){
                    
                    if(!akt_element[s].isEmpty()){
                        String[] splitSymptom = splitSymptom(akt_element[s]);
                        symptomList.add(getSymptomByParameter(splitSymptom[0], splitSymptom[1], splitSymptom[2]));
                    }
                }
                
                Zustand tempZustand = new Zustand(this.nextZustandId, akt_element[0], symptomList);

                Iterator<Zustand> iter_zust = gesZustandList.iterator();

                boolean is_in_list = false;
                
                while(iter_zust.hasNext()){
                    
                    Zustand akt_zust = iter_zust.next();
                    
                    if(akt_zust.isEqual(tempZustand))
                        is_in_list = true;
                }
                
                if(is_in_list == false){
                    gesZustandList.add(tempZustand);
                    this.nextZustandId++;
                }
            }
        }
    }
 
    public void printMediList() {
        
        System.out.println("Anzahl Medikamente: " + gesMediList.size());
        
        Iterator<Medikament> iter = gesMediList.iterator();
        
        while(iter.hasNext()){
            
            Medikament akt_medi = iter.next();
            
            System.out.println(akt_medi.getId() + ": " + akt_medi.getName());
        }
    }
    
    public void printSymptomList() {
        
        System.out.println("Anzahl Symptome: " + gesSymptomList.size());
        
        Iterator<Symptom> iter = gesSymptomList.iterator();
        
        while(iter.hasNext()){
            
            Symptom akt_symptom = iter.next();
            
            System.out.println(akt_symptom.getId() + ": " + akt_symptom.getSymptom());
        }
    }
    
    public void printZustandList() {
        
        System.out.println("Anzahl Zustände: " + gesZustandList.size());
        
        Iterator<Zustand> iter = gesZustandList.iterator();
        
        while(iter.hasNext()){
            
            Zustand akt_zustand = iter.next();
            
            String ausgabe = akt_zustand.getId() + ": " + akt_zustand.getTumor();
            
            if(akt_zustand.getSymptome().size() > 0)
                ausgabe += " - " + akt_zustand.getSymptomString();
            
            System.out.println(ausgabe);
        }
    }

    public List<Medikament> getGesMediList() {
        return gesMediList;
    }

    public List<Symptom> getGesSymptomList() {
        return gesSymptomList;
    }

    public List<Zustand> getGesZustandList() {
        return gesZustandList;
    }
    
    public String[] getZustandList(){
        
        String[] list = new String[gesZustandList.size()];
        
        Iterator<Zustand> iter = gesZustandList.iterator();
        
        while(iter.hasNext()){
            Zustand akt_zustand = iter.next();
            
            list[akt_zustand.getId()] = akt_zustand.getZustandBeschreibung();
        }
        
        return list;
    }
    
    public String[] getMediList(){
        
        String[] list = new String[gesMediList.size()];
        
        Iterator<Medikament> iter = gesMediList.iterator();
        
        while(iter.hasNext()){
            Medikament akt_zustand = iter.next();
            
            list[akt_zustand.getId()] = akt_zustand.getName();
        }
        
        return list;
    }
    
    public int getAnzahlZustaende(){
        return gesZustandList.size();
    }
    
    public int getAnzahlMedikamente(){
        return gesMediList.size();
    }
    
    public Integer[] getPatientMediListNumById(int patientId){
        
        return patientMediListNum.get(patientId).toArray(new Integer[patientMediListNum.get(patientId).size()]);
    }
    
    public Integer[] getPatientZustandListNumById(int patientId){
        
        return patientZustandListNum.get(patientId).toArray(new Integer[patientZustandListNum.get(patientId).size()]);
    }

    public Map<Integer, ArrayList<Integer>> getPatientMediListNum() {
        return patientMediListNum;
    }

    public Map<Integer, ArrayList<Integer>> getPatientZustandListNum() {
        return patientZustandListNum;
    }

    public ArrayList<Medikament> getMedikamenteByPatientenId(int id){
        
        Iterator iter = patientMediList.entrySet().iterator();
        
        while(iter.hasNext()){
            
            Map.Entry akt_patient = (Map.Entry)iter.next();
            
            if((int)akt_patient.getKey() == id){
                return (ArrayList<Medikament>)akt_patient.getValue();
            }
        }
        
        return null;
    }
    
    public ArrayList<Integer[]> getMedikamenteLerndaten(){

        ArrayList<Integer[]> list = new ArrayList<>();
        
        for(int p = 1; p <= this.anzPatienten; p++){
            list.add(patientMediListNum.get(p).toArray(new Integer[patientMediListNum.get(p).size()]));
        }
        
        return list; 
    }
    
    public ArrayList<Integer[]> getZustandsLerndaten(){
        
        ArrayList<Integer[]> list = new ArrayList<>();
        
        for(int p = 1; p <= this.anzPatienten; p++){
            list.add(patientZustandListNum.get(p).toArray(new Integer[patientZustandListNum.get(p).size()]));
        }
        
        return list;        
    }
    
    private ArrayList<String[]> readSymptomData(String dateiname) {
        
        ArrayList<String[]> list = new ArrayList<>();
        String csvFile = "/Users/Fabian/Box Sync/BA-Arbeit/TumorAusw/uebergaenge/" + dateiname;
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ";";
        
        try {
		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {
                        
                        if(!line.isEmpty()){
                            String[] ges_list = line.split(cvsSplitBy);

                            String[] symptome = new String[ges_list.length - 2]; 

                            //Medikament und Tumorart entfernen
                            for(int i = 2; i < ges_list.length; i++){

                                symptome[ i-2 ] = ges_list[ i ];
                            }

                            list.add(symptome);
                        }
		}
 
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
        return list;
    }
    
    private ArrayList<String> readMidikamentData(String dateiname) {
        
        ArrayList<String> list = new ArrayList<>();
        String csvFile = "/Users/Fabian/Box Sync/BA-Arbeit/TumorAusw/uebergaenge/" + dateiname;
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ";";

        try {
		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {
 
                        if(!line.isEmpty()){
                            String[] ges_list = line.split(cvsSplitBy);

                            list.add(ges_list[0]);
                        }
		}
 
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
        return list;
    }
   
    private ArrayList<String[]> readZustandData(String dateiname) {
        
        ArrayList<String[]> list = new ArrayList<>();
        String csvFile = "/Users/Fabian/Box Sync/BA-Arbeit/TumorAusw/uebergaenge/" + dateiname;
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ";";
        
        try {
		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {

                    String[] ges_list = line.split(cvsSplitBy);

                    String[] zustand = new String[ges_list.length - 1]; 

                    //Medikament und Tumorart entfernen
                    for(int i = 1; i < ges_list.length; i++){

                        zustand[ i-1 ] = ges_list[ i ];
                    }

                    list.add(zustand);
		}
 
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
        return list;
    }
}
