package whu.littlekkk.targetdetection;

import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import whu.littlekkk.targetdetection.ResultPair;

public class FileReduce extends
		Reducer<IntWritable, ResultPair, IntWritable, ResultPair> {

	@Override
	protected void reduce(IntWritable key, Iterable<ResultPair> value,Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		super.reduce(key, value, context);
	}

}
