package whu.littlekkk.targetdetection;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

public class ResultPair implements Writable {
	
	public  Text key;
	public  BytesWritable value;
	public boolean isSlice;
	public int offsetX;
	public int offsetY;
	
	public ResultPair(Text key, BytesWritable value)
	{
		this.key = key;
		this.value = value;
		isSlice = false;
		offsetX = 0;
		offsetY = 0;
	}
	
	public ResultPair()
	{
		key = null;
		value = null;
		isSlice = false;
		offsetX = 0;
		offsetY = 0;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		key = new Text(in.readLine());
		int length = in.readInt();
		byte[] tmp = new byte[length];
		in.readFully(tmp, 0, length);
		value = new BytesWritable(tmp);
		isSlice = in.readBoolean();
		offsetX = in.readInt();
		offsetY = in.readInt();
		
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeBytes(key.toString()+"\n");
		out.writeInt(value.getLength());
		out.write(value.getBytes());
		out.writeBoolean(isSlice);
		out.writeInt(offsetX);
		out.writeInt(offsetY);
	}

}
