import java.io.InputStream;
import javax.swing.*;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

//TODO: Initialize buttons swing

public class Main extends JFrame{

    public static void main(String[] args) {
        String files = connect("ls");
        String[] filesystem_split = files.split("\\r?\\n");

        initGUI(filesystem_split);
    }


    private static void initGUI(String[] filesystem_split) {
        JFrame l_Frame= new JFrame("~");
        for(int i = 0; i < filesystem_split.length; i++) {
            JButton name = new JButton(filesystem_split[i]);
            l_Frame.add(name);
        }
        l_Frame.setSize(500,500);
        l_Frame.setVisible(true);
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