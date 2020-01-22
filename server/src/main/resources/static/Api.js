import {get} from "./utils.js";

export async function getValue(id, x, y) {
    return await get(`outopia/getValue`, {id, x, y});
}

export async function reveal(id, x, y) {
    return await get(`outopia/reveal`, {id, x, y});
}