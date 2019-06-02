var stompcCient = null

var destination = '/topic/extract-ckan/';
var data = "/app/sendCkanApiUrls/";

function setConnected(connected) {

}

function connect() {
    var socket = new SockJS('/websocket');
    stompcCient = Stomp.over(socket);
    stompcCient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompcCient.subscribe(destination, function (greeting) {
            onServerResult(JSON.parse(greeting.body).content);
        })
    })
}

function sendParameters(urls) {
    stompcCient.send(data, {}, JSON.stringify({'ckanUrls': urls}))
}

function onServerResult(message) {
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
    /*$("form").on('submit', function (e) {
        e.preventDefault();
    });*/

    // check first radio button by default
    $("#radio0").prop("checked", true);

    connect();

    $('#extractButton').click(function () {
        // Remove failed message
        $('#failMessage').addClass("invisible");

        // Get selected urls
        var selectedUrls = [];
        $('#ckanUrlSelector input:checked').each(function () {
            selectedUrls.push($(this).attr('value'));
        });
        sendParameters(selectedUrls);

        // Start loading
        if (selectedUrls.length > 0) {
            $(this).addClass("running");
            $(this).attr("disabled", true);
        }
    })
});