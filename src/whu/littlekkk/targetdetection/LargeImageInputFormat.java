package whu.littlekkk.targetdetection;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import whu.littlekkk.targetdetection.FilePackage;
import whu.littlekkk.targetdetection.LargeImageInputSplit;
import whu.littlekkk.targetdetection.LargeImageRecordReader;

public class LargeImageInputFormat extends InputFormat<IntWritable, ResultPair> {

	public static String inputPath="";
	
	@Override
	public RecordReader<IntWritable, ResultPair> createRecordReader(
			InputSplit split, TaskAttemptContext context) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub

		LargeImageRecordReader recordReader =  new LargeImageRecordReader();
		recordReader.initialize(split, context);
		return recordReader;
	}

	@Override
	public List<InputSplit> getSplits(JobContext job) throws IOException {
		// TODO Auto-generated method stub
		try
		{
			int id = 0;
			List<InputSplit> splits = new ArrayList<InputSplit>();
			Configuration conf = job.getConfiguration();
			FileSystem fs = FileSystem.get(new URI(inputPath),conf);
			SeqFileFilter filter = new SeqFileFilter();
			FileStatus[] files = fs.listStatus(new Path(inputPath));
			for(FileStatus file:files)
			{
				if(!filter.accept(null, file.getPath().getName().toString())) continue;
				LargeImageInputSplit split = new LargeImageInputSplit();
				split.length = file.getLen();
				split.packagePath = file.getPath().toString();
				BlockLocation[] blkLocations = fs.getFileBlockLocations(file, 0, file.getLen());
				List<String> locations = new ArrayList< String>();
				for(BlockLocation blkLocation:blkLocations)
				{
					String[] hosts = blkLocation.getHosts();
					for(String host:hosts)
					{
						locations.add(host);
					}
				}
				split.location = new String[locations.size()];
				split.id = id;
				locations.toArray(split.location);
				splits.add(split);
				id++;
			}
			return splits;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
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
