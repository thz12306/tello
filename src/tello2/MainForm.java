package tello2;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class MainForm {

	private JFrame frame;
	private JTextField showCommandText;
	private JTextField replyText;
	private JTextField hText;
	private JTextField batText;
	UdpServer udpServer2 = null;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm window = new MainForm();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainForm() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(79, 10, 244, 23);
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "\u63A7\u5236\u6A21\u5F0F", "\u8D77\u98DE",
				"\u7740\u9646", "\u5411\u4E0A\u98DE\u884C", "\u5411\u4E0B\u98DE\u884C", "\u5411\u5DE6\u98DE\u884C",
				"\u5411\u53F3\u98DE\u884C", "\u5411\u524D\u98DE\u884C", "\u5411\u540E\u98DE\u884C",
				"\u5411\u5DE6\u7FFB\u6EDA", "\u5411\u53F3\u7FFB\u6EDA" }));
		frame.getContentPane().add(comboBox);

		JButton button = new JButton("确认");
		button.setBounds(331, 10, 93, 23);
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String cmd = (String) comboBox.getModel().getElementAt(comboBox.getSelectedIndex());
				showCommandText.setText(cmd);
				try {
					udpServer2.send(cmd);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		frame.getContentPane().add(button);

		JLabel label = new JLabel("操作:");
		label.setBounds(10, 14, 59, 15);
		frame.getContentPane().add(label);

		showCommandText = new JTextField();
		showCommandText.setBounds(3, 53, 221, 21);
		frame.getContentPane().add(showCommandText);
		showCommandText.setColumns(10);

		JLabel replylabel = new JLabel("响应消息:");
		replylabel.setBounds(3, 96, 54, 15);
		frame.getContentPane().add(replylabel);

		replyText = new JTextField();
		replyText.setBounds(3, 136, 66, 21);
		UdpServer udpServer = new UdpServer();
		udpServer.setMsgPrinter(new UdpServer.Receive() {

			@Override
			public void print(String msg1, String msg2) {
				hText.setText(msg1);
				batText.setText(msg2);
			}
		});
		udpServer.receiver();
		frame.getContentPane().add(replyText);
		replyText.setColumns(10);

		udpServer2 = new UdpServer();
		udpServer2.setMsgPrinter2(new UdpServer.Receive2() {

			@Override
			public void print(String msg3) {
				replyText.setText(msg3);
			}
		});
		udpServer2.receiver2();

		JLabel hlabel = new JLabel("飞行高度:");
		hlabel.setBounds(79, 96, 54, 15);
		frame.getContentPane().add(hlabel);

		JLabel batLabel = new JLabel("电池电量:");
		batLabel.setBounds(158, 96, 54, 15);
		frame.getContentPane().add(batLabel);

		hText = new JTextField();
		hText.setBounds(79, 136, 66, 21);
		frame.getContentPane().add(hText);
		hText.setColumns(10);

		batText = new JTextField();
		batText.setBounds(158, 136, 66, 21);
		frame.getContentPane().add(batText);
		batText.setColumns(10);

		JButton performButton = new JButton("开始表演");
		performButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//////// perform

				try {
					showCommandText.setText("表演中，不要发送控制命令");
					udpServer2.setPerformlisten(new UdpServer.PerformAction() {

						@Override
						public void onStop() {
							button.setEnabled(true);
							performButton.setEnabled(true);
						}

						@Override
						public void onStart() {
							button.setEnabled(false);
							performButton.setEnabled(false);
						}
					});
					udpServer2.performStart();
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		});
		performButton.setBounds(331, 53, 93, 23);
		frame.getContentPane().add(performButton);

		JButton stopPerfromButton = new JButton("停止表演");
		stopPerfromButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//// stopPerfrom
				try {
					udpServer2.stop();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		stopPerfromButton.setBounds(331, 96, 93, 23);
		frame.getContentPane().add(stopPerfromButton);
	}
}
