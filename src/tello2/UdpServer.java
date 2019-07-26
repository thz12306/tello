package tello2;

import java.io.*;
import java.net.*;

/**
 * 接收类
 * 
 * @author 唐宏章
 *
 */
public class UdpServer {

	public boolean isStopped = false;

	/**
	 * 交由上层打印处理接口
	 * 
	 * @author 唐宏章
	 *
	 */
	public interface Receive {
		public void print(String msg1, String msg2);
	}

	public interface Receive2 {
		public void print(String msg);
	}

	public interface PerformAction {
		public void onStart();

		public void onStop();
	}

	/**
	 * 内部消息接收器
	 */
	private Receive l = null;
	private Receive2 m = null;
	private PerformAction listen = null;

	public void receiver() {
		new Thread() {
			@Override
			public void run() {
				DatagramSocket dz = null;
				try {
					InetSocketAddress address = new InetSocketAddress("0.0.0.0", 8890);
					dz = new DatagramSocket(address);
					byte[] buf = new byte[1024];
					DatagramPacket dp = new DatagramPacket(buf, buf.length);
					while (true) {
						dz.receive(dp);
						String str = new String(dp.getData(), 0, dp.getLength());
						if (str != null) {
							byte[] buf1 = str.getBytes();
							String[] strarray = str.split(";");
							if (l != null) {
								l.print(strarray[14], strarray[15]);
							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				dz.close();
			}
		}.start();
	}

	/**
	 * 既做接收，又做发送端
	 */
	DatagramSocket ds = null;

	public void send(String command) {
		if (ds != null) {
			InetAddress target;
			try {
				target = InetAddress.getByName("192.168.10.1");

				switch (command) {
				case "控制模式":
					command = "command";
					break;
				case "起飞":
					command = "takeoff";
					break;
				case "着陆":
					command = "land";
					break;
				case "向上飞行":
					command = "up 20";
					break;
				case "向下飞行":
					command = "down 20";
					break;
				case "向左飞行":
					command = "left 20";
					break;
				case "向右飞行":
					command = "right 20";
					break;
				case "向前飞行":
					command = "forward 20";
					break;
				case "向后飞行":
					command = "back 20";
					break;
				case "向左翻滚":
					command = "flip l";
					break;
				case "向右翻滚":
					command = "flip r";
					break;
				default:
					break;
				}

				byte[] buf2 = command.getBytes();
				DatagramPacket op2 = new DatagramPacket(buf2, buf2.length, target, 8889);
				ds.send(op2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		isStopped = true;
	}

	/**
	 * 表演
	 */
	public void performStart() {
		new Thread() {
			@Override
			public void run() {
				isStopped = false;
				listen.onStart();
				if (ds != null) {
					InetAddress target;
					try {
						target = InetAddress.getByName("192.168.10.1");
						String[] perform_str = { "takeoff", "up 20", "flip l", "flip r", "land" };
						for (int i = 0; i < perform_str.length; i++) {
							if (isStopped == false) {
								byte[] buf2 = perform_str[i].getBytes();
								DatagramPacket op2 = new DatagramPacket(buf2, buf2.length, target, 8889);
								ds.send(op2);
								try {
									for (int j = 0; j < 50 && !isStopped; j++) {
										Thread.sleep(100);
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							/**
							 * 如果isStopped执行，执行着陆操作
							 */
						}
						/*
						 * System.out.print("a"); send("着陆");
						 */
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				listen.onStop();
			}
		}.start();
	}

	/**
	 * 接收回复
	 */
	public void receiver2() {
		new Thread() {
			@Override
			public void run() {
				try {
					System.out.println("run");
					InetSocketAddress address = new InetSocketAddress("0.0.0.0", 8889);
					ds = new DatagramSocket(address);
					byte[] buf = new byte[1024];
					DatagramPacket dp = new DatagramPacket(buf, buf.length);
					while (true) {
						ds.receive(dp);
						String str2 = new String(dp.getData(), 0, dp.getLength());
						System.out.println(str2 + "：8889");
						if (str2 != null) {
							if (m != null) {
								m.print(str2);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				ds.close();
			}
		}.start();
	}

	/**
	 * 设置消息监听
	 * 
	 * @param l
	 */
	public void setMsgPrinter(Receive l) {
		this.l = l;
	}

	public void setMsgPrinter2(Receive2 m) {
		this.m = m;
	}

	public void setPerformlisten(PerformAction p) {
		this.listen = p;
	}

}
