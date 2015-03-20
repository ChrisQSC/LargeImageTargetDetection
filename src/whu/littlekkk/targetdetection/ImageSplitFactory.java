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
	
	private BufferedImage src;
	
	private String fileName;
	
	public ImageSplitFactory(String path)
	{
		try {
			
			this.src = ImageIO.read(new FileInputStream(path));
			this.fileName = new Path(path).getName();
			this.totalX = this.src.getWidth()/this.splitWidth+1;
			this.totalY = this.src.getHeight()/this.splitHeight+1;
		
			this.splitHeight = this.src.getHeight()/this.totalX;
			this.splitWidth = this.src.getWidth()/this.totalY;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ResultPair GetNextSplit()
	{
		int offsetX = new Double(currentX*splitWidth*(1+overlap)).intValue();
		int offsetY = new Double(currentY*splitHeight*(1+overlap)).intValue();
		BufferedImage temp= this.src.getSubimage(offsetX,offsetY, splitWidth, splitHeight);
		ResultPair split = new ResultPair(new Text(this.fileName),new BytesWritable(imageToByte(temp,"JPEG")));
		split.isSlice = true;
		split.offsetX = offsetX;
		split.offsetY = offsetY;
		if(totalX >= currentX++)
		{
			currentX=0;
			if(totalY >= currentY++)
			{
				return null;
			}
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
