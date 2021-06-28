package utils;

import net.coobird.thumbnailator.Thumbnails;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

/**
 * @author bobo
 * @date 2021/6/24
 */

public class ScreenUtil {
    public static byte[] getDesktopScreen(){
        byte[] byteArray = null;
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        try {
            Robot robot = new Robot();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedImage image = robot.createScreenCapture(screenRect);
            Thumbnails.of(image)
                    .width(1024)
                    .outputFormat("jpg")
                    .toOutputStream(out);
            byteArray = out.toByteArray();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}
