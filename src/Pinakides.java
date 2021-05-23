import com.openalpr.jni.Alpr;
import com.openalpr.jni.AlprPlate;
import com.openalpr.jni.AlprPlateResult;
import com.openalpr.jni.AlprResults;
import java.awt.Image;

import java.sql.*;
import org.opencv.core.Core;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
/**
 *
 * @author rafail
 */

public class Pinakides {
private static java.sql.Timestamp getCurrentTimeStamp() {

	java.util.Date today = new java.util.Date();
	return new java.sql.Timestamp(today.getTime());
    }

public static void sendData(String plates,float conf){
    Connection conn = MySqlconnect.dbConnection() ;
    PreparedStatement prs=null;
//    
    try{
                        conn.setAutoCommit(false);
                        

                        String sql=("Insert into alpr_cars(Car_Plates,Overall_Confidence,TimeStampUpload) values (?,?,?)");
                        prs=conn.prepareStatement(sql);
                        prs.setString(1, plates);
                        prs.setFloat(2, conf);
                        
                        prs.setTimestamp(3, getCurrentTimeStamp());
                        prs.addBatch();
                        
                        prs.executeBatch();
                        conn.commit();
    }catch (Exception ex){System.out.print("Error Update");}
}  
public static void getPlates(String image_path){
        
        
        String country = "eu";
        String configfile = "C:\\Program Files\\Openalpr\\openalpr_64\\openalpr.conf";
        String runtimeDataDir = "C:\\Program Files\\Openalpr\\openalpr_64\\runtime_data";
        String licensePlate =image_path;

        Alpr alpr = new Alpr(country, configfile, runtimeDataDir);

        alpr.setTopN(0);
        alpr.setDefaultRegion("wa");
       
        // Read an image into a byte array and send it to OpenALPR
        Path path = Paths.get(licensePlate);
        try{
        byte[] imagedata = Files.readAllBytes(path);
        AlprResults results = alpr.recognize(imagedata);

        

     

        System.out.format("  %-15s%-8s\n", "Plate Number", "Confidence");
        for (AlprPlateResult result : results.getPlates())
        {
            for (AlprPlate plate : result.getTopNPlates()) {
                if (plate.isMatchesTemplate()){
                    System.out.print("  * ");
                    sendData(plate.getCharacters(),plate.getOverallConfidence());}
                else
                    System.out.print("  - ");
                    System.out.format("%-15s%-8f\n", plate.getCharacters(), plate.getOverallConfidence());
                    sendData(plate.getCharacters(),plate.getOverallConfidence());
            }
        }
        }
        catch(Throwable e){}


        
        alpr.unload();
    }


public static void main(String[] args) throws Exception {

System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


VideoCapture cap = new VideoCapture();
//
    String input = "";//put this type rtsp://{cameraIp}:{port}/h264_pcm.sdp for ip camer input or file path of .mp4 file
    String output = "";//put file path for output to save frames
    cap.set(Videoio.CV_CAP_PROP_FPS, 1);
//
cap.open(input);
//
    
   int frame_number= (int) cap.get(Videoio.CAP_PROP_POS_FRAMES);
    
    double calc_timestamp=0;
    Mat frame = new Mat();

        
if (cap.isOpened())
{
   
   
    while(cap.read(frame))
        
    {   
        
        if((frame_number % 30)==0){
       
        double frame_timestamp=(int) cap.get(Videoio.CAP_PROP_POS_MSEC);
        Imgcodecs.imwrite(output + "/" + frame_number +".jpg", frame);
        String image_path=output + "/" + frame_number +".jpg";
        calc_timestamp=frame_timestamp/1000;
        System.out.println(calc_timestamp);
        
        getPlates(image_path);
        frame_number=0;
        
        }
        frame_number++;
        
    }
    

    cap.release();    

}

    else
    {
        System.out.println("Fail");
    }
}
}
