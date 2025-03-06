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
  int uid = Util.getUid(session);

  User user = new UserDAO().getUserById(uid);

%>

<a href="logout" class="btn btn-danger mb-3">Logout</a>

<section id="userInfo" class="mt-5">
  <a href="home" class="btn btn-secondary mb-3">
    <i class="bi bi-arrow-left"></i> Back
  </a>
  <h1 class="mb-4">Edit <%=user.getUsername()%></h1>
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




</body>
</html>