package connectDB;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import justChatClient.ClientGUI;
import justChatServer.ServerMain;

import javax.swing.JList;

public class ChatRoomGUI extends JFrame implements Runnable {

	private JPanel contentPane;
	private JFrame frame;
	JTextArea inChatTxt;
	private JTextField inputTxtx;
	private JTextField textField_1;
	private JList inRoomList;
	private DefaultListModel inListModel;
	
	Color color = new Color(0x00CED1);
	String roomName, nickname, check;
	Socket socket;
	DataInputStream inStream;
	DataOutputStream outStream;
	ServerMain server;
	

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatRoomGUI frame = new ChatRoomGUI(null,null,null,null,null,null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ChatRoomGUI(Socket s, String room, String nick, 
		String id, String pw, String logincheck) throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 775, 580);
		setTitle("Just Chat!");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(color);
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.roomName = room;
		this.nickname = nick;
		this.check = this.roomName;
		socket = new Socket("127.0.0.1", 5570);
		this.inStream = new DataInputStream(socket.getInputStream());
		this.outStream = new DataOutputStream(socket.getOutputStream());
		outStream.writeUTF("saveRoom|"+roomName);
		outStream.writeUTF("roomResetList|");
		outStream.writeUTF("roomUpdateList|");
		outStream.writeUTF("setChatList|"+nickname+"/"+roomName);
		outStream.writeUTF("chatNameReset|"+roomName);
		outStream.writeUTF("allChatName|"+roomName+"/"+nickname);
		
		JLabel roomLb = new JLabel("채팅창");
		roomLb.setHorizontalAlignment(SwingConstants.CENTER);
		roomLb.setFont(new Font("Dialog", Font.BOLD, 20));
		roomLb.setBounds(38, 29, 76, 29);
		contentPane.add(roomLb);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(38, 70, 376, 342);
		contentPane.add(scrollPane);
		
		// 채팅창
		inChatTxt = new JTextArea();
		// allChatTxt.setText("settext");
		scrollPane.setViewportView(inChatTxt);
		inChatTxt.setEditable(false);
		
		JLabel listLb = new JLabel("접속자 목록");
		listLb.setHorizontalAlignment(SwingConstants.CENTER);
		listLb.setFont(new Font("Dialog", Font.BOLD, 20));
		listLb.setBounds(565, 29, 156, 29);
		contentPane.add(listLb);
		
		JButton newRoomBtn = new JButton("귓속말");
		newRoomBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String select = inRoomList.getSelectedValue().toString();
				if(select.equals(nickname)) { // 자기 이름을 선택할 시
					JOptionPane.showMessageDialog(null, "다른 사람을 선택해주세요.");
				}else {
					try {// 귓속말 입력받아 해당 사람에게 보내기
						String str = JOptionPane.showInputDialog("귓속말 입력▼▼",
								JOptionPane.OK_OPTION);
						if(!str.equals(null))  // 입력한 내용 있을 때
							outStream.writeUTF("secretChat|"+roomName+"/"+select
									+"/[ " + nickname+ "(귓속말) ] " + str);
					} catch (IOException e1) {
						//e1.printStackTrace();
					}
				// 기타 귓속말 전송
				}
			}
		});
		newRoomBtn.setFont(new Font("Dialog", Font.BOLD, 18));
		newRoomBtn.setBounds(565, 451, 157, 23);
		contentPane.add(newRoomBtn);
		
		// ========== 나가기 Btn ==========
		JButton returnBtn = new JButton("나가기"); 
		returnBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(null, "나가시겠습니까?",
						"나가기",JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					try { // ----------------- 나가기 버튼 ------------------
						outStream.writeUTF("portCheck|");
						outStream.writeUTF("roomResetList|");
						outStream.writeUTF("roomUpdateList|");
						outStream.writeUTF("reChatName|"+nickname);
						outStream.writeUTF("chatNameReset|"+roomName);
						outStream.writeUTF("allChatName|"+roomName+"/"+nickname);
						setVisible(false);
						new ClientGUI(id,pw,check).setVisible(true);
					} catch (IOException e1) {}
				}
			}
		});
		returnBtn.setFont(new Font("Dialog", Font.BOLD, 18));
		returnBtn.setBounds(564, 492, 157, 23);
		contentPane.add(returnBtn);
		
		// 채팅 작성하는 곳
		inputTxtx = new JTextField();
		inputTxtx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					outStream.writeUTF("chatting|"+roomName+"/[ " + nickname + " ] "
							+inputTxtx.getText());
				} catch (IOException ee) {}
				inputTxtx.setText("");
			}
		});
		inputTxtx.setFont(new Font("Dialog", Font.BOLD, 20));
		inputTxtx.setColumns(10);
		inputTxtx.setBounds(81, 449, 333, 22);
		contentPane.add(inputTxtx);

		// 접속자 목록
		inListModel = new DefaultListModel();
		inRoomList = new JList(inListModel);
		inRoomList.setBounds(565, 70, 156, 342);
		contentPane.add(inRoomList);
		
		JLabel chatinputLb = new JLabel("▶▶");
		chatinputLb.setHorizontalAlignment(SwingConstants.CENTER);
		chatinputLb.setBounds(38, 449, 50, 22);
		contentPane.add(chatinputLb);
		
		JButton newRoomBtn_1_1 = new JButton("\uCCA8\uBD80");
		newRoomBtn_1_1.setFont(new Font("굴림", Font.BOLD, 13));
		newRoomBtn_1_1.setBounds(12, 492, 68, 23);
		contentPane.add(newRoomBtn_1_1);
		
		textField_1 = new JTextField();
		textField_1.setFont(new Font("Dialog", Font.BOLD, 20));
		textField_1.setColumns(10);
		textField_1.setBounds(81, 491, 333, 22);
		contentPane.add(textField_1);
		
		Thread th = new Thread(this);
		th.start();
		System.out.println(s);
	}
	
	@Override // 받는 쪽은 스레드로 구현해야 한다. : 서버가 나에게 언제 전달해 줄 지 모르는 일이므로 while
	public void run() {
		try {
			while(true) {
				String rcvChatRoomMsgs = inStream.readUTF();
				System.out.println("채팅방 확인용:"+rcvChatRoomMsgs);
				String[] rcvChatRoomMsg = rcvChatRoomMsgs.split("\\|");
				String number = rcvChatRoomMsg[0]; 
				System.out.println(number);
				switch(number) {
				case "chatNameReset":
					inListModel.removeAllElements();
					break;
				case "allChatName":
					inListModel.addElement(rcvChatRoomMsg[1]);
					break;
				case "chatting": 
					inChatTxt.append(rcvChatRoomMsg[1]+"\n");
					break;
				case "secretChat":
					inChatTxt.append(rcvChatRoomMsg[1]+"\n");
					break;
				}
			}
		}catch (Exception e) {
			//e.printStackTrace();
		}
	}
}
