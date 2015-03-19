package whu.littlekkk.targetdetection;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

/*
 * * ͼƬ�и���
 * *
 */
public class ImageSplitFactory {
	
	public int splitWidth = 1000;
	public int splitHeight = 1000;
	public double overlap = 0.1;
	
	private int currentX = 0;
	private int currentY = 0;
	
	private int totalX = 0;
	private int totalY = 0;
	
	private BufferedImage src;
	
	public ImageSplitFactory(String path)
	{
		try {
			
			this.src = ImageIO.read(new FileInputStream(path));
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
		
		return null;
	}
}	
