package whu.littlekkk.targetdetection;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import whu.littlekkk.targetdetection.ResultPair;

public class FileReduce extends
		Reducer<IntWritable, ResultPair, IntWritable, ResultPair> {

	@Override
	protected void reduce(IntWritable key, Iterable<ResultPair> value,Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		ResultPair firstSlice =  value.iterator().next();
		BufferedImage img = new BufferedImage(firstSlice.width,firstSlice.height,BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D paint = img.createGraphics();
		paint.setBackground(Color.WHITE);
		ByteArrayInputStream in = new ByteArrayInputStream(firstSlice.value.getBytes());    //将b作为输入流；
		BufferedImage image = ImageIO.read(in); 
		paint.drawImage(image,new AffineTransform(1f,0f,0f,1f,firstSlice.offsetX,firstSlice.offsetY),null);
		for(ResultPair slice : value)
		{
			if(!slice.isSlice)
			{
				context.write(key, slice);
				return;
			}
			 in = new ByteArrayInputStream(slice.value.getBytes());    //将b作为输入流；
			 image = ImageIO.read(in); 
			paint.drawImage(image,new AffineTransform(1f,0f,0f,1f,slice.offsetX,slice.offsetY),null);
		}
		paint.dispose();
        ByteArrayOutputStream output = new ByteArrayOutputStream();  
        ImageIO.write(img, "BMP", output);  
		firstSlice.value = new BytesWritable(output.toByteArray()) ;
		context.write(key,firstSlice);
	}

}
