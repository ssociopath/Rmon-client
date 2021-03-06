package utils;

import net.coobird.thumbnailator.Thumbnails;
import network.vo.KeyCmd;
import network.vo.Task;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bobo
 * @date 2021/6/24
 */

public class SystemUtil {
    private static String MAC_ADDRESS = null;
    private final static String OS_NAME = System.getProperties().getProperty("os.name");
    private static final Pattern MAC_PATTERN = Pattern.compile(".*((:?[0-9a-f]{2}[-:]){5}[0-9a-f]{2}).*",
            Pattern.CASE_INSENSITIVE);
    private static Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    private static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getDesktopScreen(int size){
        byte[] byteArray = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedImage image = robot.createScreenCapture(screenRect);
            if(size>=screenRect.width||size == 1080){
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

    public static List<Task> getAllTasks(){
        List<Task> taskList = new ArrayList<>();
        Process process =null;
        int count=0;
        try {
            if (OS_NAME.startsWith(Constant.LINUX)) {
                BufferedReader reader =null;
                process = Runtime.getRuntime().exec("top -b -n 1");
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = null;
                int index = 0;
                while((line = reader.readLine())!=null){
                    if(index>6){
                        String[] lineStr = line.split("\\s+");
                        taskList.add(Task.builder()
                                .pid(lineStr[1])
                                .name(lineStr[12])
                                .user(lineStr[2])
                                .mem(lineStr[10]+"%")
                                .cpu(lineStr[11])
                                .build());
                    }else{
                        index++;
                    }


                    count++;
                }
                reader.close();
            } else {
                process = Runtime.getRuntime().exec("taskList");
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String s = "";
                //TODO WINDOWS???
//                while ((s = br.readLine()) != null) {
//                    if ("".equals(s)) {
//                        continue;
//                    }
//                    taskList.add(s+" ");
//                }
//
//                // ???????????????????????????
//                String maxRow = taskList.get(1) + "";
//                String[] maxCol = maxRow.split(" ");
//                // ????????????????????????
//                String[] taskName = new String[taskList.size()];
//                // ?????? PID??????
//                String[] taskPid = new String[taskList.size()];
//                // ???????????????
//                String[] taskSessionName = new String[taskList.size()];
//                // ??????#??????
//                String[] taskSession = new String[taskList.size()];
//                // ???????????? ??????
//                String[] taskNec = new String[taskList.size()];
//                for (int i = 0; i < taskList.size(); i++) {
//                    String data = taskList.get(i) + "";
//                    for (int j = 0; j < maxCol.length; j++) {
//                        switch (j) {
//                            case 0:
//                                taskName[i]=data.substring(0, maxCol[j].length()+1);
//                                data=data.substring(maxCol[j].length()+1);
//                                break;
//                            case 1:
//                                taskPid[i]=data.substring(0, maxCol[j].length()+1);
//                                data=data.substring(maxCol[j].length()+1);
//                                break;
//                            case 2:
//                                taskSessionName[i]=data.substring(0, maxCol[j].length()+1);
//                                data=data.substring(maxCol[j].length()+1);
//                                break;
//                            case 3:
//                                taskSession[i]=data.substring(0, maxCol[j].length()+1);
//                                data=data.substring(maxCol[j].length()+1);
//                                break;
//                            case 4:
//                                taskNec[i]=data;
//                                break;
//                            default:
//                                break;
//                        }
//                    }
//                }
//
//                for (int i = 0; i < taskNec.length; i++) {
//                    //??????????????????
//                    System.out.println(taskName[i]+" "+taskPid[i]+" "+taskSessionName[i]+" "+taskSession[i]+" "+taskNec[i]);
//                    count++;
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskList;
    }



    private final static List<String> getMacAddressList() throws IOException {
        final String[] windowsCommand = { "ipconfig", "/all" };
        final String[] linuxCommand = { "/sbin/ifconfig", "-a" };
        final ArrayList<String> macAddressList = new ArrayList<>();
        final String[] command;

        if (OS_NAME.startsWith(Constant.WINDOWS)) {
            command = windowsCommand;
        } else if (OS_NAME.startsWith(Constant.LINUX)) {
            command = linuxCommand;
        } else {
            throw new IOException("Unknown operating system:" + OS_NAME);
        }
        // ????????????
        final Process process = Runtime.getRuntime().exec(command);

        BufferedReader bufReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        for (String line = null; (line = bufReader.readLine()) != null;) {
            Matcher matcher = MAC_PATTERN.matcher(line);
            if (matcher.matches()) {
                macAddressList.add(matcher.group(1));
            }
        }

        process.destroy();
        bufReader.close();
        return macAddressList;
    }

    /**
     * ???????????????????????????????????????????????????????????????
     *
     * @return
     */
    public static String getMacAddress() {
        if (MAC_ADDRESS == null || "".equals(MAC_ADDRESS)) {
            StringBuilder sb = new StringBuilder();
            try {
                List<String> macList = getMacAddressList();
                for (String amac : macList) {
                    if (!"0000000000E0".equals(amac)) {
                        sb.append(amac);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            MAC_ADDRESS = sb.toString();
        }
        return MAC_ADDRESS;
    }

    public static void exeControl(String cmd){
        System.out.println(cmd);
        KeyCmd keyCmd = JsonUtil.parseObject(cmd,KeyCmd.class);
        String openType = keyCmd.getOpenType();
        if("mousedown".equals(openType)){
            int remoteClientX = keyCmd.getClientX() * screenRect.width/keyCmd.getImageWidth();
            int remoteClientY = keyCmd.getClientY() * screenRect.height/keyCmd.getImageHeight();

            robot.mouseMove( remoteClientX , remoteClientY );

            int button = keyCmd.getButton();
            if(button == 0) {
                robot.mousePress(InputEvent.BUTTON1_MASK);//??????
            }else if(button == 1) {
                robot.mousePress(InputEvent.BUTTON2_MASK);//?????????
            }else if(button == 2) {
                robot.mousePress(InputEvent.BUTTON3_MASK);//??????
            }
        }else if("mouseup".equals(openType)){
            int remoteClientX = keyCmd.getClientX() * screenRect.width/keyCmd.getImageWidth();
            int remoteClientY = keyCmd.getClientY() * screenRect.height/keyCmd.getImageHeight();

            robot.mouseMove( remoteClientX , remoteClientY );

            int button = keyCmd.getButton();
            if(button == 0) {
                robot.mouseRelease(InputEvent.BUTTON1_MASK);//??????
            }else if(button == 1) {
                robot.mouseRelease(InputEvent.BUTTON2_MASK);//?????????
            }else if(button == 2) {
                robot.mouseRelease(InputEvent.BUTTON3_MASK);//??????
            }
        }else if("keydown".equals(openType)){
            int keyCode = keyCmd.getKeyCode();
            robot.keyPress(changeKeyCode(keyCode));
        }else if("keyup".equals(openType)){
            int keyCode = keyCmd.getKeyCode();
            robot.keyRelease(changeKeyCode(keyCode));
        }
    }


    public static void exeCmd(String cmd){
        String[] linuxCmd = new String[2];
        switch (cmd){
            case "lockscreen": linuxCmd[0]="xdg-screensaver";linuxCmd[1]="lock";break;
            case "reboot": linuxCmd[0]="reboot";linuxCmd[1]="";break;
            case "shutdown": linuxCmd[0]="shutdown";linuxCmd[1]="now";break;
            default:break;
        }
        try {
            Runtime.getRuntime().exec(linuxCmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int changeKeyCode(int sourceKeyCode){
        //??????
        if(sourceKeyCode == 13) return 10;
        //,< 188 -> 44
        if(sourceKeyCode == 188) return 44;
        //.>???Js??????190?????????Java??????46
        if(sourceKeyCode == 190) return 46;
        // /????Js??????191?????????Java??????47
        if(sourceKeyCode == 191) return 47;
        //;: 186 -> 59
        if(sourceKeyCode == 186) return 59;
        //[{ 219 -> 91
        if(sourceKeyCode == 219) return 91;
        //\| 220 -> 92
        if(sourceKeyCode == 220) return 92;
        //-_ 189->45
        if(sourceKeyCode == 189) return 45;
        //=+ 187->61
        if(sourceKeyCode == 187) return 61;
        //]} 221 -> 93
        if(sourceKeyCode == 221) return 93;
        //DEL
        if(sourceKeyCode == 46) return 127;
        //Ins
        if(sourceKeyCode == 45) return 155;
        return sourceKeyCode;
    }

}
