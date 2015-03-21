package whu.littlekkk.targetdetection;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import javax.imageio.ImageIO;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;




public class ImageSplitFactory {
	
	public int splitWidth = 1000;
	public int splitHeight = 1000;
	public double overlap = 0.1;
	
	private int currentX = 0;
	private int currentY = 0;
	
	private int totalX = 0;
	private int totalY = 0;
	
	private int width = 0;
	private int height = 0;
	private BufferedImage src;
	
	private String fileName;
	
	public ImageSplitFactory(String path)
	{
		try {
			
			this.src = ImageIO.read(new FileInputStream(path));
			this.fileName = new Path(path).getName();
			this.totalX = this.src.getWidth()/this.splitWidth+1;
			this.totalY = this.src.getHeight()/this.splitHeight+1;
			this.width = this.src.getWidth();
			this.height = this.src.getHeight();
			this.splitHeight = this.src.getHeight()/this.totalY;
			this.splitWidth = this.src.getWidth()/this.totalX;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ResultPair GetNextSplit()
	{
		if(totalY == currentY)
		{
			return null;
		}
		
		int offsetX = new Double(currentX*splitWidth).intValue();
		int offsetY = new Double(currentY*splitHeight).intValue();
		System.out.println(totalY);
		System.out.println(currentY);
		System.out.println( totalX-1>currentX?new Double(splitWidth*(1+overlap)).intValue():splitWidth);
		System.out.println( totalY-1>currentY?new Double(splitHeight*(1+overlap)).intValue():splitHeight);
		BufferedImage temp= this.src.getSubimage(offsetX,offsetY, totalX-1>currentX?new Double(splitWidth*(1+overlap)).intValue():splitWidth,totalY-1>currentY?new Double(splitHeight*(1+overlap)).intValue():splitHeight);
		ResultPair split = new ResultPair(new Text(this.fileName),new BytesWritable(imageToByte(temp,"BMP")));
		split.isSlice = true;
		split.offsetX = offsetX;
		split.offsetY = offsetY;
		
		currentX++;
		if(currentX >= totalX)
		{
			currentX=0;
			currentY++;
		}
		return split;
	}
	
	public byte[] imageToByte(final BufferedImage bufferedImage, final String formatName)  
	{  
		try
		{
	        ByteArrayOutputStream output = new ByteArrayOutputStream();  
	        ImageIO.write(bufferedImage, formatName, output);  
	        return output.toByteArray();  
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        	return null;
        }
      } 
}	
