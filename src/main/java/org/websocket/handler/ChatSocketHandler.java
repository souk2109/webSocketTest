package org.websocket.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.log4j.Log4j;

@Log4j
public class ChatSocketHandler extends TextWebSocketHandler {
	private static final String FILE_UPLOAD_PATH = "C:/test/websocket";
	List<WebSocketSession> sessionList = new ArrayList<WebSocketSession>();

	// 커넥션이 연결된 후
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessionList.add(session);
		log.info("연결된 세션 : " + session.getId());
		System.out.println("afterConnectionEstablished : " + session);
	}

	// 텍스트 메세지를 받음
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
		log.info(message.getPayload());
		for (WebSocketSession sess : sessionList) {
			sess.sendMessage(new TextMessage(sess.getId() + ": " + message.getPayload()));
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		log.info(exception);
	}

	// 여기서 이미지나 영상 메세지를 받음
	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		ByteBuffer msgBuffer = message.getPayload();
		String fileName = "temp.jpg";
		File dir = new File(FILE_UPLOAD_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(FILE_UPLOAD_PATH, fileName);
		FileOutputStream out = null;
		FileChannel outChannel = null;
		try {
			msgBuffer.flip(); // byteBuffer를 읽기 위해 세팅
			out = new FileOutputStream(file, true); // 생성을 위해 OutputStream을 연다.
			outChannel = out.getChannel(); // 채널을 열고
			msgBuffer.compact(); // 파일을 복사한다.
			outChannel.write(msgBuffer); // 파일을 쓴다.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (outChannel != null) {
					outChannel.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		msgBuffer.position(0);
		// 파일쓰기가 끝나면 이미지를 발송한다.
		for (WebSocketSession sess : sessionList) {
			try {
				sess.sendMessage(new BinaryMessage(msgBuffer));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 소켓 커넥션이 끊긴 후
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessionList.remove(session);
		log.info(session.getId() + "와의 연결이 끊김!");
		System.out.println("채팅방 퇴장자: "+ session.getId());
	}
}
