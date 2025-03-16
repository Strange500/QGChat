<%@ page import="fr.univ.lille.s4a021.dto.Channel" %>
<%

  Channel ch = (Channel) request.getAttribute("channel");
  String url = (String) request.getAttribute("url");
  String title = "Share Channel " + ch.getName();

%>
<!doctype html>
<html lang="en">
<head>
    <%@include file="components/head.jsp"%>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
</head>
<body class="container mt-5">

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