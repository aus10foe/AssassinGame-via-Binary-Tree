import java.lang.Math;

public class SOC {
   private int[] bounds;

   

   public static void main(String args[]) {
      int state_of_charge;
      state_of_charge = soc_task();
      System.out.println(state_of_charge);
   }
   
   public static int soc_task() {
      /*
           Temp | Voltage
                   200     250     300     350     400
           -10     0       10      35      100     100
           0       0       0       20      80      100
           25      0       0       10      60      100
           45      0       0       0       50      100
       */
       
       int[][] SOC_array  =    {{0, 10, 35, 100, 100}, {0,  0, 20,  80, 100}, {0,  0, 10,  60, 100}, {0,  0,  0,  50, 100}};
                              
       int[] temp_array     = {-10, 0, 25, 45};            // an array of known temperatures. 
       int[] voltage_array = {200, 250, 300, 350, 400};   // an array of known voltages
       
       /*
         Vo = Vt + R*I // find open circuit voltage
       */
       
       int openCirVolt;            // The open circuit voltage is our target X parameter.
       double tempV;               // temp variable to store voltage
       
       double HV_voltage = 270;  // global variable
       double HV_current = 3;      // global variable
       int temp = 2;              // global variable
       int SOC;
              
       tempV = HV_voltage + (0.5 * (HV_current));  // dependent on physical data readings for voltage and current.
       openCirVolt = (int) Math.round(tempV);                 // round to int
   
       if (openCirVolt < 200 || openCirVolt > 400 ||
           temp < -10 || temp > 45) {
           // Temp out of bounds throw exception;
       }
       
       int[] bounds;
       bounds = findBounds(temp_array, voltage_array, openCirVolt, temp);   // find the four bounds surrounding targetX
       SOC = biLinearInterp(SOC_array, openCirVolt, temp, bounds);        // interpolatation
              // update SOC
       return SOC;
   }


// Searches for Horizontal and Vertical bounds in a restricted 4x5 matrix
// for Targe Values x and y. 
  public static int[] findBounds(int[] y, int[] x, int tarX, int tarY) {
       double distanceX = tarX - x[0]; // initial voltage distance
       double distanceY = tarY - y[0]; // initial temp distance
       double minX = distanceX;        // set min
       double minY = distanceY;
       int minX_index = 0;             // set min index
       int minY_index = 0;
       int[] result = new int[6];                  // initialize result
       System.out.println("tarX = " + tarX);
       System.out.println("tarY = " + tarY);
 


   
       // Search for Horizontal Bounds
       if (tarX == x[4]) {
           // check for edge case, if tarX = 400 bounds must be on the edge
           minX_index = 3;
       } else {
           for(int i = 1; tarX >= x[i]; i++) { // loop until you find shortest distance
               distanceX = tarX - x[i];
               if (distanceX < minX) {
                   // update min;
                   // update min_index
                   minX = distanceX;
                   minX_index = i;
               }
           }
       }
       // Search for Vertical Bounds
       if (tarY == y[3]) {
           // check for edge case;
           minY_index = 2;
       } else {
           for(int j = 1; y[j] <= tarY; j++) {
               distanceY = tarY - y[j];
               if (distanceY < minY) {
                   // update min;
                   // update min_index
                   minY = distanceY;
                   minY_index = j;
               }
           }
       }
       
       result[0] = x[minX_index];
       result[1] = y[minY_index];
       result[2] = x[minX_index + 1];
       result[3] = y[minY_index + 1];
       result[4] = minX_index;
       result[5] = minY_index;
   
   
     
//        for(int i = 0; i < 6; i++) { 
//          bounds[i] = result[i];   // update global variable bounds
//        }
      return result; 
   }

   public static int biLinearInterp(int[][] chart, int tarX, int tarY, int[] bounds){
       // unpack bounds
   
       int x1 = bounds[0];
       int y1 = bounds[2];
       int x2 = bounds[1];
       int y2 = bounds[3];
       int minX_index = bounds[4];
       int minY_index = bounds[5];
       System.out.println("minX_index = " + minX_index);
       System.out.println("minY_index = " + minY_index);
      
       // do interpolation
       double firstWeight;     // Weight at f(x, y1)
       double secondWeight;    // Weight at f(x, y2)
       double tarWeight;       // Weight at target point f(x,y) = SOC
       
       firstWeight  = (((x2 - tarX) / (x2 - x1)) * chart[minY_index][minX_index])
                    + (((tarX - x1) / (x2 - x1)) * chart[minY_index + 1][minX_index]);
   
       secondWeight = (((x2 - tarX) / (x2 - x1)) * chart[minY_index][minX_index + 1])
                    + (((tarX - x1) / (x2 - x1)) * chart[minY_index + 1][minX_index + 1]);
   
       tarWeight    = (((y2 - tarY) / (y2 - y1)) * firstWeight)
                    + (((tarY - y1) / (y2 - y1)) * secondWeight);
   
       int result = (int) Math.round(tarWeight); // round off new SOC to int
   
       return result;
   

   }   
}