import java.io.InputStream;
import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.*;
import java.awt.*;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.miginfocom.layout.Grid;
import net.miginfocom.swing.MigLayout;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

//TODO: reinitGUI(),
//TODO: add new frame instead of redrawing old one

public class Main extends JFrame{
    public static String[] filesystem_split;

    public static void main(String[] args) {
        String files = connect("ls");
        Main.filesystem_split = files.split("\\r?\\n");

        initGUI(Main.filesystem_split);
        //playSong("http://www.ntonyx.com/mp3files/Morning_Flower.mp3");
    }

    private static void initGUI(String[] filesystem_split) {
        JFrame frame = new JFrame("~");
        frame.setSize(500,500);

        JLabel title = new JLabel("Welcome to Shared-Filesystem! Directory: ~", SwingConstants.CENTER);
        title.setBounds(frame.getWidth()/6, 30, 300, 200);
        frame.add(title);

        for(int i = 0; i < filesystem_split.length; i++) {
            var directoryName = filesystem_split[i];
            JButton name = new JButton(filesystem_split[i]);
            name.setBounds(i*110 + (frame.getWidth()/6), 200, 100, 40);

            name.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    reInitGUI("ls ~/" + directoryName);
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                }
            });
            frame.add(name);
        }

        frame.setLayout(null);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public static void reInitGUI(String command) {
        JFrame frame = new JFrame(command.split(" ")[1]);
        frame.setSize(500,500);

        JButton backButton = new JButton("Go Back");
        backButton.setBounds(10, 10, 100, 30);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int count = command.length() - command.replace("/", "").length();
                if(count == 1) {
                    initGUI(Main.filesystem_split);
                }
                else {
                    reInitGUI(command.substring(0, command.lastIndexOf("/")));
                }
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        });

        frame.add(backButton);

        String files = connect(command);
        String[] filesystem_split = files.split("\\r?\\n");

        JLabel title = new JLabel("Welcome to Shared-Filesystem! Directory: " + command.split(" ")[1], SwingConstants.CENTER);
        title.setBounds(frame.getWidth()/6, 30, 300, 200);
        frame.add(title);

        JPanel panel = new JPanel();
        BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxlayout);
        panel.setBorder(new EmptyBorder(new Insets(150, 200, 150, 200)));

        for(int i = 0; i < filesystem_split.length; i++) {
            var directoryName = filesystem_split[i];
            JButton name = new JButton(filesystem_split[i]);
            name.setSize(100, 40);

            name.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    reInitGUI("ls " + command.split(" ")[1] + "/" + ("\"" + directoryName + "\""));
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                }
            });

            panel.add(name);
        }
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
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

    public static void playSong(String url) {
        Player mp3player = null;
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream());
            mp3player = new Player(in);
            mp3player.play();
        } catch (MalformedURLException ex) {
        } catch (IOException e) {
        } catch (JavaLayerException e) {
        } catch (NullPointerException ex) {
        }
    }
}