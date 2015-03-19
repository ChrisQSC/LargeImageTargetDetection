package whu.littlekkk.targetdetection;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.io.SequenceFile;

public class LargeImageOutputFormat extends OutputFormat<IntWritable, ResultPair> {

	public static String OutputPath = "";
	
	@Override
	public void checkOutputSpecs(JobContext arg0) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OutputCommitter getOutputCommitter(TaskAttemptContext arg0)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return new LargeImageOutputCommitter();
	}

	@Override
	public RecordWriter<IntWritable, ResultPair> getRecordWriter(
			TaskAttemptContext task) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		if(OutputPath.equals(""))
		{
			OutputPath = task.getConfiguration().get("mapred.output.dir");
		}
		FileSystem fs = new Path(OutputPath).getFileSystem(task.getConfiguration());
		SequenceFile.Writer writer = new SequenceFile.Writer(fs, task.getConfiguration(),new Path(OutputPath+"/result.seq"), Text.class, BytesWritable.class);
		return new LargeImageRecordWriter(writer);
	}

}
