import { jwtDecode } from "jwt-decode";

export type UserRole =
    | "ROLE_ADMIN"
    | "ROLE_STUDENT"
    | "ROLE_STAFF"
    | "ROLE_CANDIDATE";

export type JwtPayload = {
    sub: string;
    role: UserRole;
    userId: number;
    exp: number;
};

const PASSWORD_CHANGE_REQUIRED_KEY = "passwordChangeRequired";

export function getCurrentUser() {
    const token = localStorage.getItem("token");

    if (!token) return null;

    try {
        const user = jwtDecode<JwtPayload>(token);

        if (user.exp * 1000 < Date.now()) {
            localStorage.removeItem("token");
            return null;
        }

        return user;
    } catch {
        localStorage.removeItem("token");
        return null;
    }
}

export function setPasswordChangeRequired(required: boolean) {
    if (required) {
        localStorage.setItem(PASSWORD_CHANGE_REQUIRED_KEY, "true");
        return;
    }

    localStorage.removeItem(PASSWORD_CHANGE_REQUIRED_KEY);
}

export function isPasswordChangeRequired() {
    return localStorage.getItem(PASSWORD_CHANGE_REQUIRED_KEY) === "true";
}

export function clearAuthState() {
    localStorage.removeItem("token");
    setPasswordChangeRequired(false);
}

export function getDashboardPath(role: UserRole) {
    if (role === "ROLE_ADMIN") return "/admin";
    if (role === "ROLE_STUDENT") return "/student";
    if (role === "ROLE_STAFF") return "/staff";
    if (role === "ROLE_CANDIDATE") return "/candidate";

    return "/";
}
