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
<body class="container">

    <%@ page import="fr.univ.lille.s4a021.dao.ChannelDAO" %>
    <%@ page import="fr.univ.lille.s4a021.dto.Channel" %>

    <%@ page import="java.util.List" %>
    <%@ page import="fr.univ.lille.s4a021.model.bdd.Util" %>
    <%@ page import="fr.univ.lille.s4a021.dao.UserDAO" %>
    <%@ page import="java.sql.SQLException" %>

    <%
        if (!Util.userIsConnected(session)) {
            response.sendRedirect("index.jsp");
            return;
        }
    %>

    <a href="logout" class="btn btn-danger">Logout</a>


    <H1 class="mt-4">Channels</H1>
    <a href="createChannel.jsp" class="btn btn-primary mb-3">Create a channel</a>
    <%
        ChannelDAO channelDAO = new ChannelDAO();
        List<Channel> channels = channelDAO.getAllChannels();
        if (channels != null) {
    %>
    <%
            UserDAO userDAO = new UserDAO();
            for (Channel channel : channels) {
                boolean estAbonne = false;
                try {
                    estAbonne = userDAO.estAbonne((int) session.getAttribute("id"), channel.getCid());
                } catch (SQLException e) {
                    response.sendRedirect("home.jsp");
                }
                if (!estAbonne) {
                    continue;
                }
    %>
    <a href="channel.jsp?channelID=<%=channel.getCid()%>" class="list-group-item list-group-item-action">
        <h2 class="h5"><%=channel.getName()%></h2>
    </a>
    <%
            }
        }
    %>

</body>
</html>