package utils;

import org.omg.SendingContext.RunTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bobo
 * @date 2021/6/24
 */

public class SystemUtil {
    private static String macAddressStr = null;
    private final static String OS_NAME = System.getProperties().getProperty("os.name");
    private static final Pattern MAC_PATTERN = Pattern.compile(".*((:?[0-9a-f]{2}[-:]){5}[0-9a-f]{2}).*",
            Pattern.CASE_INSENSITIVE);

    public static int getAllTasks(){
        List<String> taskList = new ArrayList<>();
        Process process=null;
        int count=0;
        try {
            if (OS_NAME.startsWith(Constant.LINUX)) {
                BufferedReader reader =null;
                process = Runtime.getRuntime().exec("ps -ef");
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = null;
                while((line = reader.readLine())!=null){
                    System.out.println(line);
                    count++;
                }
            } else {
                process = Runtime.getRuntime().exec("taskList");
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String s = "";
                while ((s = br.readLine()) != null) {
                    if ("".equals(s)) {
                        continue;
                    }
                    taskList.add(s+" ");
                }

                // 获取每列最长的长度
                String maxRow = taskList.get(1) + "";
                String[] maxCol = maxRow.split(" ");
                // 定义映像名称数组
                String[] taskName = new String[taskList.size()];
                // 定义 PID数组
                String[] taskPid = new String[taskList.size()];
                // 会话名数组
                String[] taskSessionName = new String[taskList.size()];
                // 会话#数组
                String[] taskSession = new String[taskList.size()];
                // 内存使用 数组
                String[] taskNec = new String[taskList.size()];
                for (int i = 0; i < taskList.size(); i++) {
                    String data = taskList.get(i) + "";
                    for (int j = 0; j < maxCol.length; j++) {
                        switch (j) {
                            case 0:
                                taskName[i]=data.substring(0, maxCol[j].length()+1);
                                data=data.substring(maxCol[j].length()+1);
                                break;
                            case 1:
                                taskPid[i]=data.substring(0, maxCol[j].length()+1);
                                data=data.substring(maxCol[j].length()+1);
                                break;
                            case 2:
                                taskSessionName[i]=data.substring(0, maxCol[j].length()+1);
                                data=data.substring(maxCol[j].length()+1);
                                break;
                            case 3:
                                taskSession[i]=data.substring(0, maxCol[j].length()+1);
                                data=data.substring(maxCol[j].length()+1);
                                break;
                            case 4:
                                taskNec[i]=data;
                                break;
                            default:
                                break;
                        }
                    }
                }

                for (int i = 0; i < taskNec.length; i++) {
                    //打印进程列表
                    System.out.println(taskName[i]+" "+taskPid[i]+" "+taskSessionName[i]+" "+taskSession[i]+" "+taskNec[i]);
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
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
        // 执行命令
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
     * 获取一个网卡地址（多个网卡时从中获取一个）
     *
     * @return
     */
    public static String getMacAddress() {
        if (macAddressStr == null || "".equals(macAddressStr)) {
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
            macAddressStr = sb.toString();
        }
        return macAddressStr;
    }



    public static void main(String[] args) {
        SystemUtil.getAllTasks();
        System.out.println(SystemUtil.getMacAddress());
    }
}
