<%@ page import="fr.univ.lille.s4a021.dto.User" %>
<%
    String UserProfilePicture = (String) request.getAttribute("UserProfilePicture");
    User currentUser = (User) request.getAttribute("currentUser");
%>

<div class="container mt-4">
    <a href="home?action=logout" class="btn btn-danger mb-3">Logout</a>

    <section class="text-left mb-4">
        <div class="d-flex align-items-center">
            <a href="user?action=edit" class="d-inline-block position-relative" id="profileLink">
                <img src="data:image/jpeg;base64,<%=UserProfilePicture%>" alt="profile picture"
                     class="img-fluid rounded-circle" style="width: 80px; height: 80px; object-fit: cover;">
                <i class="bi bi-pencil"
                   style="position: absolute; bottom: 50%; right: 50%; transform: translate(50%, 50%); font-size: 3em; background: rgba(127,127,127,0.5); border-radius: 50%; height: 100%; width: 100%; padding: 5% 0 0 15%; display: none;">
                </i>
            </a>
            <p class="ml-3 mt-2 mb-0 h5"><%=currentUser.getUsername()%>
            </p>
        </div>
    </section>
</div>