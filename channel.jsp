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

    <%@ page import="fr.univ.lille.s4a021.dao.ChannelDAO" %>
    <%@ page import="fr.univ.lille.s4a021.dto.Channel" %>
    <%@ page import="java.util.List" %>
    <%@ page import="fr.univ.lille.s4a021.dto.Message" %>
    <%@ page import="fr.univ.lille.s4a021.model.bdd.Util" %>
    <%@ page import="fr.univ.lille.s4a021.dao.UserDAO" %>
    <%@ page import="java.sql.SQLException" %>
    <%@ page import="fr.univ.lille.s4a021.dto.User" %>

    <%
        if (!Util.userIsConnected(session)) {
            response.sendRedirect("index.jsp");
        }
    %>

    <a href="home.jsp" class="btn btn-primary mb-3">Back</a>

    <h1 class="mb-4">Channel</h1>
    <%
        String channelIdParam = request.getParameter("channelID");
        if (channelIdParam != null) {
            boolean estAbonne = false;
            try {
                estAbonne = new UserDAO().estAbonne((int) session.getAttribute("id"), Integer.parseInt(channelIdParam));
            } catch (SQLException e) {
                response.sendRedirect("home.jsp");
            }
            if (!estAbonne) {
                response.sendRedirect("home.jsp");
            }
            int channelID = Integer.parseInt(channelIdParam);
            ChannelDAO channelDAO = new ChannelDAO();
            Channel channel = channelDAO.getChannelById(channelID);
            if (channel != null) {
    %>
        <h2 class="mb-4"><%=channel.getName()%></h2>
    <%
                List<Message> messages = channel.getMessages();
                if (messages != null) {
                    for (Message message : messages) {
                        User user = new User(-1, "Unknown", "Unknown", "Unknown");
                        try {
                            user = new UserDAO().getUserById(message.getSenderId());
                        } catch (SQLException e) {
                            response.sendRedirect("home.jsp");
                        }
            %>
            <div class="border p-3 mb-3 rounded">
                <span class="font-weight-bold text-dark"><%=user.getUsername()%></span>
                <p class="my-2 text-muted"><%=message.getContenu()%></p>
            </div>

            <%
                    }
                }
            }
    %>
        <form action="send" method="POST" class="mt-4">
            <input type="hidden" name="channelID" value="<%=channelID%>">

            <div class="form-group">
                <input type="text" class="form-control" name="message" placeholder="Enter your message">
            </div>
            <button type="submit" class="btn btn-primary">Send</button>
        </form>
    <%
        }
    %>

</body>
</html>