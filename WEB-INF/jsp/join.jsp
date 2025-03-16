<%@ page import="fr.univ.lille.s4a021.controller.AbstractController" %>
<%@ page import="fr.univ.lille.s4a021.dto.Channel" %>
<%@ page import="fr.univ.lille.s4a021.dto.User" %>

<%
    String title = "Join Channel";
    try {
        String token = (String) request.getAttribute("token");
        User user = (User) request.getAttribute("user");
        Channel channel = (Channel) request.getAttribute("channel");
%>
<!doctype html>
<html lang="en">
<head>
  <%@include file="components/head.jsp"%>
</head>
<body class="container mt-5">

<%@ include file="components/TopBar.jsp" %>

<div class="card">
  <div class="card-header">
    <h1 class="mb-4">Accept Channel Invitation</h1>
  </div>
  <div class="card-body">
    <p>You have been invited to join the channel "<%=channel.getName()%>" by <%=user.getUsername()%></p>
      <form action="channel" method="get">
          <input type="hidden" name="action" value="acceptInvite">
      <input type="hidden" name="token" value="<%=token%>">
      <button type="submit" class="btn btn-primary">Accept</button>
    </form>
  </div>
</div>

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