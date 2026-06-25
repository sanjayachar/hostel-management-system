import { api } from "../../lib/api";

export async function login(username: string, password: string) {
    const response = await api.post("/auth-service-api/auth/login", {
        username,
        password
    });
    return response.data.token;
}

export async function logout() {
    await api.post("/auth-service-api/auth/logout");
}