import {get} from "./utils.js";

// noinspection JSUnusedGlobalSymbols
export async function value(x, y) {
    return await get(`outopia/value`, {x, y});
}