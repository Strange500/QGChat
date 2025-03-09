<%@ page import="fr.univ.lille.s4a021.controller.MainController" %>
<%@ page import="fr.univ.lille.s4a021.dto.Channel" %>
<%@ page import="fr.univ.lille.s4a021.dao.impl.ChannelDAOSql" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="fr.univ.lille.s4a021.util.JwtManager" %>
<%@ page import="fr.univ.lille.s4a021.util.Pair" %>
<%@ page import="io.jsonwebtoken.JwtException" %>
<%@ page import="fr.univ.lille.s4a021.dto.User" %>
<%@ page import="fr.univ.lille.s4a021.dao.impl.UserDAOSql" %>
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
    UserDAO userDAO = null;
    ChannelDAO channelDAO = null;

    try {
        userDAO = Config.getConfig().getUserDAO();
        channelDAO = Config.getConfig().getChannelDAO();
    } catch (ConfigErrorException e) {
        MainController.sendErrorPage(500, e.getMessage(), request, response);
        return;
    }

  String token = request.getParameter("token");
  if (token == null) {
      MainController.sendErrorPage(400, "Bad Request: The token is missing", request, response);
      return;
  }
    Pair<Integer, Integer> pair = null;
    try {
        pair = new JwtManager().getUidAndCidFromChannelInviteToken(token)   ;
    } catch (JwtException e) {
        MainController.sendErrorPage(400, "Bad Request: " + e.getMessage(), request, response);
        return;
    }


  if (pair == null) {
      MainController.sendErrorPage(400, "Bad Request: The token is invalid", request, response);
      return;
  }

  int userID = pair.getFirst();
  int channelID = pair.getSecond();

    User user = null;
    Channel channel = null;
    try {
        user = userDAO.getUserById(userID);
        channel = channelDAO.getChannelById(channelID);
    } catch (UserNotFoundException | ChannelNotFoundException e) {
        MainController.sendErrorPage(404, e.getMessage(), request, response);
        return;
    } catch (DataAccessException e) {
        MainController.sendErrorPage(500, e.getMessage(), request, response);
        return;
    }

%>

<a href="home?action=logout" class="btn btn-danger mb-3">Logout</a>

<a href="home" class="btn btn-primary mb-3">Back</a>

<div class="card">
  <div class="card-header">
    <h1 class="mb-4">Accept Channel Invitation</h1>
  </div>
  <div class="card-body">
    <p>You have been invited to join the channel "<%=channel.getName()%>" by <%=user.getUsername()%></p>
    <form action="join" method="get">
      <input type="hidden" name="action" value="join">
      <input type="hidden" name="token" value="<%=token%>">
      <button type="submit" class="btn btn-primary">Accept</button>
    </form>
  </div>
</div>



</body>

</html>