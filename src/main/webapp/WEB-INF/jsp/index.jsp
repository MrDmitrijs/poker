<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Poker</title>
    <link href="webjars/bootstrap/3.3.6/css/bootstrap.min.css"
          rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/bs/dt-1.10.16/datatables.min.css"/>
    <script src="webjars/jquery/1.9.1/jquery.min.js"></script>
    <script src="webjars/bootstrap/3.3.6/js/bootstrap.min.js"></script>
</head>
<body>

<div class="container">
    <div id="pokerGame"></div>

    <div class="row" style="margin-top: 50px">
        <div class="col-md-4">
            <label for="alpha">Alpha</label>
            <input type="text" id="alpha" value="${alpha}">
        </div>
        <div class="col-md-4">
            <label for="iterations">Iterations</label>
            <input type="text" id="iterations" value="${iterations}">
        </div>
        <div class="col-md-4">
            <label for="epoch">Number of epoch</label>
            <input type="text" id="epoch" value="">
        </div>
        <div class="col-md-4">
            <button type="button" class="btn btn-primary" id="learnButton">Learn</button>
        </div>
    </div>

    <div class="row" style="margin-top: 50px">
        <div class="col-md-4">
            <label for="bank">Bank</label>
            <input type="text" id="bank" value="2000" disabled>
        </div>
    </div>

    <div class="row" style="margin-top: 20px">
        <div class="col-md-4">
            <p>Is player agressive?</p>
            <div class="radio">
                <label><input type="radio" name="aggro" value="1">Yes</label>
            </div>
            <div class="radio">
                <label><input type="radio" name="aggro" value="0">No</label>
            </div>
        </div>
        <div class="col-md-4">
            <label for="bet">Bet</label>
            <input type="text" id="bet">
        </div>
        <div class="col-md-4">
            <label for="cardPower">Power of cards</label>
            <input type="text" id="cardPower">
        </div>
    </div>

    <div class="row">
        <div class="col-md-4">
            <button type="button" class="btn btn-primary" id="playGame">Give a suggestion</button>
        </div>
    </div>

    <div class="row" style="margin-top: 20px">
        <label for="solution">Solution</label>
        <input type="text" id="solution" value="">
    </div>

</div>
<script>
    $('#learnButton').on('click', function () {
        $.ajax({
            url: 'learn',
            data: {
                alpha: $('#alpha').val(),
                iterations: $('#iterations').val()
            },
            success: function (data) {
                $('#epoch').val(data);
            }
        });
    });

    $('#playGame').on('click', function () {
        $.ajax({
            url: 'play',
            data: {
                aggro: $("input[name=aggro]:checked").val(),
                bet: (($('#bet').val() * 100)/ 2000) * 0.01,
                cardPower: $('#cardPower').val()
            },
            success: function (data) {
                $('#solution').val(data);
            }
        });
    });

</script>
</body>
</html>