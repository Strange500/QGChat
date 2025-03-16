<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="fr.univ.lille.s4a021.controller.AbstractController" %>
<%
  String title = "Create friend request";
  try {
    List<User> notFriends = (List<User>) request.getAttribute("notFriends");
    Map<Integer, String> base64ProfilePictures = (Map<Integer, String>) request.getAttribute("base64ProfilePictures");
%>
<!doctype html>
<html lang="en">
<head>
  <%@include file="components/head.jsp"%>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/qrcodejs/1.0.0/qrcode.min.js"></script>
</head>
<body class="container mt-5">

<%@ include file="components/TopBar.jsp" %>


<h1 class="mb-4">Create friend request</h1>



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