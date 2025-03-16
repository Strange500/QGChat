<%@ page import="fr.univ.lille.s4a021.dto.Channel" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport"
        content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>Document</title>
  <!-- import bootstreap -->
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
</head>
<body class="container mt-5">


<%

  Channel ch = (Channel) request.getAttribute("channel");
  String url = (String) request.getAttribute("url");

%>

<%@ include file="components/TopBar.jsp" %>


<h1 class="mb-4">Share Channel <%=ch.getName()%></h1>

<div class="alert alert-info" role="alert">
  <strong>Share this link:</strong>
  <small>This link is valid for 1 hour, anyone with this link can join the channel</small>
  <input type="text" class="form-control" value="<%= url %>" readonly onclick="this.select();">
</div>

<div id="qrcode" class="d-flex justify-content-center my-4"></div>

<script defer>
    var qrcode = new QRCode(document.getElementById("qrcode"), {
        text: "<%= url %>",
        width: 256,
        height: 256,
        colorDark: "#000000",
        colorLight: "#ffffff",
        correctLevel: QRCode.CorrectLevel.Q
    });
</script>


</html>