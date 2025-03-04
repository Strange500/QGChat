<%@ page import="fr.univ.lille.s4a021.controller.MainController" %>
<%@ page import="fr.univ.lille.s4a021.dto.Channel" %>
<%@ page import="fr.univ.lille.s4a021.dao.ChannelDAO" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="fr.univ.lille.s4a021.model.bdd.Util" %>
<%@ page import="fr.univ.lille.s4a021.util.JwtManager" %>
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
  Integer chanelID = Integer.parseInt(request.getParameter("channelID"));
  if (chanelID == null) {
    MainController.sendErrorPage(400, "Bad Request: The channel ID is missing", request, response);
    return;
  }
  Channel ch = null;
  try {
    ch = new ChannelDAO().getChannelById(chanelID);
    if (ch == null) {
      MainController.sendErrorPage(400, "Bad Request: The channel ID is invalid", request, response);
      return;
    }
  } catch (SQLException e) {
    MainController.sendErrorPage(400, "Bad Request: The channel ID is invalid", request, response);
    return;
  }

  int userID = Util.getUid(session);


  String token = new JwtManager().createJwtForChannelLink(userID, chanelID);

  String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/home?action=join&token=" + token;

%>

<a href="home?action=logout" class="btn btn-danger mb-3">Logout</a>

<a href="home" class="btn btn-primary mb-3">Back</a>

<h1 class="mb-4">Share Channel <%=ch.getName()%></h1>

<div class="alert alert-info" role="alert">
  <strong>Share this link:</strong>
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