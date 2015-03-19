package whu.littlekkk.targetdetection;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.io.SequenceFile;

public class LargeImageRecordWriter extends RecordWriter<IntWritable, ResultPair>  {

	public SequenceFile.Writer  writer = null;
	public LargeImageRecordWriter(SequenceFile.Writer writer)
	{
		this.writer = writer;
	}
	@Override
	public void close(TaskAttemptContext arg0) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		writer.close();
	}

	@Override
	public void write(IntWritable key, ResultPair value) throws IOException,
			InterruptedException {
		// TODO Auto-generated method stubd
		writer.append(value.key,value.value);
	}

}
