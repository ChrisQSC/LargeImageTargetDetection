package whu.littlekkk.targetdetection;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class LargeImagePartition extends Partitioner<IntWritable, ResultPair> {

	@Override
	public int getPartition(IntWritable arg0, ResultPair arg1, int arg2) {
		// TODO Auto-generated method stub
		return arg0.get()%arg2;
	}

}
