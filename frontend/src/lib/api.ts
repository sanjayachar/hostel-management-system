import axios from "axios";

export const api = axios.create();

api.interceptors.request.use((config) => {
    const url = config.url ?? "";

    if (url.includes("/auth/login") || url.includes("/public/")) {
        return config;
    }

    const token = localStorage.getItem("token");

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});
