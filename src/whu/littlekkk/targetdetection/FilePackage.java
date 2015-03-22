package whu.littlekkk.targetdetection;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

public class FilePackage {

	public class ImageFilter implements FilenameFilter
	{

		@Override
		public boolean accept(File file, String filename) {
			// TODO Auto-generated method stub
			String ext = getExtensionName(filename.toLowerCase());
			if(ext.equals("bmp")||ext.equals("jpg")||ext.equals("jpeg")||ext.equals("tif"))
				return true;
			return false;
		}
		
	}
	
	public void PackageImages(String inputPath,String outputPath,String setName){
		// TODO Auto-generated method stub
		try
		{
			/*delete*/
			int count  = 0;
			if(setName==null) setName = "imageSet";
			Path resultPath = new Path(outputPath);
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(new URI(outputPath),conf);
			
			//Check the outputPath
			if(fs.exists(resultPath))
			{
				//throw new Exception("Output Floder Exists!");
				fs.delete(resultPath, true);

				fs.mkdirs(resultPath);
				fs.close();
				fs = FileSystem.get(URI.create(outputPath), conf);
			}
			else
			{
				fs.mkdirs(resultPath);
				fs.close();
				fs = FileSystem.get(URI.create(outputPath), conf);
			}
			
			File folder = new File(inputPath);
			ImageFilter filter = new ImageFilter();
			File[] files = folder.listFiles(filter);
			
			int tag = -1;
			int allSize = 0;
			Path seqFile = null;
			SequenceFile.Writer writer = null;
			for(File file:files)
			{
				if(allSize==0||allSize+file.length()>66000000)
				{
					if(file.length()>40000000)
					{
						ImageSplitFactory factory  = new ImageSplitFactory(file.getPath());
						ResultPair temp;
						int singleFileRead = 0;
						SequenceFile.Writer hugeImgWriter = null;
						while((temp = factory.GetNextSplit())!=null)
						{
							if(singleFileRead == 0||singleFileRead > 40000000)
							{
								System.out.print("New Block Added.\n");
								allSize = 0;
								tag+=1;
								seqFile = new Path(outputPath+"/"+setName+"_"+String.valueOf(tag)+".seq");
								if(hugeImgWriter!=null) hugeImgWriter.close();
								hugeImgWriter = new SequenceFile.Writer(fs,conf,seqFile,Text.class,ResultPair.class);
								singleFileRead = 0;
							}
							File mfile = new File("/home/littlekkk/24/result/"+count+".bmp");
							ByteArrayInputStream in = new ByteArrayInputStream(temp.value.getBytes());    //将b作为输入流；
							BufferedImage image = ImageIO.read(in); 
							ImageIO.write(image, "BMP", mfile);
							singleFileRead+= temp.value.getLength();
							count++;
							hugeImgWriter.append(new Text(file.getName()), temp);
						}
						hugeImgWriter.close();
					}
					else
					{
						System.out.print("New Block Added.\n");
						allSize = 0;
						tag+=1;
						seqFile = new Path(outputPath+"/"+setName+"_"+String.valueOf(tag)+".seq");
						if(writer!=null) IOUtils.closeStream(writer);
						writer=new SequenceFile.Writer(fs,conf,seqFile,Text.class,BytesWritable.class);
						byte[] tmp = new byte[(int) file.length()];
						FileInputStream in = new FileInputStream(file);
						in.read(tmp);
						in.close();
						BytesWritable img = new BytesWritable(tmp);
						writer.append(new Text(file.getName()), img);
						//System.out.print("File Added:"+ file.getName()+"\n");
						allSize+=file.length();
					}
				}
				else
				{
					byte[] tmp = new byte[(int) file.length()];
					FileInputStream in = new FileInputStream(file);
					in.read(tmp);
					in.close();
					BytesWritable img = new BytesWritable(tmp);
					writer.append(new Text(file.getName()), img);
					//System.out.print("File Added:"+ file.getName()+"\n");
					allSize+=file.length();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	//Get extension of each file
	public static String getExtensionName(String filename) { 
        if ((filename != null) && (filename.length() > 0)) { 
            int dot = filename.lastIndexOf('.'); 
            if ((dot >-1) && (dot < (filename.length() - 1))) { 
                return filename.substring(dot + 1); 
            } 
        } 
        return filename; 
    } 
	
	public void Unpackage(String inputPath, String outputPath)
	{
		try {

			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(new URI(inputPath),conf);
			SeqFileFilter filter = new SeqFileFilter();
			FileStatus[] files = fs.listStatus(new Path(inputPath));
			for(FileStatus file:files)
			{
				if(!filter.accept(null, file.getPath().getName().toString())) continue;
				SequenceFile.Reader reader = new SequenceFile.Reader(fs, file.getPath(), fs.getConf());
				Text key = new Text();
				BytesWritable  value = new BytesWritable();
				while(reader.next(key, value))
				{
					File outFile = new File(outputPath+"/"+key+".bmp");
					int k = 0;
					while(outFile.exists())
					{
						outFile = new File(outputPath+"/"+k+key+".bmp");
						k++;
					}
					System.out.println(outFile.getName()+"outs");
					outFile.createNewFile();
					ByteArrayInputStream in = new ByteArrayInputStream(value.getBytes());    //将b作为输入流；
					BufferedImage image = ImageIO.read(in); 
					ImageIO.write(image, "BMP", outFile);
				}
				reader.close();
			}
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class SeqFileFilter implements FilenameFilter
	{

		@Override
		public boolean accept(File file, String filename) {
			// TODO Auto-generated method stub
			String ext = FilePackage.getExtensionName(filename.toLowerCase());
			if(ext.equals("seq"))
				return true;
			return false;
		}
		
	}
}
