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


<%@ page import="java.util.List" %>
<%@ page import="fr.univ.lille.s4a021.dto.User" %>
<%@ page import="fr.univ.lille.s4a021.controller.AbstractController" %>

<%
    try {
        List<User> users = (List<User>) request.getAttribute("users");
%>

<%@ include file="components/TopBar.jsp" %>


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

<%
    } catch (Exception e) {
        request.setAttribute("message", e.getMessage());
        request.setAttribute("errorCode", AbstractController.getErrorCode(e));
        request.setAttribute("exception", e);

        if (!response.isCommitted()) { %>

            <jsp:forward page="error.jsp"/>

        <% } else {
            e.printStackTrace();
            out.println("An error occurred: " + e.getMessage());
            }
    }
%>

</html>