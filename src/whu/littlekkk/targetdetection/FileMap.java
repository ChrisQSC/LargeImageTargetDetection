package whu.littlekkk.targetdetection;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

public class FileMap extends Mapper<IntWritable, ResultPair, IntWritable, ResultPair> {

	@Override
	protected void map(IntWritable key, ResultPair value, Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("map");
		MatOfByte newMat = new MatOfByte(value.value.getBytes());
		Mat image = Highgui.imdecode(newMat, Highgui.CV_LOAD_IMAGE_COLOR );
		image.channels();
		Mat result = FTSaliencyRegionDetection.GetSaliencyMap(image,true);
		MatOfByte mat =new MatOfByte();
		Highgui.imencode(".jpg",result, mat);
	    byte[] byteArray = mat.toArray();
	    value.value.set(byteArray, 0,byteArray.length );
	    context.write(key, value);
	    System.out.println(value.key.toString()+"processed");
	}
	
}
