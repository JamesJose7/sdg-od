var stompcCient = null

var destination = '/topic/transform-ckan/' + socketUri;
var data = "/app/sendCkanUrls/" + socketUri;

function setConnected(connected) {

}

function connect() {
    var socket = new SockJS(contextPath + '/websocket');
    stompcCient = Stomp.over(socket);
    stompcCient.connect({}, function (frame) {
        setConnected(true);
        // console.log('Connected: ' + frame);
        stompcCient.subscribe(destination, function (greeting) {
            onServerResult(JSON.parse(greeting.body).content);
        })
    })
}

function sendParameters(urls, format, upload, noCache) {
    stompcCient.send(data, {}, JSON.stringify({'ckanUrls': urls, 'format': format, 'upload': upload, 'noCache': noCache}))
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
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

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
        // Get selected format
        var selectedFormat = $("#formatSelector input:checked").attr('value');

        // Get aditional options
        var upload = $("#upload").is(':checked');
        var noCache = $("#no-cache").is(':checked');

        sendParameters(selectedUrls, selectedFormat, upload, noCache);

        // Start loading
        if (selectedUrls.length > 0) {
            $(this).addClass("running");
            $(this).attr("disabled", true);
        }
    })
});