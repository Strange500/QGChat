<div class="border p-3 mb-3 rounded">
    <div class="d-flex justify-content-between align-items-center">
        <%@ include file="messagePart/UserProfile.jsp" %>

        <% if (userCanEdit) { %>
        <%@ include file="messagePart/DeleteAndAditForm.jsp" %>
        <% } %>
    </div>

    <% if (isImgMessage) { %>
    <img src="data:image/jpeg;base64,<%= message.getContenu() %>" class="img-fluid my-2" alt="img">
    <% } else { %>
    <% if (messageRequireEdit && userCanEdit) { %>
    <form action="message" method="POST" class="mt-4">
        <input type="hidden" name="action" value="edit">
        <input type="hidden" name="mid" value="<%= message.getMid() %>">
        <input type="text" class="form-control" name="message" value="<%= message.getContenu() %>">
    </form>
    <% } else { %>
    <p class="my-2 text-muted"><%= message.getContenu() %>
    </p>
    <% } %>
    <% } %>

    <%@ include file="messagePart/ReactionForm.jsp" %>
</div>