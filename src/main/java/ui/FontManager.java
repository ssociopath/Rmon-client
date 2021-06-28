package ui;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

/**
 * @author bobo
 * @date 2021/6/28
 */

public class FontManager {
    public static Font SimSun;
    public static Font SimSHei;

    static{
        SimSun = getFont("SimSun.ttf");
        SimSHei = getFont("SimHei.ttf");
        initGlobalFont();
    }

    public static Font getFont(String name){
        InputStream is = null;
        BufferedInputStream bis = null;
        Font font = null;
        try {
            is = FontManager.class.getClassLoader().getResourceAsStream("fonts/"+name);
            assert is != null;
            bis = new BufferedInputStream(is);
            font = Font.createFont(Font.TRUETYPE_FONT, bis);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bis) {
                    bis.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return font;
    }

    public static void initGlobalFont() {
        FontUIResource fontResource = new FontUIResource(SimSun.deriveFont(Font.PLAIN,16));
        for(Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if(value instanceof FontUIResource) {
                UIManager.put(key, fontResource);
            }
        }
    }
}
