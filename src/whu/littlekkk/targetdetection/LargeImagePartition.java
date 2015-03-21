package whu.littlekkk.targetdetection;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class LargeImagePartition extends Partitioner<IntWritable, ResultPair> {

	@Override
	public int getPartition(IntWritable arg0, ResultPair arg1, int arg2) {
		// TODO Auto-generated method stub
		if(arg1.isSlice)
			return arg1.key.hashCode()%arg2;
		return arg0.get()%arg2;
	}

}
