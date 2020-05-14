package mlulsp.domain;



//import java.io.FileWriter;
//import java.io.PrintWriter;
//import java.util.Collection;
//import java.util.Vector;
//
//
//import java.util.Collection;
//import java.util.Random;


public class ProductionSchedule {

    public int genome[][]; 		// matrix
    public int production[][]; 	// prod-mengen
    public int onHold[][]; 		// lagermengen
    public int demand[][]; 		// bedarf
    public double tcost[][];
    public double cost[]; 		// kosten pro item
    //private int id;

    public void justInTime (){
        for (int i = 0; i < genome.length; i++) {
            for (int j = 0; j < genome[i].length; j++) {
            	this.genome[i][j] = 1;
            }
        }
    }
    public void bereinigen (){
		for (int i = 0; i < genome.length; i++) {
			for (int j = 0; j < genome[i].length; j++) {
				if(production[i][j] == 0){
					genome[i][j] = 0;
				}
			}
		}
    }
 
    
    /**
     * Create a new {@link ProductionSchedule}.
     * 
     * @param itemCount
     *            the number of items
     * @param periodCount
     *            the number of periods
     */
    
    public ProductionSchedule(int itemCount, int periodCount) {
        production = new int[itemCount][periodCount];
        onHold     = new int[itemCount][periodCount];
        demand     = new int[itemCount][periodCount];
        genome     = new int[itemCount][periodCount];
        tcost      = new double[itemCount][periodCount];
        cost       = new double[itemCount];
    }

    public ProductionSchedule(int[][] bits, Instance instance) {
    	int itemCount   = bits.length;
    	int periodCount = bits[0].length;
    	genome          = new int[itemCount][periodCount];
        production      = new int[itemCount][periodCount];
        onHold          = new int[itemCount][periodCount];
        demand          = new int[itemCount][periodCount];
       
        tcost           = new double[itemCount][periodCount];
        cost            = new double[itemCount];
        
        for(int i=0;i<bits.length;i++){
        	for(int j=0;j<bits[i].length;j++){
        		genome[i][j] = bits[i][j];
        	}
        }
        
//        genome = bits;
        
        instance.decodeMatrix(this);
        

    }

    
    
   
    public Double getCostSum() {
        double ret = 0;
        for (int i = 0; i < cost.length; i++) {
            ret += cost[i];
        }
        return ret;
    }
    
    
    public Double getCostsForItem(int itemKey) {
        return cost[itemKey - 1];
    }

    public void setCostsForItem(int itemKey, double costs) {
        cost[itemKey - 1] = costs;
    }

    public int[] getGenome(int itemKey) {
        return genome[itemKey - 1];
    }

    public int getGenomeForPeriod(int itemKey, int period) {
        return genome[itemKey - 1][period - 1];
    }

    public void setDemandForPeriod(int itemKey, int period, int amount) {
        this.demand[itemKey - 1][period - 1] = amount;
    }

    public int getDemandForPeriod(int itemKey, int period) {
        return this.demand[itemKey - 1][period - 1];
    }

    public void setBitInGenome(int itemKey, int period, int bit) {
        this.genome[itemKey - 1][period - 1] = bit;
    }

    public void setProduction(int itemKey, int period, int amount) {
        this.production[itemKey - 1][period - 1] = amount;
    }

    public void setOnHold(int itemKey, int period, int amount) {
        this.onHold[itemKey - 1][period - 1] = amount;
    }

    public int getProduction(int itemKey, int period) {
        return this.production[itemKey - 1][period - 1];
    }

    public int getOnHold(int itemKey, int period) {
        return this.onHold[itemKey - 1][period - 1];
    }

    public void setGenome(int[][] matrix) {
        this.genome = matrix;
    }

    // dummy method for teh testing. remove if testing is over :)
//    public void printToConsole() {
//        System.out.println("demand");
//        for (int i = 0; i < demand.length; i++) {
//            System.out.println("Item: " + (i + 1));
//            for (int j = 0; j < demand[0].length; j++) {
//                System.out.print(demand[i][j] + ",");
//            }
//            System.out.println("");
//        }
//        System.out.println("production (on hold)");
//        for (int i = 0; i < production.length; i++) {
//            System.out.println("Item: " + (i + 1));
//            for (int j = 0; j < production[0].length; j++) {
//                System.out.print(production[i][j] + " (");
//                System.out.print(onHold[i][j] + "), ");
//            }
//            System.out.println("");
//        }
//        System.out.println("kosten:");
//        for (int i = 0; i < genome.length; i++) {
//            System.out.println("Item: " + (i + 1) + " -> " + cost[i]);
//        }
//
//    }
//
//    public String controlDump() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(getCostSum()+"\n");
//        for (int item = 0; item < demand.length; item++) {
//            for (int period = 0; period < demand[item].length; period++) {
//                sb.append(demand[item][period]);
//                sb.append(" ");
//            }
//            sb.deleteCharAt(sb.length()-1);
//            sb.append("\n");
//        }
//        return sb.toString();
//    }
//    
//    public String bitDump() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(getCostSum()+"\n");
//        for (int item = 0; item < genome.length; item++) {
//            for (int period = 0; period < genome[item].length; period++) {
//                sb.append(genome[item][period]);
//                sb.append(" ");
//            }
//            sb.deleteCharAt(sb.length()-1);
//            sb.append("\n");
//        }
//        return sb.toString();
//    }
//
//    public void dump() {
//        StringBuffer sb = new StringBuffer();
//        for (int item = 0; item < genome.length; item++) {
//            for (int period = 0; period < genome[item].length; period++) {
//                sb.append(genome[item][period]);
//            }
//            sb.append("\n");
//        }
//        System.out.println(sb);
//    }
//    
//    public void stamp(int id){
//        this.id = id;
//    }
//    
//    public int getStamp(){
//        return id;
//    }
}
