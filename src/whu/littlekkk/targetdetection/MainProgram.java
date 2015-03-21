package whu.littlekkk.targetdetection;
import whu.littlekkk.targetdetection.FilePackage;;

public class MainProgram {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(System.getProperty("java.library.path"));
		String cmd = args[0].toLowerCase();
		if(cmd.equals("package"))
		{
			FilePackage task = new FilePackage();
			task.PackageImages(args[1],args[2],null);
		}
		if(cmd.equals("detection"))
		{
			ImageTargetDetection task = new ImageTargetDetection();
			task.processLargeImage(args[1],args[2]);
		}
		if(cmd.equals("unpackage"))
		{
			FilePackage task = new FilePackage();
			task.Unpackage(args[1],args[2]);
		}
	}

}
