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
    <%@ page import="fr.univ.lille.s4a021.model.bdd.Util" %>
    <%@ page import="fr.univ.lille.s4a021.dao.UserDAO" %>
    <%@ page import="java.sql.SQLException" %>
    <%@ page import="fr.univ.lille.s4a021.dto.Message" %>
    <%@ page import="fr.univ.lille.s4a021.dto.User" %>
    <%@ page import="fr.univ.lille.s4a021.dto.ImgMessage" %>
    <%@ page import="fr.univ.lille.s4a021.dao.MessageDAO" %>
    <%@ page import="fr.univ.lille.s4a021.util.Pair" %>
    <%@ page import="java.text.SimpleDateFormat" %>
    <%@ page import="java.io.IOException" %>
    <%@ page import="java.util.*" %>

    <div id="hover-div" style="display: none"></div>

    <%!
        private void processMessages(List<Message> messages, Map<Date, String> resultMap, HttpServletResponse response) throws IOException, SQLException {
            for (Message message : messages) {
                StringBuilder sb = new StringBuilder();
                User user = getUserById(message.getSenderId(), response);
                sb.append("<div class=\"border p-3 mb-3 rounded\">");
                sb.append("<span class=\"font-weight-bold text-dark\">").append(user.getUsername()).append("</span>");
                sb.append("<small class=\"text-muted ml-2\">").append(message.getTimeAgo()).append("</small>");
                sb.append("<p class=\"my-2 text-muted\">").append(message.getContenu()).append("</p>");
                sb.append("<form action=\"like\" method=\"POST\" id=\"likeForm\">");
                sb.append("<div style=\"display: none\" id=\"userDiv\">");
                List<String> whoLiked = new MessageDAO().getWhoLiked(message.getMid());
                if (whoLiked != null && !whoLiked.isEmpty()) {
                    sb.append("<span class=\"text-muted\">Liked by: ");
                    for (String u : whoLiked) {
                        sb.append(u).append(", ");
                    }
                    sb.delete(sb.length() - 2, sb.length());
                    sb.append("</span>");
                }
                sb.append("</div>");

                if (new MessageDAO().isLiked(message.getMid())) {

                    sb.append("<input type=\"hidden\" name=\"mid\" value=\"").append(message.getMid()).append("\"><button type=\"submit\" class=\"btn btn-link p-0\"><i class=\"bi bi-star-fill\"></i></button>");

                } else {
                    sb.append("<input type=\"hidden\" name=\"mid\" value=\"").append(message.getMid()).append("\"><button type=\"submit\" class=\"btn btn-link p-0\"><i class=\"bi bi-star\"></i></button>");
                }
                sb.append("</form>");
                sb.append("</div>");
                resultMap.put(message.getDateSend(), sb.toString());
            }
        }

        private void processImgMessages(List<ImgMessage> imgMessages, Map<Date, String> resultMap, HttpServletResponse response) throws IOException, SQLException {
            for (ImgMessage message : imgMessages) {
                StringBuilder sb = new StringBuilder();
                User user = getUserById(message.getSenderId(), response);
                sb.append("<div class=\"border p-3 mb-3 rounded\">");
                sb.append("<span class=\"font-weight-bold text-dark\">").append(user.getUsername()).append("</span>");
                sb.append("<small class=\"text-muted ml-2\">").append(message.getTimeAgo()).append("</small>");
                sb.append("<img src=\"img/").append(message.getImg()).append("\" class=\"img-fluid my-2\">");
                sb.append("<form action=\"like\" method=\"POST\" id=\"likeForm\">");
                sb.append("<div style=\"display: none\" id=\"userDiv\">");
                List<String> whoLiked = new MessageDAO().getWhoLiked(message.getMid());
                if (whoLiked != null && !whoLiked.isEmpty()) {
                    sb.append("<span class=\"text-muted\">Liked by: ");
                    for (String u : whoLiked) {
                        sb.append(u).append(", ");
                    }
                    sb.delete(sb.length() - 2, sb.length());
                    sb.append("</span>");
                }
                sb.append("</div>");

                if (new MessageDAO().isLiked(message.getMid())) {
                    sb.append("<input type=\"hidden\" name=\"mid\" value=\"").append(message.getMid()).append("\"><button type=\"submit\" class=\"btn btn-link p-0\"><i class=\"bi bi-star-fill\"></i></button>");

                } else {
                    sb.append("<input type=\"hidden\" name=\"mid\" value=\"").append(message.getMid()).append("\"><button type=\"submit\" class=\"btn btn-link p-0\"><i class=\"bi bi-star\"></i></button>");
                }
                sb.append("</form>");
                sb.append("</div>");
                resultMap.put(message.getDateSend(), sb.toString());
            }
        }

        private User getUserById(int userId, HttpServletResponse response) throws IOException {
            User user = new User(-1, "Unknown", "Unknown", "Unknown");
            try {
                user = new UserDAO().getUserById(userId);
            } catch (SQLException e) {
                response.sendRedirect("home.jsp");
            }
            return user;
        }
    %>

    <%
        if (!Util.userIsConnected(session)) {
            response.sendRedirect("index.jsp");
            return;
        }
    %>

    <a href="logout" class="btn btn-danger mb-3">Logout</a>

    <div class="row">
        <div class="col-md-4">
            <section id="channels">
                <h1 class="mt-4">Channels</h1>


                <a href="createChannel.jsp" class="btn btn-primary mb-3"><i class="bi bi-plus"></i></a>
                <%
                    ChannelDAO channelDAO = new ChannelDAO();
                    List<Channel> channels = channelDAO.getAllChannels();
                    if (channels != null) {
                %>
                <%
                    UserDAO userDAO = new UserDAO();
                    for (Channel channel : channels) {
                        boolean estAbonne = false;
                        try {
                            estAbonne = userDAO.estAbonne((int) session.getAttribute("id"), channel.getCid());
                        } catch (SQLException e) {
                            response.sendRedirect("home.jsp");
                            return;
                        }
                        if (!estAbonne) {
                            continue;
                        }
                %>
                <a href="home.jsp?channelID=<%=channel.getCid()%>" class="list-group-item list-group-item-action">
                    <h2 class="h5"><%=channel.getName()%></h2>
                </a>
                <%
                    }
                }
                %>
            </section>
        </div>

        <div class="col-md-8">
            <section id="conversation">
                <%
                    String channelIdParam = request.getParameter("channelID");
                    if (channelIdParam != null) {
                        boolean estAbonne = false;
                        try {
                            estAbonne = new UserDAO().estAbonne((int) session.getAttribute("id"), Integer.parseInt(channelIdParam));
                        } catch (SQLException e) {
                            response.sendRedirect("home.jsp");
                            return;
                        }
                        if (!estAbonne) {
                            return;
                        }
                        int channelID = Integer.parseInt(channelIdParam);
                        Channel channel = channelDAO.getChannelById(channelID);
                        if (channel != null) {
                %>
                <div class="d-flex justify-content-between align-items-center">
                    <h2 class="mb-4"><%=channel.getName()%></h2>
                    <a id="editLink" href="ModifChannel.jsp?channelID=<%=channelID%>" class="btn btn-primary mb-3">
                        <i class="bi bi-pencil-square"></i>
                    </a>
                </div>

<div id="messageList" class="overflow-auto" style="max-height: 400px;">
    <%
        List<Message> messages = channel.getMessages();
        Pair<List<ImgMessage>, List<Message>> pair = new MessageDAO().separateImgFromMessage(messages);
        Map<Date, String> resultMap = new HashMap<>();
        if (messages != null && !messages.isEmpty()) {
            processMessages(pair.getSecond(), resultMap, response);
            processImgMessages(pair.getFirst(), resultMap, response);

            List<Date> sortedDates = new ArrayList<>(resultMap.keySet());
            sortedDates.sort(Date::compareTo);
            for (Date date : sortedDates) {
                out.println(resultMap.get(date));
            }
        }

        } else {
    %>
    <div class="alert alert-info">No messages yet</div>
    <%
        }}
    %>
</div>
<script defer>
    const messageList = document.getElementById('messageList');
    messageList.scrollTop = messageList.scrollHeight;

    const likeForms = document.querySelectorAll('#likeForm');
    likeForms.forEach(form => {
        form.addEventListener('mouseenter', (event) => {
            document.getElementById('hover-div').style.display = 'block';
            document.getElementById('hover-div').style.position = 'absolute';

            const rect = form.getBoundingClientRect();
            document.getElementById('hover-div').style.top = rect.top + 'px';
            document.getElementById('hover-div').style.left = rect.left + 20 + 'px';
            document.getElementById('hover-div').style.width = rect.width + 'px';
            document.getElementById('hover-div').style.height = rect.height + 'px';

            const hoverDiv = document.getElementById('hover-div');
            hoverDiv.innerHTML = form.querySelector('#userDiv').innerHTML;

        });

        form.addEventListener('mouseleave', () => {
            document.getElementById('hover-div').style.display = 'none';
        });

    });

</script>
                <form action="send" method="POST" class="mt-4" enctype="multipart/form-data">
                    <input type="hidden" name="channelID" value="<%=channelIdParam%>">
                    <div class="form-group">
                        <input type="text" class="form-control" name="message" placeholder="Enter your message">
                    </div>
                    <input type="file" accept="image/jpeg" class="form-control-file" name="img">
                    <button type="submit" class="btn btn-primary"><i class="bi bi-send"></i></button>
                </form>

            </section>
        </div>
    </div>
</body>
</html>