const messageList = document.getElementById('messageList');
if (messageList) {



    const reactSpan = document.querySelectorAll('.reactSpan');
    reactSpan.forEach(span => {
        span.addEventListener('mouseover', () => {
            document.getElementById('hover-div').style.display = 'block';
            document.getElementById('hover-div').style.position = 'absolute';

            const rect = span.getBoundingClientRect();
            document.getElementById('hover-div').style.top = rect.bottom + 'px';
            document.getElementById('hover-div').style.left = rect.left + 'px';

            const hoverDiv = document.getElementById('hover-div');
            hoverDiv.innerHTML = span.querySelector('.reactDetails').innerHTML;

        });

        span.addEventListener('mouseleave', () => {
            document.getElementById('hover-div').style.display = 'none';
        });

    });

    const textInputDiv = document.getElementById('textInputDiv');
    const imgInput = document.querySelector('input[type="file"]');
    imgInput.addEventListener('change', () => {
        const previewCard = document.getElementById('preview');
        const textInputDiv = document.getElementById('textInputDiv');
        previewCard.innerHTML = '';
        previewCard.style.display = 'flex';

        const files = imgInput.files;

        if (files.length === 0) {
            alert('No files selected');
            return;
        }

        Array.from(files).forEach((file) => {
            const reader = new FileReader();

            reader.onload = (e) => {
                const contentType = file.type;
                if (!contentType.startsWith('image') && !contentType.startsWith('video') && !contentType.startsWith('audio')) {
                    alert('Invalid file type: ' + file.name);
                    imgInput.value = '';
                    previewCard.style.display = 'none';
                    textInputDiv.style.display = 'block';
                    return;
                }

                let result;
                const mediaWidth = '150px';
                const mediaHeight = '100px';

                if (contentType.startsWith('image')) {
                    result = `<img src="${e.target.result}" class="img-fluid preview-item" style="width: ${mediaWidth}; height: ${mediaHeight}; object-fit: cover;">`;
                } else if (contentType.startsWith('video')) {
                    result = `<video controls class="preview-item" style="width: ${mediaWidth}; height: ${mediaHeight};">
                            <source src="${e.target.result}" type="${contentType}">
                          </video>`;
                } else if (contentType.startsWith('audio')) {
                    result = `<audio controls class="preview-item" style="width: ${mediaWidth};">
                            <source src="${e.target.result}" type="${contentType}">
                          </audio>`;
                }
                const ra = Math.floor(Math.random() * 1000000);
                // convert to base 64
                const id = btoa(ra.toString());

                const cardHTML = `
                <div class="col-6 col-sm-4 col-md-3 p-1 position-relative">
                    <div class="card" style="width: 100%;">
                        <div class="card-body p-0">
                            
                            ${result}
                        </div>
                    </div>
                </div>`;

                previewCard.innerHTML += cardHTML;

            };
            previewCard.innerHTML += `<a class="position-absolute" style="top: 0; right: 0; z-index: 1000;"
                                style="color: blue; cursor: pointer;" onclick="{
                                const previewCard = document.getElementById('preview');
                                const textInputDiv = document.getElementById('textInputDiv');
                                previewCard.style.display = 'none';
                                textInputDiv.style.display = 'block';
                                previewCard.innerHTML = '';    
                                const imgInput = document.querySelector('input[type=file]');
                                imgInput.value = '';
                            }" >
                                <i class="bi bi-x-octagon"></i>
                            </a>
            `

            reader.readAsDataURL(file);
        });
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

    const messages = messageList.querySelectorAll('.messageText');

    // check if one message contains the word sheep
    messages.forEach(message => {
        if (message.innerText.toLowerCase().includes('sheep')) {
            displaySheep();
        }
    });
    const {Marked} = globalThis.marked;
    const {markedHighlight} = globalThis.markedHighlight;
    const marked = new Marked(
        markedHighlight({
            emptyLangClass: 'hljs',
            langPrefix: 'hljs language-',
            highlight(code, lang, info) {
                const language = hljs.getLanguage(lang) ? lang : 'plaintext';
                return hljs.highlight(code, {language}).value;
            }
        })
    );

    messages.forEach(message => {
        const text = message.innerHTML;
        const textarea = document.createElement('textarea');
        textarea.innerHTML = text;
        const clean = DOMPurify.sanitize(textarea.value);
        message.outerHTML = marked.parse(clean);
    });
    messageList.scrollTop = messageList.scrollHeight;
}

const profileLink = document.getElementById('profileLink');
profileLink.addEventListener('mouseenter', () => {
    profileLink.querySelector('i').style.display = 'block';
});
profileLink.addEventListener('mouseleave', () => {
    profileLink.querySelector('i').style.display = 'none';
});

const otherReacts = document.querySelectorAll('.otherReact');
otherReacts.forEach(otherReact => {
    const aLink = otherReact.querySelector('a');
    const otherReactFormsDivs = otherReact.querySelectorAll('.otherReactForm > div');
    aLink.addEventListener('click', () => {
        otherReactFormsDivs.forEach(div => {
            div.style.display = 'flex';
        });
    });

    otherReact.addEventListener('mouseleave', () => {
        otherReactFormsDivs.forEach(div => {
            div.style.display = 'none';
        });
    });
});

const timeAgos = document.querySelectorAll('.timeAgo');

setInterval(() => {
    const now = Math.floor(Date.now() / 1000); // Get current time in seconds

    timeAgos.forEach(timeAgo => {
        const spanElement = timeAgo.querySelector("span");
        const pElement = timeAgo.querySelector("p");

        if (!spanElement || !pElement) {
            return;
        }

        const secondSent = parseInt(spanElement.innerHTML, 10); // Parse the timestamp safely

        const diffSeconds = now - secondSent;
        let timeString = "";

        if (diffSeconds < 60) {
            timeString = diffSeconds + " second" + (diffSeconds !== 1 ? "s" : "") + " ago";
        } else if (diffSeconds < 3600) { // Less than an hour
            const diffMinutes = Math.floor(diffSeconds / 60);
            timeString = diffMinutes + " minute" + (diffMinutes !== 1 ? "s" : "") + " ago";
        } else if (diffSeconds < 86400) { // Less than a day
            const diffHours = Math.floor(diffSeconds / 3600);
            timeString = diffHours + " hour" + (diffHours !== 1 ? "s" : "") + " ago";
        } else { // More than a day
            const diffDays = Math.floor(diffSeconds / 86400);
            timeString = diffDays + " day" + (diffDays !== 1 ? "s" : "") + " ago";
        }

        pElement.innerHTML = timeString;
    });
}, 1000);


const channelTab = document.querySelector('#channelTab');
const channels = document.querySelector('#channels');
const friendTab = document.querySelector('#friendTab');
const friendsChannel = document.querySelector('#friendsChannel');

if (channelTab.classList.contains('active')) {
    channels.style.display = 'block';
    friendsChannel.style.display = 'none';
}

if (friendTab.classList.contains('active')) {
    channels.style.display = 'none';
    friendsChannel.style.display = 'block';
}

channelTab.addEventListener('click', (e) => {
    e.preventDefault();
    channels.style.display = 'block';
    friendsChannel.style.display = 'none';
    channelTab.classList.add('active');
    friendTab.classList.remove('active');

});

friendTab.addEventListener('click', (e) => {
    e.preventDefault()
    channels.style.display = 'none';
    friendsChannel.style.display = 'block';
    channelTab.classList.remove('active');
    friendTab.classList.add('active');
});
