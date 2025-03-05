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
        String channelID = request.getParameter("channelID");
        if (channelID == null) {
            response.sendRedirect("home");
        }

        ChannelDAO channelDAO = new ChannelDAO();
        Channel channel = channelDAO.getChannelById(Integer.parseInt(channelID));

        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.getAllUsers();
        List<User> abonnes = channelDAO.getAbonnes(channel.getCid());
    %>

    <a href="logout" class="btn btn-danger mb-3">Logout</a>

    <section id="channel">
        <a href="home?action=view&channelID=<%=channel.getCid()%>" class="btn btn-primary mb-3">Back</a>
        <h1 class="mt-4">Edit <%=channel.getName()%></h1>
        <form action="channel" method="get">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="channelID" value="<%=channel.getCid()%>">
            <div class="form-group">
                <label for="name">Name</label>
                <input type="text" class="form-control" id="name" name="name" value="<%=channel.getName()%>">
            </div>

            <div id="abonneList">
                <h2>Abonnes</h2>
                <ul>
                    <% for (User user : users) {
                        if (user.getUid() == (int) session.getAttribute("id")) {
                            continue;
                        }

                    %>

                        <li>
                            <input type="checkbox" name="users" value="<%=user.getUid()%>"
                                <% for (User abonne : abonnes) {
                                    if (abonne.getUid() == user.getUid()) {
                                        out.print("checked");
                                    }
                                } %>
                            >
                            <%=user.getUsername()%>
                        </li>
                    <% } %>
                </ul>

            </div>

            <div id="adminList">
                <%
                List<User> admins = channelDAO.getAdmins(channel.getCid());
                %>

                <h2>Admins</h2>
                <ul>
                    <% for (User user : abonnes) {
                        if (user.getUid() == (int) session.getAttribute("id")) {
                            continue;
                        }
                    %>

                        <li>
                            <input type="checkbox" name="admins" value="<%=user.getUid()%>"
                                <% for (User admin : admins) {
                                    if (admin.getUid() == user.getUid()) {
                                        out.print("checked");
                                    }
                                } %>
                            >
                            <%=user.getUsername()%>
                        </li>
                    <% } %>
            </div>


            <button type="submit" class="btn btn-primary">Submit</button>
            <a href="channel?action=delete&channelID=<%=channel.getCid()%>" class="btn btn-danger">Delete</a>
        </form>
    </section>




</body>
</html>