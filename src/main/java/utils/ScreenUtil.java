package utils;

import net.coobird.thumbnailator.Thumbnails;
import sun.awt.ComponentFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.peer.RobotPeer;
import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bobo
 * @date 2021/6/24
 */

public class ScreenUtil {
    public static byte[] getDesktopScreen(int size){
        byte[] byteArray = null;
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        try {
            Robot robot = new Robot();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedImage image = robot.createScreenCapture(screenRect);
            if(size>=screenRect.width){
                ImageIO.write(image, "jpg", out);
            }else{
                Thumbnails.of(image)
                        .width(size)
                        .outputFormat("jpg")
                        .toOutputStream(out);
            }
            byteArray = out.toByteArray();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArray;
    }


    public static void main(String[] args) throws AWTException {
    }
}
