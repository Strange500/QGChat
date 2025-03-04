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
    <%@ page import="fr.univ.lille.s4a021.controller.MainController" %>

    <div id="hover-div"
         class="popover bs-popover-top shadow bg-white rounded"
         style="display: none; position: absolute; z-index: 1000;">
    </div>


    <canvas style="display: none; position: absolute; top: 0; left: 0; height: 100vh; width: 100vw; z-index: 1000;"></canvas>

    <%!
        private String processMessages(List<? extends Message> messages, int uid) throws IOException, SQLException {
            StringBuilder sb = new StringBuilder();
            for (Message message : messages) {

                User user = new UserDAO().getUserById(message.getSenderId());
                sb.append("<div class=\"border p-3 mb-3 rounded\">");
                sb.append("<div class=\"d-flex justify-content-between align-items-center\">");
                sb.append("<span class=\"font-weight-bold text-dark\">").append(user.getUsername()).append("</span>");
                appendDeleteForm(sb, message.getMid());
                sb.append("</div>");
                sb.append("<small class=\"text-muted ml-2\">").append(message.getTimeAgo()).append("</small>");
                if (message.getImg() != null) {
                    sb.append("<img src=\"data:image/jpeg;base64,").append(message.getImg()).append("\" class=\"img-fluid my-2\">");
                } else {
                    sb.append("<p class=\"my-2 text-muted\">").append(message.getContenu()).append("</p>");
                }
                appendLikeForm(sb, message.getMid(), uid);
                sb.append("</div>");
            }
            return sb.toString();
        }
        %>

        <%!

        private void appendLikeForm(StringBuilder sb, int mid, int uid) throws SQLException {
            sb.append("<form action=\"message?action=like\" method=\"POST\" id=\"likeForm\">");
            sb.append("<div style=\"display: none\" id=\"userDiv\">");
            List<String> whoLiked = new MessageDAO().getWhoLiked(mid);
            if (whoLiked != null && !whoLiked.isEmpty()) {
                if (whoLiked.size() > 3) {
                    int size = whoLiked.size();
                    whoLiked = whoLiked.subList(whoLiked.size() - 3, whoLiked.size());
                    whoLiked.add("<br>and " + (size - 3) + " more");
                }
                sb.append("<span class=\"text-muted\">Liked by: ");
                for (String u : whoLiked) {
                    sb.append(u).append(", ");
                }
                sb.delete(sb.length() - 2, sb.length());
                sb.append("</span>");
            }
            else {
                sb.append("<span class=\"text-muted\">No likes yet</span>");
            }
            sb.append("</div>");

            if (new MessageDAO().isLikedByUser(mid, uid)) {
                sb.append("<input type=\"hidden\" name=\"mid\" value=\"").append(mid).append("\"><button type=\"submit\" class=\"btn btn-link p-0\"><i class=\"bi bi-star-fill\"></i></button>");
            } else {
                sb.append("<input type=\"hidden\" name=\"mid\" value=\"").append(mid).append("\"><button type=\"submit\" class=\"btn btn-link p-0\"><i class=\"bi bi-star\"></i></button>");
            }
            sb.append("</form>");
        }

        %>

    <%!
        private void appendDeleteForm(StringBuilder sb, int mid) {
        sb.append("<form action=\"message?action=delete\" method=\"POST\">");
        sb.append("<input type=\"hidden\" name=\"mid\" value=\"").append(mid).append("\">");
        sb.append("<button type=\"submit\" class=\"btn btn-link p-0\"><i class=\"bi bi-trash\"></i></button>");
        sb.append("</form>");
        }
    %>

    <a href="?action=logout" class="btn btn-danger mb-3">Logout</a>

    <div class="row">
        <div class="col-md-4">
            <section id="channels">
                <h1 class="mt-4">Channels</h1>


                <a href="channel?action=createchannel" class="btn btn-primary mb-3"><i class="bi bi-plus"></i></a>
                <%
                    ChannelDAO channelDAO = new ChannelDAO();
                    List<Channel> channels = channelDAO.getAllChannels();
                    if (channels != null) {
                    UserDAO userDAO = new UserDAO();
                    for (Channel channel : channels) {
                        boolean estAbonne = false;
                        try {
                            estAbonne = userDAO.estAbonne((int) session.getAttribute("id"), channel.getCid());
                        } catch (SQLException e) {
                            MainController.sendErrorPage(500, "An error occurred while trying to get the channels", request, response);
                        }
                        if (!estAbonne) {
                            continue;
                        }
                        %>
                            <a href="?action=view&channelID=<%=channel.getCid()%>" class="list-group-item list-group-item-action">
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
                            MainController.sendErrorPage(500, "An error occurred while trying to get the channel", request, response);
                            return;
                        }
                        if (!estAbonne) {
                            return;
                        }
                        int channelID = Integer.parseInt(channelIdParam);
                        Channel channel = null;
                        try {
                            channel = channelDAO.getChannelById(channelID);
                        }catch (SQLException e) {
                            MainController.sendErrorPage(500, "An error occurred while trying to get the channel", request, response);
                            return;
                        }
                        if (channel != null) {
                            %>
                            <div class="d-flex justify-content-between align-items-center">
                                <div class="d-flex align-items-center">
                                    <h2 class="mb-4"><%=channel.getName()%></h2>
                                    <a href="channel?action=deletechannel&channelID=<%=channelID%>" class="btn btn-danger mb-3">
                                        <i class="bi bi-trash"></i>
                                    </a>
                                </div>


                                <div>
                                    <a id="editLink" href="channel?action=modifchannel&channelID=<%=channelID%>" class="btn btn-primary mb-3">
                                        <i class="bi bi-pencil-square"></i>
                                    </a>
                                    <a id="shareLink" href="channel?action=share&channelID=<%=channelID%>" class="btn btn-primary mb-3">
                                        <i class="bi bi-share"></i>
                                    </a>
                                    <a id="quitLink" href="channel?action=quit&channelID=<%=channelID%>" class="btn btn-danger mb-3">
                                        <i class="bi bi-box-arrow-right"></i>
                                    </a>
                                </div>

                            </div>

                            <div id="messageList" class="overflow-auto" style="max-height: 400px;">
                                <%
                                    List<Message> messages = channel.getMessages();
                                    Pair<List<ImgMessage>, List<Message>> pair = new MessageDAO().separateImgFromMessage(messages);
                                    List<Message> messagesList = new ArrayList<>(){{addAll(pair.getSecond()); addAll(pair.getFirst());}};
                                    messages.sort(Comparator.comparing(Message::getDateSend));
                                    if (messages != null && !messages.isEmpty()) {
                                        int uid = (int) session.getAttribute("id");
                                        out.print(processMessages(messagesList, uid));
                                    }

                        } else {
                            %>
                            <div class="alert alert-info">No messages yet</div>
                            <%
                        }
                    %>
                        </div>

                        <form action="message" method="POST" class="mt-4" enctype="multipart/form-data">
                            <input type="hidden" name="action" value="send">
                            <input type="hidden" name="channelID" value="<%=channelIdParam%>">
                            <div class="form-group">
                                <div class="card" style="max-width: 100px; display: none" id="preview">
                                    <div class="card-body p-0">
                                        <a class="position-absolute top-0 end-0 p-1">
                                            <i class="bi bi-x-octagon"></i>
                                        </a>
                                        <img src="" alt="preview" class="img-fluid" >
                                    </div>
                                </div>

                                <input type="text" class="form-control" name="message" placeholder="Enter your message">
                            </div>
                            <input type="file" accept="image/jpeg" class="form-control-file" name="img" id="imgInput" style="display: none;">
                            <a class="btn btn-secondary" onclick="document.getElementById('imgInput').click();">
                                <i class="bi bi-paperclip"></i>
                            </a>
                            <button type="submit" class="btn btn-primary"><i class="bi bi-send"></i></button>
                        </form>
                <%
                    }
                %>

                <% if (request.getAttribute("senderror") != null) { %>
                    <div class="alert alert-danger mt-3" role="alert">
                        <%= request.getAttribute("senderror") %>
                        <% request.removeAttribute("senderror"); %>
                    </div>
                <% } %>

            </section>
        </div>
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

                const hoverDiv = document.getElementById('hover-div');
                hoverDiv.innerHTML = form.querySelector('#userDiv').innerHTML;

            });

            form.addEventListener('mouseleave', () => {
                document.getElementById('hover-div').style.display = 'none';
            });

        });

        const imgInput = document.querySelector('input[type="file"]');
        imgInput.addEventListener('change', () => {
            const previewCard = document.getElementById('preview');
            preview.style.display = 'block';
            const previewImg = previewCard.querySelector('img');
            const reader = new FileReader();
            reader.onload = (e) => {
                previewImg.src = e.target.result;
                previewImg.style.display = 'block';
            };
            reader.readAsDataURL(imgInput.files[0]);

        });

        const previewCardCancelBtn = document.querySelector('#preview a');
        previewCardCancelBtn.addEventListener('click', () => {
            const previewCard = document.getElementById('preview');
            previewCard.style.display = 'none';
            const imgInput = document.querySelector('input[type="file"]');
            imgInput.value = '';
        });

        function displaySheep() {

            const canvas = document.querySelector('canvas');
            canvas.style.display = 'block';
            const ctx = canvas.getContext('2d');
            // draw image sheep.jpg
            const img = new Image();
            img.src = 'sheep.jpg';
            img.onload = () => {
                ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
            };
            setTimeout(() => {
                canvas.style.display = 'none';
            }, 500);

        }

        // check if one message contains the word sheep
        const messages = messageList.querySelectorAll('p');
        messages.forEach(message => {
            if (message.innerText.toLowerCase().includes('sheep')) {
                displaySheep();
            }
        });

    </script>
</body>


</html>