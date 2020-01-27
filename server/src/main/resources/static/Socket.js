import {args} from "./utils.js";

let globalSocket = null;
let globalCallbacks = [];

export class Socket {
    eventListeners = new Map();
    connectListeners = new Set();
    disconnectListeners = new Set();
    errorListeners = new Set();
    socket;

    constructor(url, params = {}) {
        this.socket = this._connect(url, params);
    }

    _connect(url, params = {}) {
        const socket = new WebSocket(`${location.protocol.replace("http", "ws")}//${location.host}${url}${args(params)}`);
        socket['onopen'] = (e) => {
            this.connectListeners.forEach(it => it(e));
        };
        socket['onclose'] = (e) => {
            this.disconnectListeners.forEach(it => it(e));
            setTimeout(() => this._connect(url, params), 5000);
        };
        socket['onerror'] = (err) => {
            this.errorListeners.forEach(it => it(err));
        };
        socket['onmessage'] = (e) => {
            console.log("in", e);
            let json = JSON.parse(e.data);
            this.eventListener(json.type).forEach(it => it(json));
        };
    }

    send(json) {
        this.socket.send(JSON.stringify(json));
    }

    eventListener(key) {
        if (!this.eventListeners.has(key)) {
            this.eventListeners.set(key, new Set());
        }
        return this.eventListeners.get(key);
    }

    discover(x, y) {
        this.send({type: 'discover', x, y});
    }

    static start(url, params) {
        globalSocket = new Socket(url, params);
        globalCallbacks.forEach(it => it(globalSocket));
    }

    static socket(callback) {
        if (globalSocket != null) {
            callback(globalSocket);
        } else {
            globalCallbacks.push(callback);
        }
    }
}
