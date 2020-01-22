import {socket} from "./Socket.js";

(async function () {
    socket.eventListener('getValue').add((e) => console.log("got getValue", e));
    socket.eventListener('reveal').add((e) => console.log("got reveal", e));
})();