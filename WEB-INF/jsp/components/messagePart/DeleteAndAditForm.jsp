<div class="d-flex">
    <% if (!isImgMessage && !isVideoMessage && !isAudioMessage) { %>
    <a href="?channelID=<%=message.getChannelId()%>&editMid=<%=message.getMid()%>"
       class="btn btn-link p-0"><i class="bi bi-pencil"></i></a>
    <% } %>
    <form action="message?action=delete" method="POST">
        <input type="hidden" name="mid" value="<%=message.getMid()%>">
        <button type="submit" class="btn btn-link p-0"><i
                class="bi bi-trash"></i></button>
    </form>
</div>