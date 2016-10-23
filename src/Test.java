import java.util.ArrayList;

public class Test {
    
    public static void main(String[] args) {
        /** Anzahl der Sequenzen */
        int anzPatienten = 15;
        
        Data data = new Data( anzPatienten );
        /** Einlesen der Behandlungsprotokolle */
        data.init();
        
        /** Anlegen des HMM */
        HMM hmm = new HMM( data.getAnzahlZustaende(), data.getAnzahlMedikamente() );

        /** Berechnen der StartWkt */
        hmm.compute_pi( data.getZustandsLerndaten() );
        /** Berechnen der UebergangsWkt */
        hmm.compute_a( data.getZustandsLerndaten() );
        /** Berechnen der AusgabeWkt */
        hmm.compute_b( data.getMedikamenteLerndaten(), data.getZustandsLerndaten() );

        ArrayList<double[][]> whsMatrix = new ArrayList();
        
        double[] bewertungAusgabe = new double[ anzPatienten ];
        double[] bewertungUebergang = new double[ anzPatienten ];
        double[] sicherheitAusgabe = new double[ anzPatienten ];
        double[] sicherheitUebergang = new double[ anzPatienten ];
        
        /** Test: Vergleich zwischen Sequenz und Modell */
        for(int p = 1; p <= anzPatienten; p++){
            whsMatrix.add( hmm.testModellSequenzAbweichung( data.getPatientMediListNumById(p), data.getPatientZustandListNumById(p) ) );
        }

        /** Durchlaufen aller Sequenzen */
        for(int p = 0; p < anzPatienten; p++){
            
            for(int e = 0; e < whsMatrix.get(p).length; e++){
                
                /** Zustandsuebergang/Sicherheit */
                if( whsMatrix.get(p)[e][0] < whsMatrix.get(p)[e][2] ){
                    bewertungUebergang[ p ] += ( ( whsMatrix.get(p)[e][2] - whsMatrix.get(p)[e][0] ) / whsMatrix.get(p)[e][2] );
                } else {
                    bewertungUebergang[ p ] += 1;
                    if( ( whsMatrix.get(p)[e][2] - whsMatrix.get(p)[e][4] ) == 1)
                        sicherheitUebergang[ p ]++;
                }
                
                /** Ausgabe/Sicherheit */
                if( whsMatrix.get(p)[e][1] < whsMatrix.get(p)[e][3] ){
                    bewertungAusgabe[ p ] += ( ( whsMatrix.get(p)[e][3] - whsMatrix.get(p)[e][1] ) / whsMatrix.get(p)[e][3] );
                } else {
                    bewertungAusgabe[ p ] += 1;
                    if( ( whsMatrix.get(p)[e][3] - whsMatrix.get(p)[e][5] ) == 1)
                        sicherheitAusgabe[ p ]++;
                }
            }
        }
        
        double gesBewertungUebergang = 0;
        double gesBewertungAusgabe = 0;
        double gesSicherheitAusgabe = 0;
        double gesSicherheitUebergang = 0;
        int gesAnzahl = 0;
        
        for(int p = 0; p < anzPatienten; p++){
            
            gesBewertungUebergang += bewertungUebergang[ p ];
            gesBewertungAusgabe += bewertungAusgabe[ p ];
            gesSicherheitAusgabe += sicherheitAusgabe[ p ];
            gesSicherheitUebergang += sicherheitUebergang[ p ];
            gesAnzahl += whsMatrix.get(p).length;
        }
    }
}