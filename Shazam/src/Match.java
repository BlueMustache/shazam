import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Complex;
import model.DataPoint;


public class Match {

	public static void determineKeyPoints(Map<Long, List<DataPoint>> hashMap,Map<String, Map<Integer, Integer>> matchMap,Complex[][] results) {
 
		double highscores[][];
		long points[][];

		highscores = new double[results.length][5];
		
		//init
		for (int i = 0; i < results.length; i++) {
			for (int j = 0; j < 5; j++) {
				highscores[i][j] = 0;
			}
		}

		points = new long[results.length][5];
		for (int i = 0; i < results.length; i++) {
			for (int j = 0; j < 5; j++) {
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

           matching(hashMap,matchMap,h, t);
				
	}
		

	}	
	
	private static void matching(Map<Long, List<DataPoint>> hashMap,Map<String, Map<Integer, Integer>> matchMap,long hash, int t){
		
		List<DataPoint> listPoints;

		if ((listPoints = hashMap.get(hash)) != null) {
			
			for (DataPoint dP : listPoints) {
				
				int offset = Math.abs(dP.getTime() - t);
				
				Map<Integer, Integer> tmpMap = new HashMap<Integer, Integer>();
				
				if ((tmpMap = matchMap.get(dP.getSongId())) == null) {
					
					tmpMap = new HashMap<Integer, Integer>();
					tmpMap.put(offset, 1);
					matchMap.put(dP.getSongId(), tmpMap);
					
				} else {
					
					Integer count = tmpMap.get(offset);
					if (count == null) {
						tmpMap.put(offset, new Integer(1));
					} else {
						tmpMap.put(offset, new Integer(count + 1));
					}
					
				}
			}
			
			 
		}
		
		
		
	} 
	
}
