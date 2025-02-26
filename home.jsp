<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
</head>
<body>

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

    <a href="logout">Logout</a>


    <H1>Channels</H1>
    <a href="createChannel.jsp">Create a channel</a>
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
        <a href="channel.jsp?channelID=<%=channel.getCid()%>">
            <h2><%=channel.getName()%></h2>
        </a>
    <%
            }
        }
    %>

</body>
</html>