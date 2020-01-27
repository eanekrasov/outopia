import {Socket} from "./Socket.js";

const template = document.createElement('template');
template.innerHTML = `
<style>
    #wrapper {
        box-shadow: 0 0 3px red;
    }
    #wrapper.discovered {
        box-shadow: 0 0 3px green;
    }
    #value {
        position: absolute;
        background: white;
        border: 1px solid green;
        display: none;
    }
    :hover #value {
        display: block;
    }
</style>
<span id="wrapper">
<span id="x"></span>x<span id="y"></span><br /><span id="value"></span><slot></slot>
</span>
`;

customElements.define('outopia-cell', class extends HTMLElement {
    $wrapper;
    $x;
    $y;
    $value;

    constructor() {
        super();
        let root = this.attachShadow({mode: 'open'});
        root.appendChild(template.content.cloneNode(true));
        this.$wrapper = root.querySelector("#wrapper");
        this.$x = root.querySelector("#x");
        this.$y = root.querySelector("#y");
        this.$value = root.querySelector("#value");
        this._onClick = this._onClick.bind(this);
        this._onDiscovered = this._onDiscovered.bind(this);
    }

    // noinspection JSUnusedGlobalSymbols
    connectedCallback() {
        this.addEventListener("click", this._onClick);
        Socket.socket(socket => {
            socket.eventListener('discovered').add(this._onDiscovered);
        });
    }

    // noinspection JSUnusedGlobalSymbols
    disconnectedCallback() {
        this.removeEventListener("click", this._onClick);
        Socket.socket(socket => {
            socket.eventListener('discovered').remove(this._onDiscovered);
        });
    }

    _onDiscovered(e) {
        if (String(e.x) === this.x && String(e.y) === this.y) {
            this.discovered = 'true';
            this.$value.innerHTML = e.value.map(it => {
                switch (it.type) {
                    case "city":
                        return `city ${it.level} ${it.owner.id}`;
                    case "field":
                        return `${it.resource} ${it.level} ${it.owner.id}`;
                    default:
                        return it;
                }
            }).join("<br />");
        }
    }

    async _onClick() {
        Socket.socket(socket => {
            socket.discover(this.x, this.y);
        });
    }

    // noinspection JSUnusedGlobalSymbols
    static get observedAttributes() {
        return ['x', 'y', 'discovered'];
    }

    // noinspection JSUnusedGlobalSymbols
    attributeChangedCallback(name, oldValue, newValue) {
        switch (name) {
            case 'x' :
                this.$x.innerText = newValue;
                break;
            case 'y' :
                this.$y.innerText = newValue;
                break;
            case 'discovered' :
                this.$wrapper.classList.toggle("discovered", this.discovered === "true");
                break;
        }
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

    get discovered() {
        return this.getAttribute("discovered");
    }

    set discovered(value) {
        this.setAttribute("discovered", value);
    }
});
