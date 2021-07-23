package camera;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class LiveConfigFrame extends JFrame implements ActionListener{

    JLabel ipLabel;
    JTextField ip;
    JLabel portLabel;
    JTextField port;
    JButton start;

    public LiveConfigFrame(String title) {
        this.setIconImage(new ImageIcon(LiveConfigFrame.class.getResource("q2.png")).getImage());
        this.setTitle(title);

        JTextArea label = new JTextArea("ip应为nginx运行的电脑/服务器ip，" +
                "端口不建议修改，如果一定要修改，" +
                "请保证该端口与nginx和前端页面访问端口一致。");
        label.setLineWrap(true);
        label.setEditable(false);
        label.setBounds(0, 160, 280, 100);
        this.port = new JTextField(String.valueOf(CommonConfig.putPort));
        this.port.setBounds(110, 75, 100, 30);
        this.ipLabel = new JLabel("输入推流ip:");
        this.ipLabel.setBounds(35, 20, 115, 30);
        this.portLabel = new JLabel("输入推流端口:");
        this.portLabel.setBounds(20, 75, 115, 30);
        this.start = new JButton("载入配置并开始");
        this.start.setBounds(60, 120, 150, 30);
        this.ip = new JTextField(CommonConfig.putHost);
        this.ip.setBounds(110, 20, 100, 30);

        // 绑定按钮点击事件
        start.addActionListener(this);
        this.add(port);
        this.add(ipLabel);
        this.add(label);
        this.add(portLabel);
        this.add(start);
        this.add(ip);
        this.setSize(300, 255);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CommonConfig.putHost = ip.getText();
        CommonConfig.putPort = Integer.parseInt(port.getText());
        // 尝试连接，如果目标地址不可达，则不开启直播
        Socket rtmpSocket = new Socket();
        try {
            rtmpSocket.connect(new InetSocketAddress(CommonConfig.putHost, CommonConfig.putPort), 1000);
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(null, "ip地址或端口不可达，请重新配置", "提醒", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Live.isAction = true;
        this.dispose();
    }

}
