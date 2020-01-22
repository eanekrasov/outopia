import {socket} from "./Socket.js";

const template = document.createElement('template');
template.innerHTML = `
<style>
    #value {
        box-shadow: 0 0 3px red;
    }
    #value.revealed {
        box-shadow: 0 0 3px green;
    }
</style>
<span id="x"></span>x<span id="y"></span><br /><span id="value"></span><slot></slot>`;

customElements.define('outopia-cell', class extends HTMLElement {
    constructor() {
        super();
        let root = this.attachShadow({mode: 'open'});
        root.appendChild(template.content.cloneNode(true));
        this.$$ = {
            x: root.querySelector("#x"),
            y: root.querySelector("#y"),
            value: root.querySelector("#value")
        };
        this._onClick = this._onClick.bind(this);
        this._onReveal = this._onReveal.bind(this);
        this._invalidate();
    }

    connectedCallback() {
        this.addEventListener("click", this._onClick);
        socket.eventListener('reveal').add(this._onReveal);
    }

    disconnectedCallback() {
        this.removeEventListener("click", this._onClick);
        socket.eventListener('reveal').remove(this._onReveal);
    }

    _invalidate() {
        this.$$.value.classList.toggle("revealed", this.value === "true");
    }

    _onReveal(e) {
        if (String(e.x) === this.x && String(e.y) === this.y) {
            this.value = String(e.value);
            this._invalidate();
        }
    }

    async _onClick() {
        socket.reveal(this.x, this.y);
    }

    static get observedAttributes() {
        return ['x', 'y', 'value'];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        this.$$[name].innerText = newValue;
    }

    get x() {
        return this.getAttribute("x");
    }

    set x(value) {
        this.setAttribute("x", value);
    }

    get y() {
        return this.getAttribute("y");
    }

    set y(value) {
        this.setAttribute("y", value);
    }

    get value() {
        return this.getAttribute("value");
    }

    set value(value) {
        this.setAttribute("value", value);
    }
});
