package whu.littlekkk.targetdetection;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputSplit;

public class LargeImageInputSplit extends InputSplit implements Writable{
	
	public String packagePath;
	public long length;
	public String[] location;
	public int id;
	
	@Override
	public long getLength() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return length;
	}

	@Override
	public String[] getLocations() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		System.out.print(location[0]);
		return location;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub

		length = in.readLong();
		packagePath = in.readLine();
		int hostLength = in.readInt();
		id = in.readInt();

		List<String> hosts_  = new ArrayList<String>();
		for(int i = 0;i<hostLength;i++)
		{
			hosts_.add(in.readLine());
		}
		location = new String[hosts_.size()];
		System.out.println(location);
		hosts_.toArray(location);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeLong(length);
		out.writeBytes(packagePath+"\n");
		out.writeInt(location.length);
		out.writeInt(id);
		for(String host_ : location)
		{
			System.out.print(host_);
		}
		

		

	}

}
