
import java.util.ArrayList;
import java.util.Iterator;

public class HMM {
    /** Anzahl der Zustaende */
    private final int numStates;
    /** Groesse des Ausgabealphabets */
    private final int sigmaSize;
    
    /** Startwahrscheinlichkeiten */
    public double pi[];
    /** Uebergangswahrscheinlichkeiten */
    public double a[][];
    /** Ausgabewahrscheinlichkeiten */
    public double b[][];

    /** Initialisiert das HMM */
    public HMM(int numStates, int sigmaSize) {
        this.numStates = numStates;
        this.sigmaSize = sigmaSize;
        
        pi = new double[numStates];
        a = new double[numStates][numStates];
        b = new double[numStates][sigmaSize];
    }

    public double[] getPi() {
        return pi;
    }

    public double[][] getA() {
        return a;
    }

    public double[][] getB() {
        return b;
    }
    
    public void compute_a(ArrayList<Integer[]> z) {
        /** 2-Dim-Array fuer Haeufigkeitszaehlung */
        int a_num[][] = new int[numStates][numStates];
        /** Array fuer Zeilensummen zur Normalisierung */
        int a_denom[] = new int[numStates];

        /** Durchlaufen der Trainingssequenzen */
        Iterator<Integer[]> iter_z = z.iterator();

        while(iter_z.hasNext()) {

            Integer[] akt_z = iter_z.next();

            for(int j = 0; j < akt_z.length - 1; j++){
                /** Haeufigkeit + 1, fuer Zustandswechsel akt_z[ j ] in akt_z[ j+1 ] */
                a_num[ akt_z[ j ] ][ akt_z[ j+1 ] ] ++;
            }
        }
        /** Berechnen der Zeilensummen der Variable a_num */
        for(int i = 0; i < numStates; i++){

            for(int j = 0; j < numStates; j++){
                a_denom[ i ] += a_num[ i ][ j ];
            }
        }
        /** Normalisieren der Werte */
        for(int i = 0; i < numStates; i++){

            for(int j = 0; j < numStates; j++){
                a[ i ][ j ] = divide( a_num[ i ][ j ], a_denom[ i ]);
            }
        }
    }
    
    public void compute_b(ArrayList<Integer[]> o, ArrayList<Integer[]> z) {
        /** 2-Dim-Array fuer Haeufigkeitszaehlung */
        int b_num[][] = new int[numStates][sigmaSize];
        /** Array fuer Zeilensummen zur Normalisierung */
        int b_denom[] = new int[numStates];

        /** Durchlaufen der Trainingssequenzen */
        Iterator<Integer[]> iter_o = o.iterator();
        Iterator<Integer[]> iter_z = z.iterator();

        while(iter_o.hasNext() && iter_z.hasNext()) {

            Integer[] akt_o = iter_o.next();
            Integer[] akt_z = iter_z.next();

            for(int j = 0; j < akt_o.length; j++){
                /** Haeufigkeit + 1, fuer Ausgabe akt_o[ j ] in Zustand akt_z[ j ] */
                b_num[ akt_z[ j ] ][ akt_o[ j ] ] ++;
            }
        }
        /** Berechnen der Zeilensummen der Variable b_num */
        for(int i = 0; i < numStates; i++){

            for(int j = 0; j < sigmaSize; j++){
                b_denom[ i ] += b_num[ i ][ j ];
            }  
        }
        /** Normalisieren der Werte */
        for(int i = 0; i < numStates; i++){

            for(int j = 0; j < sigmaSize; j++){
                b[ i ][ j ] = divide( b_num[ i ][ j ], b_denom[ i ]);
            }
        } 
    }
    
    public void compute_pi(ArrayList<Integer[]> z) {
        /** Array fuer Haeufigkeitszaehlung */
        int pi_num[] = new int[numStates];
        /** Anzahl der Startzustaende bzw Trainingssequenz zur Normalisierung */
        int pi_denum = z.size();
        
        /** Durchlaufen der Trainingssequenzen */
        Iterator<Integer[]> iter_z = z.iterator();
        
        while(iter_z.hasNext()) {

            Integer[] akt_bsp = iter_z.next();
            /** Haeufigkeit + 1, fuer jeden ersten Zustand der Sequenz */
            pi_num[ akt_bsp[ 0 ] ] ++;
        }
        /** Normalisieren der Werte */
        for(int i = 0; i < numStates; i++){

            pi[ i ] = divide( pi_num[ i ], pi_denum); 
        }
    }
    /** Dividiert n durch d => 0 / 0 = 0! */
    public double divide(double n, double d) {
        if (d == 0)
          return 0;
        else
          return n / d;
    }
    
    public double[][] testModellSequenzAbweichung(Integer[] o, Integer[] z){

        if(o.length != z.length)
            System.err.println("ERROR: testModellSequenzAbweichung - o OR z length");

        double[][] whsMatrix = new double[z.length][ 6 ];

        /** Startwahrscheinlichkeit nach Sequenz */
        whsMatrix[ 0 ][ 0 ] = pi[ z[ 0 ] ];
        /** Ausgabewahrscheinlichkeit nach Sequenz */
        whsMatrix[ 0 ][ 1 ] = b[ z[ 0 ] ][ o[ 0 ] ];

        /** Startwahrscheinlichkeit nach Modell */
        whsMatrix[ 0 ][ 2 ] = getMaxPi();
        /** Ausgabewahrscheinlichkeit nach Modell */
        whsMatrix[ 0 ][ 3 ] = getMaxB( z[ 0 ] );

        /** zweitgroesste Startwahrscheinlichkeit nach Modell */
        whsMatrix[ 0 ][ 4 ] = getSecondMaxPi();
        /** zweitgroesste Ausgabewahrscheinlichkeit nach Modell */        
        whsMatrix[ 0 ][ 5 ] = getSecondMaxB( z[ 0 ] );


        for(int e = 1; e < z.length; e++){
            /** Uebergangswahrscheinlichkeit nach Sequenz */
            whsMatrix[ e ][ 0 ] = a[ z[ e-1 ] ][ z[ e ] ];
            /** Ausgabewahrscheinlichkeit nach Sequenz */
            whsMatrix[ e ][ 1 ] = b[ z[ e ] ][ o[ e ] ];

            /** Uebergangswahrscheinlichkeit nach Modell */
            whsMatrix[ e ][ 2 ] = getMaxA( z[ e-1 ] );
            /** Ausgabewahrscheinlichkeit nach Modell */
            whsMatrix[ e ][ 3 ] = getMaxB( z[ e ] );

            /** zweitgroesste Uebergangswahrscheinlichkeit nach Modell */
            whsMatrix[ e ][ 4 ] = getSecondMaxA( z[ e-1 ] );
            /** zweitgroesste Ausgabewahrscheinlichkeit nach Modell */
            whsMatrix[ e ][ 5 ] = getSecondMaxB( z[ e ] );
        }
        return whsMatrix;
    }
    /** Ermittelt maximale StartWkt */
    private double getMaxPi(){

        double maxPi = -1;

        for(int e = 0; e < pi.length; e++){
            if(pi[ e ] > maxPi)
                maxPi = pi[ e ];
        }

        return maxPi;
    }
    /** Ermittelt zweitgroesste StartWkt */
    private double getSecondMaxPi(){

        double maxPi = -1;

        for(int e = 0; e < pi.length; e++){
            if( pi[ e ] > maxPi && pi[ e ] < getMaxPi() )
                maxPi = pi[ e ];
        }

        return maxPi;    
    }
    /** Ermittelt maximale UebergangsWkt im Zustand z */
    private double getMaxA(int z){

        double maxA = -1;

        for(int e = 0; e < a[ z ].length; e++){
            if(a[ z ][ e ] > maxA)
                maxA = a[ z ][ e ];
        }

        return maxA;   
    }
    /** Ermittelt zweitgroesste UebergangsWkt im Zustand z */
    private double getSecondMaxA(int z){

        double maxA = -1;

        for(int e = 0; e < a[ z ].length; e++){
            if( a[ z ][ e ] > maxA && a[ z ][ e ] < getMaxA( z ) )
                maxA = a[ z ][ e ];
        }

        return maxA;   
    }
    /** Ermittelt maximale AusgabeWkt im Zustand z */
    private double getMaxB(int z){

        double maxB = -1;

        for(int e = 0; e < b[ z ].length; e++){
            if(b[ z ][ e ] > maxB)
                maxB = b[ z ][ e ];
        }

        return maxB;   
    }
    /** Ermittelt zweitgroesste AusgabeWkt im Zustand z */
    private double getSecondMaxB(int z){

        double maxB = -1;

        for(int e = 0; e < b[ z ].length; e++){
            if( b[ z ][ e ] > maxB && b[ z ][ e ] < getMaxB( z ) )
                maxB = b[ z ][ e ];
        }

        return maxB;   
    }
    /** gibt das Array pi aus */
    public String getPiString() {
        
        String erg = "pi = \n";
        
        for(int i = 0; i < numStates; i++){
            erg += pi[i] + " ";
        }
        
        return erg + "\n";
    }
    
    /** gibt die Matrix A aus */   
    public String getAString() {
        
        String erg = "a = \n";
        
        for(int i = 0; i < numStates; i++){
            
            for(int j = 0; j < numStates; j++) {
                erg += a[i][j] + " ";
            }
            
            erg += "\n";
        }
        
        return erg;
    }
    
    /** gibt die Matrix B aus */  
    public String getBString() {
        
        String erg = "b = \n";
        
        for(int i = 0; i < numStates; i++){
            
            for(int j = 0; j < sigmaSize; j++) {
                erg += b[i][j] + " ";
            }
            
            erg += "\n";
        }
        
        return erg;
    }
}
