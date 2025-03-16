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
    <%@ page import="fr.univ.lille.s4a021.dto.Channel" %>
    <%@ page import="java.util.List" %>
    <%@ page import="fr.univ.lille.s4a021.dto.User" %>
    <%@ page import="java.util.Map" %>
    <%@ page import="fr.univ.lille.s4a021.controller.AbstractController" %>

    <%
        try {

        Channel channel = (Channel) request.getAttribute("channel");
        List<User> users = (List<User>) request.getAttribute("users");
        List<User> subscribers = (List<User>) request.getAttribute("subscribers");
        List<User> admins = (List<User>) request.getAttribute("admins");
    %>

    <%@ include file="components/TopBar.jsp" %>


    <section id="channel">
        <h1 class="mt-4">Edit <%=channel.getName()%></h1>
        <form action="channel" method="get">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="channelID" value="<%=channel.getCid()%>">
            <div class="form-group">
                <label for="name">Name</label>
                <input type="text" class="form-control" id="name" name="name" value="<%=channel.getName()%>">
            </div>
            <div class="form-group">
                <label for="expiration">Minute before expiration Expiration</label>
                <%
                    Map<Integer, String> listChoices = Map.of(
                            0, "No expiration",
                            1, "1 minute",
                            5, "5 minutes",
                            10, "10 minutes",
                            30, "30 minutes",
                            60, "1 hour",
                            120, "2 hours",
                            1440, "1 day",
                            10080, "1 week",
                            43200, "1 month"
                    );
                    boolean vlueIsInList = listChoices.containsKey(channel.getMinuteBeforeExpiration());
                %>
                <select class="form-control" id="expiration" name="expiration">
                    <% for (Integer key : listChoices.keySet().stream().sorted().toList()) { %>
                    <option value="<%=key%>" <% if (key == channel.getMinuteBeforeExpiration()) {
                        out.print("selected");
                    } %>>
                        <%=listChoices.get(key)%>
                    </option>
                    <% } %>
                    <% if (!vlueIsInList) { %>
                    <option value="<%=channel.getMinuteBeforeExpiration()%>" selected>
                        <%=channel.getMinuteBeforeExpiration()%> minutes
                    </option>
                    <% } %>

                </select>
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
                                <% for (User abonne : subscribers) {
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
                <h2>Admins</h2>
                <ul>
                    <% for (User user : subscribers) {
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