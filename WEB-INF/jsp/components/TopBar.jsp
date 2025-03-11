<%
    String UserProfilePicture = (String) request.getAttribute("UserProfilePicture");
    User currentUser = (User) request.getAttribute("currentUser");
%>

<a href="?action=logout" class="btn btn-danger mb-3">Logout</a>
<section class="text-left">
    <div class="d-flex align-items-center">
        <a href="user?action=edit" class="d-inline-block position-relative" id="pofileLink">
            <img src="data:image/jpeg;base64,<%=UserProfilePicture%>" alt="profile picture"
                 class="img-fluid rounded-circle" style="width: 80px; height: 80px; object-fit: cover;">
            <i class="bi bi-pencil"
               style="position: absolute; bottom: 50%; right: 50%; transform: translate(50%, 50%); font-size: 3em; background: rgba(127,127,127,0.5); border-radius: 50%; height: 100%; width: 100%; padding: 5% 0 0 15%; display: none;"></i>
        </a>
        <p class="ml-3 mt-2 mb-0"><%=currentUser.getUsername()%>
        </p>
    </div>
</section>