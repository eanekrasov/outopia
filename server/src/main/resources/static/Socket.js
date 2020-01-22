import {args} from "./utils.js";

export class Socket {
    eventListeners = new Map();
    connectListeners = new Set();
    disconnectListeners = new Set();
    errorListeners = new Set();

    constructor(url, params = {}) {
        this._connect(url, params);
    }

    _connect(url, params = {}) {
        this.socket = new WebSocket(`${location.protocol.replace("http", "ws")}//${location.host}${url}${args(params)}`);
        this.socket['onopen'] = (e) => {
            this.connectListeners.forEach(it => it(e));
        };
        this.socket['onclose'] = (e) => {
            this.disconnectListeners.forEach(it => it(e));
            setTimeout(() => this._connect(url, params), 5000);
        };
        this.socket['onerror'] = (err) => {
            this.errorListeners.forEach(it => it(err));
        };
        this.socket['onmessage'] = (e) => {
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

    getValue(x, y) {
        this.send({type: 'getValue', x, y});
    }

    reveal(x, y) {
        this.send({type: 'reveal', x, y});
    }
}

export const socket = new Socket("/ws/client", {id: "coder"});
