import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.Complex;
import model.DataPoint;


public class Listen {

	public static void  determineKeyPoints(Map<Long, List<DataPoint>> hashMap,Complex[][] results,String fileName) {

		 double highscores[][];
		 long points[][];

		highscores = new double[results.length][5];
		points = new long[results.length][5];
		
		//init
		for (int i = 0; i < results.length; i++) {
			for (int j = 0; j < 5; j++) {
				highscores[i][j] = 0;
				points[i][j] = 0;
			}
		}
      //end_init
		
		for (int t = 0; t < results.length; t++) {
			
			for (int freq = Util.LOWER_LIMIT; freq < Util.UPPER_LIMIT - 1; freq++) {
				
				// Get the magnitude:
				double mag = Math.log(results[t][freq].abs() + 1);

				// Find out which range we are in:
				int index = Util.getIndex(freq);

				// Save the highest magnitude and corresponding frequency:
				if (mag > highscores[t][index]) {
					highscores[t][index] = mag;
					points[t][index] = freq;
				}
			}


			long h = Util.hash(points[t][0], points[t][1], points[t][2],points[t][3]);
	 
			 
			
			if (!hashMap.containsKey(h)) {
				
				List<DataPoint> listPoints = new ArrayList<DataPoint>();
				DataPoint point = new DataPoint(fileName, t);
				listPoints.add(point);
				
				hashMap.put(h, listPoints);
				
			} else {
				
				DataPoint point = new DataPoint( fileName, t);
				hashMap.get(h).add(point);
				
			}
			 
		
	}

 }	

	
}
