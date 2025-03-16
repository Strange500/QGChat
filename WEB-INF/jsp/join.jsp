<%@ page import="fr.univ.lille.s4a021.controller.AbstractController" %>
<%@ page import="fr.univ.lille.s4a021.dto.Channel" %>
<%@ page import="fr.univ.lille.s4a021.util.JwtManager" %>
<%@ page import="fr.univ.lille.s4a021.util.Pair" %>
<%@ page import="io.jsonwebtoken.JwtException" %>
<%@ page import="fr.univ.lille.s4a021.dto.User" %>
<%@ page import="fr.univ.lille.s4a021.dao.UserDAO" %>
<%@ page import="fr.univ.lille.s4a021.dao.ChannelDAO" %>
<%@ page import="fr.univ.lille.s4a021.exception.ConfigErrorException" %>
<%@ page import="fr.univ.lille.s4a021.Config" %>
<%@ page import="fr.univ.lille.s4a021.exception.dao.user.UserNotFoundException" %>
<%@ page import="fr.univ.lille.s4a021.exception.dao.channel.ChannelNotFoundException" %>
<%@ page import="fr.univ.lille.s4a021.exception.dao.DataAccessException" %>
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
</head>
<body class="container mt-5">


<%
    String token = (String) request.getAttribute("token");
    User user = (User) request.getAttribute("user");
    Channel channel = (Channel) request.getAttribute("channel");
%>


<%@ include file="components/TopBar.jsp" %>

<div class="card">
  <div class="card-header">
    <h1 class="mb-4">Accept Channel Invitation</h1>
  </div>
  <div class="card-body">
    <p>You have been invited to join the channel "<%=channel.getName()%>" by <%=user.getUsername()%></p>
      <form action="channel" method="get">
          <input type="hidden" name="action" value="acceptInvite">
      <input type="hidden" name="token" value="<%=token%>">
      <button type="submit" class="btn btn-primary">Accept</button>
    </form>
  </div>
</div>



</body>

</html>