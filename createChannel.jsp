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
<%@ page import="fr.univ.lille.s4a021.model.bdd.Util" %>
<%@ page import="fr.univ.lille.s4a021.dto.User" %>
<%@ page import="fr.univ.lille.s4a021.dao.UserDAO" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.ArrayList" %>

<%
    if (!Util.userIsConnected(session)) {
        response.sendRedirect("/index.jsp");
    }

    List<User> users = new ArrayList<>();

    try {
        users.addAll(new UserDAO().getAllUsers());
    } catch (SQLException e) {
        response.sendRedirect("/error.jsp");
    }
%>

<a href="logout" class="btn btn-danger mb-3">Logout</a>

<h1 class="mb-4">Create a Channel</h1>

<form action="channel" method="get">
    <input type="hidden" name="action" value="create">

    <div class="form-group">
        <label for="channelName">Channel Name</label>
        <input type="text" class="form-control" id="channelName" name="name" placeholder="Enter the name of the channel">
    </div>

    <div class="form-group">
        <label for="users">Select Users</label>
        <select class="form-control" id="users" name="users" multiple>
            <%
                for (User user : users) {
                    if (user.getUid() == (int) session.getAttribute("id")) {
                        continue;
                    }
            %>
                <option value="<%=user.getUid()%>"><%=user.getUsername()%></option>
            <%
                }
            %>
        </select>
    </div>

    <button type="submit" class="btn btn-primary">Create</button>
</form>

</body>

</html>