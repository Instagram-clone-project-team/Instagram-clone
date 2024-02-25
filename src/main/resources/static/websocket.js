// 웹소켓 연결을 위한 함수
function connect() {
    var socket = new SockJS('http://localhost:8080/ws-connection');
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);

        // 서버에서 보낸 메시지를 화면에 표시하기 위한 콜백 함수
        stompClient.subscribe('/sub/messages', function(response) {
            var message = JSON.parse(response.body);
            document.getElementById('messages').innerHTML += '<p>' + message.content + '</p>';
        });
    });
}

// 서버로 메시지를 보내기 위한 함수
function sendMessage() {
    var socket = new SockJS('http://localhost:8080/ws-connection');
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);

        // 서버로 메시지를 전송
        stompClient.send('/pub/messages', {}, JSON.stringify({
            roomId: 1,
            senderId: 4,
            content: document.getElementById('messageInput').value
        }));
    });
}

// 서버로 메시지 삭제 요청 보내기 위한 함수
function deleteMessage() {
    var socket = new SockJS('http://localhost:8080/ws-connection');
    var stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);

        // 서버로 메시지 삭제 요청 전송
        stompClient.send('/pub/messages/delete', {}, JSON.stringify({
            messageId: 1, // 메시지 ID
            memberId: 4 // 회원 ID
        }));
    });
}