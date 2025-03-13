const messageList = document.getElementById('messageList');
if (messageList) {

    messageList.scrollTop = messageList.scrollHeight;

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


    const imgInput = document.querySelector('input[type="file"]');
    imgInput.addEventListener('change', () => {
        const previewCard = document.getElementById('preview');
        preview.style.display = 'block';

        const reader = new FileReader();
        reader.onload = (e) => {
            const contentType = imgInput.files[0].type;
            console.log(contentType);

            if (!contentType.startsWith('image') && !contentType.startsWith('video') && !contentType.startsWith('audio')) {
                alert('Invalid file type');
                imgInput.value = '';
                return;
            }


            let result;

// Media element size
            const mediaWidth = '150px'; // Set fixed width for media
            const mediaHeight = '100px'; // Set fixed height for media

            if (contentType.startsWith('image')) {
                result = `<img src="${e.target.result}" class="img-fluid" alt="preview" style="width: ${mediaWidth}; height: ${mediaHeight}; object-fit: cover;">`;
            } else if (contentType.startsWith('video')) {
                result = `<video controls style="width: ${mediaWidth}; height: ${mediaHeight}; object-fit: cover;">                     
                <source src="${e.target.result}" type="${contentType}">                 
              </video>`;
            } else if (contentType.startsWith('audio')) {
                result = `<audio controls style="width: ${mediaWidth};">                     
                <source src="${e.target.result}" type="${contentType}">                 
              </audio>`;
            }

            const uuid = Math.floor(Math.random() * 1000000);
            previewCard.style.display = 'block';
            previewCard.innerHTML = `
                          <div class="card-body p-0">
                              <a class="position-absolute top-0 end-0 p-1" id="${uuid}" style="color: blue; cursor: pointer; z-index: 1000;">
                                  <i class="bi bi-x-octagon"></i>
                              </a>
                              ${result}
                          </div>`;
            const cancelBtn = document.getElementById(uuid);
            cancelBtn.addEventListener('click', () => {
                previewCard.style.display = 'none';
                previewCard.removeChild(cancelBtn.parentElement);
                const imgInput = document.querySelector('input[type="file"]');
                imgInput.value = '';
            });


            }


        reader.readAsDataURL(imgInput.files[0]);
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
            return; // Exit early if necessary elements are not present
        }

        const secondSent = parseInt(spanElement.innerHTML, 10); // Parse the timestamp safely

        // Calculate the difference
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

        // Update the paragraph element with the calculated time string
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
