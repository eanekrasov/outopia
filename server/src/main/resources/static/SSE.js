export class SSE {
    /** @type EventSource */
    source = null;
    url = "";
    callback = () => null;

    constructor(url, callback) {
        this.url = url;
        this.callback = callback;
    }

    start() {
        const source = new EventSource(this.url);
        source.addEventListener("message", (e) => this.callback(JSON.parse(e.data)));
        source.onerror = () => this.stop();
        this.source = source;
    }

    stop() {
        this.source.close();
    }
}
