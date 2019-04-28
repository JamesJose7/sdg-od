var stompcCient = null

function setConnected(connected) {

}

function connect() {
    var socket = new SockJS('/websocket');
    stompcCient = Stomp.over(socket);
    stompcCient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompcCient.subscribe('/topic/transform-ckan', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        })
    })
}

function sendName(urls) {
    stompcCient.send("/app/sendCkanUrls", {}, JSON.stringify({'ckanUrls': urls}))
}

function showGreeting(message) {
    $("#greetings").append("<tr><td><a href='" + message + "'>" + message + "</a></td></tr>");
}

$(document).ready(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    connect();
    $('#extractButton').click(function () {
        var selected = [];
        $('#extractorForm input:checked').each(function () {
            selected.push($(this).attr('value'));
        });
        sendName(selected);
    })
});