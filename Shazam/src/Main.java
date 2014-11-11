import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import model.Complex;
import model.DataPoint;

import org.tritonus.sampled.convert.PCM2PCMConversionProvider;
 
public class Main {

	private static Map<Long, List<DataPoint>> hashMap;
	private static Map<String, Map<Integer, Integer>> matchMap;
	

	
 	
	public static void main(String[] args) {
 
		hashMap = new Hashtable<Long, List<DataPoint>>();
		matchMap = new Hashtable<String, Map<Integer, Integer>>();
 
		
		String filePath1 ="E:/musicas/teste.mp3";
		String filePath2 ="E:/musicas/teste2.mp3";
		String filePath3 ="E:/musicas/teste3.mp3";
		String filePath4 ="E:/musicas/teste4.mp3";
		
		try {   
			Date dataInicial = new Date();
			
			listenSound(filePath1,"filePath1");
			System.out.println((new Date().getTime() - dataInicial.getTime())+" Escutou:"+filePath1);

			dataInicial = new Date();
			listenSound(filePath2,"filePath2");
			System.out.println((new Date().getTime() - dataInicial.getTime())+" Escutou:"+filePath2);

			dataInicial = new Date();
			listenSound(filePath3,"filePath3");
			System.out.println((new Date().getTime() - dataInicial.getTime())+" Escutou:"+filePath3);
 
			matchingSound(filePath2);
			
//			nrSong++;
//			listenSound(filePath4,3,true);
			
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			 
			e.printStackTrace();
		}
	}

	private static void matchingSound(String filePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		
		AudioFormat formatTmp = null;
		TargetDataLine lineTmp = null;
	 
		AudioInputStream din = null;
		AudioInputStream outDin = null;
		PCM2PCMConversionProvider conversionProvider = new PCM2PCMConversionProvider();
		
		File file = new File(filePath);
		
		AudioInputStream in=null;
		
		in = AudioSystem.getAudioInputStream(file);

		AudioFormat baseFormat = in.getFormat();
		
		//System.out.println(baseFormat.toString());

		AudioFormat decodedFormat = new AudioFormat(
													AudioFormat.Encoding.PCM_SIGNED,
													baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
													baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
													false);

		din = AudioSystem.getAudioInputStream(decodedFormat, in);

		if (!conversionProvider.isConversionSupported(Util.getFormat(),decodedFormat)) {
			System.out.println("Conversion is not supported");
		}

		//System.out.println(decodedFormat.toString());

		outDin = conversionProvider.getAudioInputStream(Util.getFormat(), din);
		formatTmp = decodedFormat;

		DataLine.Info info = new DataLine.Info(TargetDataLine.class,formatTmp);
		lineTmp = (TargetDataLine) AudioSystem.getLine(info);
		
		final TargetDataLine line = lineTmp;
		final AudioInputStream outDinSound = outDin;		
		 
		Matching(line, outDinSound);
		
	}

	
	private static void listenSound(String filePath,String songId) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		
		AudioFormat formatTmp = null;
		TargetDataLine lineTmp = null;
	 
		AudioInputStream din = null;
		AudioInputStream outDin = null;
		PCM2PCMConversionProvider conversionProvider = new PCM2PCMConversionProvider();
		
		File file = new File(filePath);
		
		AudioInputStream in=null;
		
		in = AudioSystem.getAudioInputStream(file);

		AudioFormat baseFormat = in.getFormat();
		
		//System.out.println(baseFormat.toString());

		AudioFormat decodedFormat = new AudioFormat(
													AudioFormat.Encoding.PCM_SIGNED,
													baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
													baseFormat.getChannels() * 2, baseFormat.getSampleRate(),
													false);

		din = AudioSystem.getAudioInputStream(decodedFormat, in);

		if (!conversionProvider.isConversionSupported(Util.getFormat(),decodedFormat)) {
			System.out.println("Conversion is not supported");
		}

		//System.out.println(decodedFormat.toString());

		outDin = conversionProvider.getAudioInputStream(Util.getFormat(), din);
		formatTmp = decodedFormat;

		DataLine.Info info = new DataLine.Info(TargetDataLine.class,formatTmp);
		lineTmp = (TargetDataLine) AudioSystem.getLine(info);
		
		
		Listening(songId, lineTmp, outDin);
 		
	}
  
	
	private static void Listening(String songId,TargetDataLine line,AudioInputStream outDinSound) {
			
		Date dataInicialBuffer= new Date();
		ByteArrayOutputStream out =Util.readBuffer(line,outDinSound);
		System.out.println("readBuffer:"+(new Date().getTime()-dataInicialBuffer.getTime()));
		
		Date dataInicialMakeSpectrum= new Date();
		Complex[][] results = Util.makeSpectrum(out);
		System.out.println("makeSpectrum:"+(new Date().getTime()-dataInicialMakeSpectrum.getTime()));
		
		Date dataInicialdetermineKeyPoints= new Date();
		Listen.determineKeyPoints(hashMap,results, songId);
		System.out.println("determineKeyPoints:"+(new Date().getTime()-dataInicialdetermineKeyPoints.getTime()));
 		
   }
	
	private static void Matching(TargetDataLine line,AudioInputStream outDinSound) {
			
			ByteArrayOutputStream out = Util.readBuffer(line, outDinSound);
            Complex[][] results = Util.makeSpectrum(out);
			Match.determineKeyPoints(hashMap,matchMap,results);
//
				
				int bestCount = 0;
				String bestSong = null;
				
	 
				
				Iterator<String> iterator = matchMap.keySet().iterator();
				while(iterator.hasNext()) {

					String id = iterator.next();
					
					
					Map<Integer, Integer> tmpMap = matchMap.get(id);
					int bestCountForSong = 0;

					for (Map.Entry<Integer, Integer> entry : tmpMap.entrySet()) {
						
						if (entry.getValue() > bestCountForSong) {
							
							bestCountForSong = entry.getValue();
						}
						//System.out.println("Time offset = " + entry.getKey() + ", Count = " + entry.getValue());
					}

					System.out.println(bestCountForSong+" For song id: " + id);
					if (bestCountForSong > bestCount) {
						bestCount = bestCountForSong;
						bestSong = id;
					}
					
					
				}

				System.out.println(" Best song id: " + bestSong);
			
		}

  
	
	
}
