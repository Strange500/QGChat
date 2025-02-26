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
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

</head>
<body class="container">
    <%@ page import="fr.univ.lille.s4a021.dao.ChannelDAO" %>
    <%@ page import="fr.univ.lille.s4a021.dto.Channel" %>
    <%@ page import="java.util.List" %>
    <%@ page import="fr.univ.lille.s4a021.model.bdd.Util" %>
    <%@ page import="fr.univ.lille.s4a021.dao.UserDAO" %>
    <%@ page import="java.sql.SQLException" %>
    <%@ page import="fr.univ.lille.s4a021.dto.Message" %>
    <%@ page import="fr.univ.lille.s4a021.dto.User" %>

    <%
        if (!Util.userIsConnected(session)) {
            response.sendRedirect("index.jsp");
            return;
        }
    %>

    <a href="logout" class="btn btn-danger mb-3">Logout</a>

    <div class="row">
        <div class="col-md-4">
            <section id="channels">
                <h1 class="mt-4">Channels</h1>


                <a href="createChannel.jsp" class="btn btn-primary mb-3"><i class="bi bi-plus"></i></a>
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
                            return;
                        }
                        if (!estAbonne) {
                            continue;
                        }
                %>
                <a href="home.jsp?channelID=<%=channel.getCid()%>" class="list-group-item list-group-item-action">
                    <h2 class="h5"><%=channel.getName()%></h2>
                </a>
                <%
                    }
                }
                %>
            </section>
        </div>

        <div class="col-md-8">
            <section id="conversation">
                <%
                    String channelIdParam = request.getParameter("channelID");
                    if (channelIdParam != null) {
                        boolean estAbonne = false;
                        try {
                            estAbonne = new UserDAO().estAbonne((int) session.getAttribute("id"), Integer.parseInt(channelIdParam));
                        } catch (SQLException e) {
                            response.sendRedirect("home.jsp");
                            return;
                        }
                        if (!estAbonne) {
                            return;
                        }
                        int channelID = Integer.parseInt(channelIdParam);
                        Channel channel = channelDAO.getChannelById(channelID);
                        if (channel != null) {
                %>
                <h2 class="mb-4"><%=channel.getName()%></h2>
<div id="messageList" class="overflow-auto" style="max-height: 400px;">
<%
            List<Message> messages = channel.getMessages();
            if (messages != null && !messages.isEmpty()) {
                for (Message message : messages) {
                    User user = new User(-1, "Unknown", "Unknown", "Unknown");
                    try {
                        user = new UserDAO().getUserById(message.getSenderId());
                    } catch (SQLException e) {
                        response.sendRedirect("home.jsp");
                        return;
                    }
                %>
                <div class="border p-3 mb-3 rounded">
                    <span class="font-weight-bold text-dark"><%=user.getUsername()%></span>
                    <p class="my-2 text-muted"><%=message.getContenu()%></p>
                </div>
    <%
        }
    } else {
    %>
    <div class="alert alert-info">No messages yet</div>
    <%
            }
        }
    %>
</div>
<script>
    const messageList = document.getElementById('messageList');
    messageList.scrollTop = messageList.scrollHeight;
</script>
                <form action="send" method="POST" class="mt-4">
                    <input type="hidden" name="channelID" value="<%=channelID%>">
                    <div class="form-group">
                        <input type="text" class="form-control" name="message" placeholder="Enter your message">
                    </div>
                    <button type="submit" class="btn btn-primary"><i class="bi bi-send"></i></button>
                </form>
                <%
                    }
                %>
            </section>
        </div>
    </div>
</body>
</html>