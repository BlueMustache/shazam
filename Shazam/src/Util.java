import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.TargetDataLine;

import model.Complex;
import model.DataPoint;
import model.FFT;


public class Util {

	public final static int UPPER_LIMIT = 300;
	public final static int LOWER_LIMIT = 40;
	public final static int[] RANGE = new int[] { 40, 80, 120, 180, UPPER_LIMIT + 1 };
	
	
	public static int getIndex(int freq) {
		int i = 0;
		while (RANGE[i] < freq)
			i++;
		return i;
	}
	
	public static long hash(long p1, long p2, long p3, long p4) {
		
	    final int FUZ_FACTOR = 2;
		
		return (p4 - (p4 % FUZ_FACTOR)) * 100000000 + (p3 - (p3 % FUZ_FACTOR))
				* 100000 + (p2 - (p2 % FUZ_FACTOR)) * 100
				+ (p1 - (p1 % FUZ_FACTOR));
	}
	
	
	   public static Complex[][] makeSpectrum(ByteArrayOutputStream out) {
			
           int CHUNK_SIZE = 4096;

			byte audio[] = out.toByteArray();
			int amountPossible = audio.length / CHUNK_SIZE;

			// When turning into frequency domain we'll need complex numbers:
			Complex[][] results = new Complex[amountPossible][];

			// For all the chunks:
			for (int times = 0; times < amountPossible; times++) {
				
				Complex[] complex = new Complex[CHUNK_SIZE];
				
				for (int i = 0; i < CHUNK_SIZE; i++) {
					// Put the time domain data into a complex number with imaginary
					// part as 0:
					complex[i] = new Complex(audio[(times * CHUNK_SIZE) + i], 0);
				}
				// Perform FFT analysis on the chunk:
				 
				results[times] = FFT.fft(complex);
				
	 
				
			}
			
	     return results;	
	 
		}

	   
		public static ByteArrayOutputStream readBuffer(int size,TargetDataLine line,AudioInputStream outDinSound){
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
		//	byte[] buffer = new byte[64];
            int c;
            int n = 0;
//			
//			
//		
//			boolean running = false;
//		 	
//			
 		byte[] buffer = new byte[(int) 1024];
//
 		try {
 			
// 			byte[] buffer = new byte[outDinSound.available()/size];	
// 			outDinSound.read(buffer);
// 			out.write(buffer);
 			while ((c = outDinSound.read(buffer, 0, buffer.length)) != -1) {
 				out.write(buffer, 0, c);
 				n++;
				if (n > size)
					break;
            }
 			
//            while ((c = outDinSound.read(buffer, 0, buffer.length)) != -1) {
//            	
//            	out.write(buffer, 0, c);
//            	
//				n++;
//				if (n > size)
//					break;
//          
//			 
//
//					int count = outDinSound.read(buffer, 0, 1024);
//					 
//					if (count > 0) {
//						out.write(buffer, 0, count);
//					}
//				 
//             }
           
			
				out.close();
				line.close();
				 
			} catch (IOException e) {
				System.err.println("I/O problems: " + e);
				System.exit(-1);
			}	
			
			return out;
		}		   
	   
		public static ByteArrayOutputStream readBuffer(TargetDataLine line,AudioInputStream outDinSound){
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
		//	byte[] buffer = new byte[64];
            int c;
            int n = 0;
//			
//			
//		
//			boolean running = false;
//		 	
//			
			byte[] buffer = new byte[4096];
//
 		try {
 			
// 			byte[] buffer = new byte[outDinSound.available()];	
// 			outDinSound.read(buffer);
// 			out.write(buffer);
 			
 			while ((c = outDinSound.read(buffer, 0, buffer.length)) != -1) {
 				out.write(buffer, 0, c);
            }
 			
//            while ((c = outDinSound.read(buffer, 0, buffer.length)) != -1) {
//            	
//            	out.write(buffer, 0, c);
//            	
//				
//		
//
//					int count = outDinSound.read(buffer, 0, 1024);
//					 
//					if (count > 0) {
//						out.write(buffer, 0, count);
//					}
//            }

           
			
				out.close();
				line.close();
				 
			} catch (IOException e) {
				System.err.println("I/O problems: " + e);
				System.exit(-1);
			}	
			
			return out;
		}	
	 

	public static AudioFormat getFormat() {
			
			float sampleRate = 44100;
			int sampleSizeInBits = 8;
			int channels = 1; // mono
			boolean signed = true;
			boolean bigEndian = true;
			return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);
	}
	   
	public static AudioFormat getDecodedFormat(AudioFormat baseFormat){
		
		AudioFormat decodedFormat = new AudioFormat(
													AudioFormat.Encoding.PCM_SIGNED,
													baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
													baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
													false);

		return decodedFormat;
	}
	
}
