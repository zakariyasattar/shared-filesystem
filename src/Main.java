import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class Main{

    public static void main(String[] args) {
        String files = connect("ls");
        String[] filesystem_split = files.split("\\r?\\n");

        for(int i = 0; i < filesystem_split.length; i++) {
            createBox(filesystem_split[i]);
        }
    }

    public static void createBox(String name) {
        System.out.println(name);
    }

    public static String connect(String command) {
        String host = "zakariya-rh";
        String user = "zakariyasattar";
        String password = "xfm9sh74";

        try {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();

//            Scanner command_input = new Scanner(System.in);
//            System.out.print(user + "@" + host + ":~$ ");
//
//            String command = command_input.nextLine();

            Channel channel = session.openChannel("exec");

            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    return (new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}