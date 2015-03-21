package whu.littlekkk.targetdetection;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import whu.littlekkk.targetdetection.LargeImageInputSplit;

public class LargeImageRecordReader extends RecordReader<IntWritable, ResultPair> {
	
	SequenceFile.Reader reader = null;
	float splitSize = 0;
	float bytesRead = 0;
	int id = 0;
	Text key = new Text();
	BytesWritable value = new BytesWritable();
	ResultPair sliceValue = new ResultPair();
	
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		IOUtils.closeStream(reader);
	}

	@Override
	public IntWritable getCurrentKey() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return new IntWritable(id);
	}

	@Override
	public ResultPair getCurrentValue() throws IOException,
			InterruptedException {
		// TODO Auto-generated method stub
		if(reader.getValueClass().equals(ResultPair.class))
		{
			bytesRead += sliceValue.value.getLength();
			return sliceValue;
		}
		else
		{
			bytesRead += value.getLength();
			return new ResultPair(key,value);
		}
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return bytesRead/splitSize;
	}

	@Override
	public void initialize(InputSplit split, TaskAttemptContext conf)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		try
		{
			LargeImageInputSplit msplit = (LargeImageInputSplit)split;
			Path seqFile = new Path(msplit.packagePath);
			FileSystem fs = FileSystem.get(new URI(seqFile.toString()),conf.getConfiguration());
			reader = new SequenceFile.Reader(fs, seqFile, conf.getConfiguration());
			splitSize = msplit.length;
			id = msplit.id;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		if(reader.getValueClass().equals(BytesWritable.class))
		{
			return reader.next(key,value);
		}
		else
			return reader.next(key,sliceValue);
	}

}
