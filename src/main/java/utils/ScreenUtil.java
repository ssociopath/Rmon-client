package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author bobo
 * @date 2021/6/24
 */

public class ScreenUtil {
    public static void getDesktopScreen(){
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        try {
            Robot robot = new Robot();
            OutputStream out = new FileOutputStream("image.png");
            BufferedImage image = robot.createScreenCapture(screenRect);
            ImageIO.write(image, "png", out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
