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
    if (message.length === 0) {
        $('#failMessage').removeClass("invisible");
    } else {
        // Replace link with new one
        $(".rdfLinks").remove();
        $("#downloadLink").append("<a class='rdfLinks' href='" + message + "'>Download Triples</a>");
        $('#extractButton').removeClass("running");
        $('#extractButton').attr("disabled", false);
    }
}

$(document).ready(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    connect();

    $('#extractButton').click(function () {
        // Remove failed message
        $('#failMessage').addClass("invisible");

        var selected = [];
        $('#extractorForm input:checked').each(function () {
            selected.push($(this).attr('value'));
        });
        sendName(selected);

        // Start loading
        if (selected.length > 0) {
            $(this).addClass("running");
            $(this).attr("disabled", true);
        }
    })
});