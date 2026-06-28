import { type FormEvent, useState } from "react";
import { Link, Navigate, useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import { HostelLogo } from "../../components/brand/HostelLogo";
import { login } from "./authApi";
import {
    clearAuthState,
    getCurrentUser,
    getDashboardPath,
    isPasswordChangeRequired,
    setPasswordChangeRequired,
    type UserRole
} from "../../lib/auth";
import "../../assets/css/LoginPage.css";
import "../../assets/css/PublicPages.css";

type LoginAudience = "admin" | "staff" | "student" | "candidate";

const audienceConfig: Record<LoginAudience, { title: string; role: UserRole; registerPath?: string }> = {
    admin: {
        title: "Admin Login",
        role: "ROLE_ADMIN",
    },
    staff: {
        title: "Staff Login",
        role: "ROLE_STAFF",
    },
    student: {
        title: "Student Login",
        role: "ROLE_STUDENT",
        registerPath: "/register/student",
    },
    candidate: {
        title: "Candidate Login",
        role: "ROLE_CANDIDATE",
        registerPath: "/register/candidate",
    },
};

export function LoginPage() {
    const { audience } = useParams<{ audience: LoginAudience }>();
    const navigate = useNavigate();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const currentUser = getCurrentUser();
    const loginConfig = audience ? audienceConfig[audience] : null;

    if (currentUser) {
        if (isPasswordChangeRequired()) {
            return <Navigate to="/change-password" replace />;
        }

        return <Navigate to={getDashboardPath(currentUser.role)} replace />;
    }

    if (!loginConfig) {
        return <Navigate to="/" replace />;
    }

    const activeLoginConfig = loginConfig;

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();
        setError("");
        setIsSubmitting(true);

        try {
            const loginResponse = await login(username.trim(), password);
            const token = loginResponse.token;

            if (!token) {
                setError("Login failed: auth service did not return a token.");
                return;
            }

            localStorage.setItem("token", token);
            setPasswordChangeRequired(loginResponse.passwordChangeRequired);

            const user = getCurrentUser();

            if (!user) {
                clearAuthState();
                setError("Login failed: received token is invalid or expired.");
                return;
            }

            if (user.role !== activeLoginConfig.role) {
                clearAuthState();
                setError(`This portal is only for ${activeLoginConfig.title.replace(" Login", "")} accounts.`);
                return;
            }

            if (loginResponse.passwordChangeRequired) {
                navigate("/change-password", { replace: true });
                return;
            }

            navigate(getDashboardPath(user.role), { replace: true });
        } catch (err) {
            if (axios.isAxiosError(err)) {
                if (err.response?.status === 401 || err.response?.status === 403) {
                    setError("Invalid username or password.");
                    return;
                }

                if (!err.response) {
                    setError("Cannot reach auth service. Check that auth-service is running on port 8081.");
                    return;
                }

                setError(`Login failed: ${err.response.status}`);
                return;
            }

            setError("Login failed. Please try again.");
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <main className="login-page">
            <section className="login-panel" aria-labelledby="login-title">
                <div className="login-header">
                    <div className="login-mark">
                        <HostelLogo className="login-logo-icon" />
                    </div>
                    <div>
                        <p className="login-eyebrow">Hostel Management</p>
                        <h1 id="login-title" className="login-title">{activeLoginConfig.title}</h1>
                    </div>
                </div>

                <form className="login-form" onSubmit={handleSubmit} noValidate>
                    <div className="login-field">
                        <label className="login-label" htmlFor="username">Username</label>
                        <input
                            id="username"
                            className="login-input"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            placeholder="Enter username"
                            autoComplete="username"
                            required
                        />
                    </div>

                    <div className="login-field">
                        <label className="login-label" htmlFor="password">Password</label>
                        <input
                            id="password"
                            className="login-input"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Enter password"
                            type="password"
                            autoComplete="current-password"
                            required
                        />
                    </div>

                    {error && (
                        <p className="login-error" role="alert">
                            {error}
                        </p>
                    )}

                    <button className="login-button" type="submit" disabled={isSubmitting}>
                        {isSubmitting ? "Logging in..." : "Login"}
                    </button>

                    <div className="login-secondary-actions">
                        <Link className="public-link" to="/">Landing Page</Link>
                        {activeLoginConfig.registerPath && (
                            <Link className="public-link" to={activeLoginConfig.registerPath}>Register</Link>
                        )}
                    </div>
                </form>
            </section>
        </main>
    );
}
