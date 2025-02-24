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

    <H1>Channels</H1>
    <%
        ChannelDAO channelDAO = new ChannelDAO();
        List<Channel> channels = channelDAO.getAllChannels();
        if (channels != null) {
    %>
    <%
            for (Channel channel : channels) {
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