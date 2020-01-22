export function args(params) {
    let args = Object.keys(params).map(k => `${k}=${params[k]}`).join('&');
    return args !== "" ? `?${args}` : "";
}

export async function call(url, options) {
    let response = await fetch(`/api/v1/${url}`, options);
    return await response.json()
}

export async function get(url, params) {
    return await call(url + args(params));
}

export async function post(url, body) {
    return await call(url, {
        method: "POST",
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(body),
    });
}