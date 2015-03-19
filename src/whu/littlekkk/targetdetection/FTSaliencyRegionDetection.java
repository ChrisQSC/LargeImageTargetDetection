package whu.littlekkk.targetdetection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class FTSaliencyRegionDetection {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String inputPath = args[0];
		String outputPath = args[1];
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		File file = new File(inputPath);
		byte[] tmp = new byte[(int) file.length()];
		FileInputStream inputStream = new FileInputStream(file);
		inputStream.read(tmp);
		inputStream.close();
		MatOfByte newMat = new MatOfByte(tmp);
		Mat image = Highgui.imdecode(newMat, Highgui.CV_LOAD_IMAGE_COLOR );
		image.channels();
		Mat result = GetSaliencyMap(image,true);
		Highgui.imwrite(outputPath, result);
	}
	
	
	public static Mat[] RGB2LAB(Mat rgbImg)
	{
		//------------------------
		// sRGB to XYZ conversion
		// (D65 illuminant assumption)
		//------------------------
		Mat labImg[] = new Mat[3];
		labImg[0] = new Mat(rgbImg.rows(),rgbImg.cols(),CvType.CV_16SC1);
		labImg[1] = new Mat(rgbImg.rows(),rgbImg.cols(),CvType.CV_16SC1);
		labImg[2] = new Mat(rgbImg.rows(),rgbImg.cols(),CvType.CV_16SC1);
		
		for(int i=0;i<rgbImg.rows();i++)
		{
			for(int j=0;j<rgbImg.cols();j++)
			{
				double sR = rgbImg.get(i, j)[0];
				double sG = rgbImg.get(i, j)[1];
				double sB = rgbImg.get(i, j)[2];
				
				double R = sR/255.0;
				double G = sG/255.0;
				double B = sB/255.0;
		
				double r, g, b;
		
				if(R <= 0.04045)	r = R/12.92;
				else				r = Math.pow((R+0.055)/1.055,2.4);
				if(G <= 0.04045)	g = G/12.92;
				else				g = Math.pow((G+0.055)/1.055,2.4);
				if(B <= 0.04045)	b = B/12.92;
				else				b = Math.pow((B+0.055)/1.055,2.4);
		
				double X = r*0.4124564 + g*0.3575761 + b*0.1804375;
				double Y = r*0.2126729 + g*0.7151522 + b*0.0721750;
				double Z = r*0.0193339 + g*0.1191920 + b*0.9503041;
				//------------------------
				// XYZ to LAB conversion
				//------------------------
				double epsilon = 0.008856;	//actual CIE standard
				double kappa   = 903.3;		//actual CIE standard
		
				double Xr = 0.950456;	//reference white
				double Yr = 1.0;		//reference white
				double Zr = 1.088754;	//reference white
		
				double xr = X/Xr;
				double yr = Y/Yr;
				double zr = Z/Zr;
		
				double fx, fy, fz;
				if(xr > epsilon)	fx = Math.pow(xr, 1.0/3.0);
				else				fx = (kappa*xr + 16.0)/116.0;
				if(yr > epsilon)	fy = Math.pow(yr, 1.0/3.0);
				else				fy = (kappa*yr + 16.0)/116.0;
				if(zr > epsilon)	fz = Math.pow(zr, 1.0/3.0);
				else				fz = (kappa*zr + 16.0)/116.0;
				
				double rL = 116.0*fy-16.0;
				double rA = 500.0*(fx-fy);
				double rB = 200.0*(fy-fz);
				
				labImg[0].put(i, j, rL);
				labImg[1].put(i, j, rA);
				labImg[2].put(i, j, rB);
			}
		}
		return labImg;
	}
	
	public static Mat GetSaliencyMap(Mat inputImg,boolean normalnize) 
	{
		Mat salMap = new Mat(inputImg.rows(),inputImg.cols(),CvType.CV_16SC1);
		Mat blurImg_l = new Mat(inputImg.rows(),inputImg.cols(),CvType.CV_16SC1);
		Mat blurImg_a = new Mat(inputImg.rows(),inputImg.cols(),CvType.CV_16SC1);
		Mat blurImg_b = new Mat(inputImg.rows(),inputImg.cols(),CvType.CV_16SC1);
		
		
		Mat[] labImgs = RGB2LAB(inputImg); 
		
		Imgproc.GaussianBlur(labImgs[0], blurImg_l, new Size(3,3), 0);
		Imgproc.GaussianBlur(labImgs[1], blurImg_a, new Size(3,3), 0);
		Imgproc.GaussianBlur(labImgs[2], blurImg_b, new Size(3,3), 0);
		
		Scalar aveL = Core.mean(labImgs[0]);
		Scalar aveA = Core.mean(labImgs[1]);
		Scalar aveB = Core.mean(labImgs[2]);
		
		for(int i=0;i<inputImg.rows();i++)
		{
			for(int j=0;j<inputImg.cols();j++)
			{
				double result = (blurImg_l.get(i, j)[0] - aveL.val[0])*(blurImg_l.get(i, j)[0] - aveL.val[0])+
						(blurImg_a.get(i, j)[0] - aveA.val[0])*(blurImg_a.get(i, j)[0] - aveA.val[0])+
						(blurImg_b.get(i, j)[0] - aveB.val[0])*(blurImg_b.get(i, j)[0] - aveB.val[0]);
				salMap.put(i, j, result);
			}
		}
		
		if(normalnize)
		{
			Mat dst = new Mat(inputImg.rows(),inputImg.cols(),CvType.CV_16SC1);
			Core.normalize(salMap, dst,0,255.0,Core.NORM_MINMAX);
			return dst;
		}
		return salMap;
		
	}
	
	
	

}
