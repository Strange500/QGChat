<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="fr.univ.lille.s4a021.controller.AbstractController" %>
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
    <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
</head>
<body class="container mt-5">

<%@ include file="components/TopBar.jsp" %>


<h1 class="mb-4">Create friend request</h1>

  <%
  try {
  List<User> notFriends = (List<User>) request.getAttribute("notFriends");
  Map<Integer, String> base64ProfilePictures = (Map<Integer, String>) request.getAttribute("base64ProfilePictures");
%>

<form action="user" method="get">
  <input type="hidden" name="action" value="sendFriendRequest">

  <div class="form-group">
    <label for="friend">Select a friend</label>
    <select class="form-control" id="friend" name="uid">
      <%
        for (User user : notFriends) {
      %>
      <option value="<%= user.getUid() %>">
        <%= user.getUsername() %>
      </option>
      <%
        }
      %>
    </select>

    <button type="submit" class="btn btn-primary mt-3">Send friend request</button>

  </div>

</form>

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