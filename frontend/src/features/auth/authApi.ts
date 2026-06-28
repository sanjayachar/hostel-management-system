import { api } from "../../lib/api";

export type LoginResponse = {
    token: string;
    passwordChangeRequired: boolean;
};

export type ChangePasswordPayload = {
    currentPassword: string;
    newPassword: string;
    confirmPassword: string;
};

export async function login(username: string, password: string) {
    const response = await api.post("/auth-service-api/auth/login", {
        username,
        password
    });

    return {
        token: response.data.token,
        passwordChangeRequired: Boolean(response.data.passwordChangeRequired),
    } satisfies LoginResponse;
}

export async function logout() {
    await api.post("/auth-service-api/auth/logout");
}

export async function changePassword(payload: ChangePasswordPayload) {
    await api.post("/auth-service-api/auth/change-password", payload);
}
