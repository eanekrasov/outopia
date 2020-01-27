import {Socket} from "./Socket.js";

(async function () {
    Socket.socket(socket => {
        socket.eventListener('resources').add((e) => {
            Object.keys(e.value).forEach(k => {
                document.querySelector(`#resource-${k}`).innerHTML = e.value[k];
            });
        });
    });
})();