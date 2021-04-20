<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
</head>
<body>
	<input type="text" id="msg" value="1212" class="form-control"><br>
	<input type="file" id="fileUpload">
	<button onclick="fileSend()" id="sendFileBtn">파일 전송</button>
	<button id="btn">메세지 전송</button>
	
	


	<script type="text/javascript">
		$("#btn").on("click", function(e) {
			e.preventDefault();

			if (!socket) {
				return;
			}
			let msg = $("input#msg").val();
			// 소캣 메세지 전송
			socket.send(msg);
		});
	</script>

	<script type="text/javascript">
		var socket = null;
		function connect() {
			var was = new WebSocket("ws://localhost:8080/socket/chatSocket");
			socket = was;
			was.onopen = function() {
				console.log('info: connection opened!');

				was.onmessage = function(event) {
					console.log("받은 메세지 : " + event.data + '\n');
				}

				was.onclose = function(event) {
					console.log("Info : connection closed");
					// setTimeout(function() {connect();}, 1000);
				}
				was.onerror = function(err) {
					console.log("Error : connection error");
				}
			}

		};
		connect();
	</script>
	
	<script type="text/javascript">
	function fileSend(){
		var file = document.querySelector("#fileUpload").files[0];
		var fileReader = new FileReader();
		fileReader.onload = function() {
			var param = {
				type: "fileUpload",
				file: file,
				roomNumber: $("#roomNumber").val(),
				sessionId : $("#sessionId").val(),
				msg : $("#chatting").val(),
				userName : $("#userName").val()
			}
			// 누가 보냈는지 확인하기 위해 text 메세지를 보내야 한다.
			socket.send(JSON.stringify(param)); //파일 보내기전 메시지를 보내서 파일을 보냄을 명시한다.

		    arrayBuffer = this.result;
		    socket.send(arrayBuffer); //파일 소켓 전송
		};
		fileReader.readAsArrayBuffer(file);
	}
	</script>
</body>
</html>