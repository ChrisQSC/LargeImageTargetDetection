package whu.littlekkk.targetdetection;


import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import whu.littlekkk.targetdetection.LargeImageInputFormat;
import whu.littlekkk.targetdetection.LargeImageOutputFormat;

public class ImageTargetDetection {
		
		public void processLargeImage(String inputPath,String outputPath)
		{
			try {
				
				Configuration conf = new Configuration();
				

				LargeImageInputFormat.inputPath = inputPath;
			    URI tmp = new URI(inputPath);
			    URI dependencePath = new URI("hdfs://"+tmp.getHost()+":"+String.valueOf(tmp.getPort())+"/dependence/imagedetection.jar#dependence");
				DistributedCache.addCacheArchive(dependencePath, conf);
			    Job job = new Job(conf,"Image Target Detection");
			    /*
			    job.getConfiguration().set("java.library.path","./dependence");
			    */
			    job.getConfiguration().set("mapred.output.dir", outputPath);
				job.setInputFormatClass(LargeImageInputFormat.class);
				job.setOutputFormatClass(LargeImageOutputFormat.class);
				
				 
				
				job.setMapperClass(FileMap.class);
				job.setReducerClass(FileReduce.class);
				
				job.setMapOutputKeyClass(IntWritable.class);
				job.setMapOutputValueClass(ResultPair.class);
				
				job.setPartitionerClass(LargeImagePartition.class);
				
				
				job.waitForCompletion(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
