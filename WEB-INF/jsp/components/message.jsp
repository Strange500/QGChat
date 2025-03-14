<div class="border p-3 mb-3 rounded">
    <div class="d-flex justify-content-between align-items-center">
        <%@ include file="messagePart/UserProfile.jsp" %>

        <% if (userCanEdit) { %>
        <%@ include file="messagePart/DeleteAndAditForm.jsp" %>
        <% } %>
    </div>

    <div class="message-content">
        <% if (isImgMessage) { %>
        <img src="data:image/jpeg;base64,<%= message.getContenu() %>"
             class="img-fluid my-2"
             alt="img"
             style="max-height: 200px; width: auto;">
        <% } else if (isVideoMessage) { %>
        <video controls class="my-2" style="max-height: 200px; width: 100%;">
            <source src="data:video/mp4;base64,<%= message.getContenu() %>" type="video/mp4">
            Your browser does not support the video tag.
        </video>
        <% } else if (isAudioMessage) { %>
        <audio controls class="my-2 w-100">
            <source src="data:audio/mp3;base64,<%= message.getContenu() %>" type="audio/mp3">
            Your browser does not support the audio tag.
        </audio>
        <% } else { %>
        <% if (messageRequireEdit && userCanEdit) { %>
        <form action="message" method="POST" class="mt-4">
            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="mid" value="<%= message.getMid() %>">
            <input type="text" class="form-control" name="message" value="<%= message.getContenu() %>">
        </form>
        <% } else { %>
        <p class="my-2 text-muted" style="white-space: pre-wrap"><%= message.getContenu() %>
        </p>
        <% } %>
        <% } %>
    </div>

    <%@ include file="messagePart/ReactionForm.jsp" %>
</div>