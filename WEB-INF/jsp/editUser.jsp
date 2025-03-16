<%@ page import="fr.univ.lille.s4a021.dto.User" %>
<%@ page import="fr.univ.lille.s4a021.controller.AbstractController" %>

<%
  String title = "Edit User";
  try {
    User user = (User) request.getAttribute("user");


%>
<!doctype html>
<html lang="en">
<head>
  <%@include file="components/head.jsp"%>

</head>
<body class="container">


<%@ include file="components/TopBar.jsp" %>

<section id="userInfo" class="mt-5">
  <a href="home" class="btn btn-secondary mb-3">
    <i class="bi bi-arrow-left"></i> Back
  </a>
  <h1 class="mb-4">Edit <%=user.getUsername()%></h1>
  <% if (request.getAttribute("editException") != null) { %>
  <div class="alert alert-danger" role="alert">
    <%= request.getAttribute("editException") %>
  </div>
  <% } %>
  <form action="user" method="get" class="mb-4">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="uid" value="<%=user.getUid()%>">
    <div class="form-group">
      <label for="username">Username</label>
      <input type="text" class="form-control" id="username" name="username" value="<%=user.getUsername()%>" required>
    </div>
    <div class="form-group">
      <label for="email">Email</label>
      <input type="email" class="form-control" id="email" name="email" value="<%=user.getMail()%>" required>
    </div>
    <button type="submit" class="btn btn-primary">
      <i class="bi bi-check-circle"></i> Submit
    </button>
  </form>
  <form action="user" method="POST" enctype="multipart/form-data">
    <input type="hidden" name="action" value="setprofilepic">
    <input type="hidden" name="uid" value="<%=user.getUid()%>">
      <div class="form-group">
        <label for="profilepic">Profile Picture</label>
        <input type="file" class="form-control-file" id="profilepic" name="profilepic" onchange="previewImage(event)">
        <div id="preview-container" class="mt-3" style="display: none;">
          <img id="preview" src="#" alt="Profile Picture Preview" class="img-thumbnail" style="max-width: 200px;">
          <button type="button" class="btn btn-danger mt-2" onclick="dismissPreview()">Dismiss</button>
        </div>
      </div>
      <button type="submit" class="btn btn-primary">
        <i class="bi bi-upload"></i> Submit
      </button>

    </form>
    <form action="user" method="POST" class="mt-4">
      <input type="hidden" name="action" value="delete">
      <input type="hidden" name="uid" value="<%=user.getUid()%>">
      <button type="submit" class="btn btn-danger">
        <i class="bi bi-trash"></i> Delete Account
      </button>
    </form>
</section>
<script>
  function previewImage(event) {
    const previewContainer = document.getElementById('preview-container');
    const preview = document.getElementById('preview');
    preview.src = URL.createObjectURL(event.target.files[0]);
    previewContainer.style.display = 'block';
  }

  function dismissPreview() {
    const previewContainer = document.getElementById('preview-container');
    const preview = document.getElementById('preview');
    preview.src = '#';
    previewContainer.style.display = 'none';
    document.getElementById('profilepic').value = '';
  }
</script>

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
</body>
</html>