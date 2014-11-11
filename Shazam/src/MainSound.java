import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import model.Complex;
import model.DataPoint;

import org.apache.commons.io.FilenameUtils;
import org.tritonus.sampled.convert.PCM2PCMConversionProvider;


public class MainSound {	

    static List<Path> fileList = new ArrayList<Path>();

	public static Map<Long, List<DataPoint>> hashMap;
	public static Map<String, Map<Integer, Integer>> matchMap;
 	
	public static int position=0;
	public static ExecutorService executor = Executors.newFixedThreadPool(8);
	
	static long media =0;
	
	public static void main(String[] args) {
		
		hashMap = new Hashtable<Long, List<DataPoint>>();
		matchMap = new Hashtable<String, Map<Integer, Integer>>();
		
		//String path ="E:/musicas/Sonata Arctica";
		//String path ="E:/Downloads/Nickelback - The Best Of Nickelback Vol. 01 [Collection] [2013] [Mp3-320]-V3nom [GLT]";
		String path ="E:/musicas/aerosmith";
		Path source = Paths.get(path);
		 
		 
 		try {
			Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS),Integer.MAX_VALUE,  new TreeFileVisitor());
			
			System.out.println("Acabou:"+fileList.size());


			for(int i=0;i<fileList.size();i++){

				listenSound(fileList.get(i));
	
			}
			
			
			
			int num =new Random().nextInt(fileList.size());
			
			long calc= (media/((long)fileList.size()));
			
			System.out.println(calc+"\n\n\n matchingSound:"+num+" path:"+fileList.get(num)+"\n\n\n");
			
			Date di=new Date();
			matchingSound(fileList.get(num).toString());
			
			System.out.println("time.matchingSound"+(new Date().getTime() -di.getTime()));
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }


	private static void matchingSound(String filePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		
		AudioFormat formatTmp = null;
		TargetDataLine lineTmp = null;
	 
		AudioInputStream din = null;
		AudioInputStream outDin = null;
		PCM2PCMConversionProvider conversionProvider = new PCM2PCMConversionProvider();
		
		AudioInputStream in=AudioSystem.getAudioInputStream( new File(filePath));
 
 		AudioFormat baseFormat = in.getFormat();
		
		System.out.println(baseFormat.toString());

 		AudioFormat decodedFormat =Util.getDecodedFormat(baseFormat);

		din = AudioSystem.getAudioInputStream(decodedFormat, in);

		if (!conversionProvider.isConversionSupported(Util.getFormat(),decodedFormat)) {
			System.out.println("Conversion is not supported");
		}

		 System.out.println(decodedFormat.toString());

 		outDin = conversionProvider.getAudioInputStream(Util.getFormat(), din);
 		formatTmp = decodedFormat;

		DataLine.Info info = new DataLine.Info(TargetDataLine.class,formatTmp);
		lineTmp = (TargetDataLine) AudioSystem.getLine(info);
		
		final TargetDataLine line = lineTmp;
		final AudioInputStream outDinSound = outDin;		
		 
		Matching(line, outDinSound);
		
	}
	
	private static void Matching(TargetDataLine line,AudioInputStream outDinSound) {
		
		ByteArrayOutputStream out = Util.readBuffer(1000,line, outDinSound);
        Complex[][] results = Util.makeSpectrum(out);
		Match.determineKeyPoints(hashMap,matchMap,results);
 
			
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
         
		 System.out.println("\n\n\n Best song id: " + bestSong);
		
	}	
	
	private static void listenSound(Path path) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		
		System.out.println(" Escutando:"+path.toString());
		
	 
		TargetDataLine line = null;
	 
		AudioInputStream din = null;
		AudioInputStream outDin = null;
		PCM2PCMConversionProvider conversionProvider = new PCM2PCMConversionProvider();
 		
		AudioInputStream in=null;
		
		in = AudioSystem.getAudioInputStream(path.toFile());

		AudioFormat baseFormat = in.getFormat();
		
		AudioFormat decodedFormat = Util.getDecodedFormat(baseFormat);

//		System.out.println(baseFormat.toString());
//		System.out.println(decodedFormat.toString());

		din = AudioSystem.getAudioInputStream(decodedFormat,in);

		if (!conversionProvider.isConversionSupported(Util.getFormat(),decodedFormat)) {
			System.out.println("Conversion is not supported for:"+path);
			return;
		}

 		outDin = conversionProvider.getAudioInputStream(Util.getFormat(), din);
	 
		DataLine.Info info = new DataLine.Info(TargetDataLine.class,decodedFormat);
		line = (TargetDataLine) AudioSystem.getLine(info);
 
		ByteArrayOutputStream out =Util.readBuffer(line,outDin);
		
		Date di = new Date();
		Complex[][] results = Util.makeSpectrum(out);
	 

		long tempo = new Date().getTime() - di.getTime();
	 	System.out.println("makeSpectrum:"+path.getFileName().toString()+" "+(tempo));
		
		media+=tempo;

		Listen.determineKeyPoints(hashMap,results, path.getFileName().toString());
		
	}
 
	static class TreeFileVisitor extends SimpleFileVisitor<Path>{
 		
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)throws IOException {
 			
			if(FilenameUtils.getExtension(file.getFileName().toString()).equalsIgnoreCase("mp3")){
				System.out.println(file);
				fileList.add(file);
			}
			return FileVisitResult.CONTINUE;
		}
 
		
	}
}