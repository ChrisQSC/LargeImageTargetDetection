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
	public int width;
	public int height;
	
	public ResultPair(Text key, BytesWritable value)
	{
		this.key = key;
		this.value = value;
		isSlice = false;
		offsetX = 0;
		offsetY = 0;
		width = 0;
		height = 0;
	}
	
	public ResultPair()
	{
		key = new Text();
		value = new BytesWritable();
		isSlice = false;
		offsetX = 0;
		offsetY = 0;
		width = 0;
		height = 0;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub

		isSlice = in.readBoolean();
		offsetX = in.readInt();
		offsetY = in.readInt();
		width = in.readInt();
		height = in.readInt();
		key = new Text(in.readLine());
		int length = in.readInt();
		byte[] tmp = new byte[length];
		in.readFully(tmp, 0, length);
		value = new BytesWritable(tmp);
		
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub

		out.writeBoolean(isSlice);
		out.writeInt(offsetX);
		out.writeInt(offsetY);
		out.writeInt(width);
		out.writeInt(height);
		out.writeBytes(key.toString()+"\n");
		out.writeInt(value.getLength());
		out.write(value.getBytes());
	}

}
