<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
</head>
<body>

    <%@ page import="fr.univ.lille.s4a021.dao.ChannelDAO" %>
    <%@ page import="fr.univ.lille.s4a021.dto.Channel" %>
    <%@ page import="java.util.List" %>
    <%@ page import="fr.univ.lille.s4a021.dto.Message" %>

    <H1>Channels</H1>
    <%
        String channelIdParam = request.getParameter("channelID");
        if (channelIdParam != null) {
            int channelID = Integer.parseInt(channelIdParam);
            ChannelDAO channelDAO = new ChannelDAO();
            Channel channel = channelDAO.getChannelById(channelID);
            if (channel != null) {
    %>
        <h2><%=channel.getName()%></h2>
    <%
                List<Message> messages = channel.getMessages();
                if (messages != null) {
                    for (Message message : messages) {
            %>
                <p><%=message.getContenu()%></p>
            <%
                    }
                }
            }
    %>
        <form action="send" method="POST">
            <input type="hidden" name="channelID" value="<%=channelID%>">

            <input type="text" name="message" placeholder="Enter your message">
            <input type="submit" value="Afficher">
        </form>
    <%
        }
    %>

    <script>
        function sendMessage(message, channelID) {
            const url = "send"
            const data = {
                message: message,
                channelID: channelID
            }
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            .then(response => response.json())
            .then(data => {
                console.log('Success:', data);
            })
        }

        document.querySelector("#sendmsgform").addEventListener("submit", function(event){
            event.preventDefault()
            const formData = new FormData(event.target)
            const message = formData.get("message")
            const channelID = formData.get("channelID")
            sendMessage(message, channelID)
        })
    </script>

</body>
</html>