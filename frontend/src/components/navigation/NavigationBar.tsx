import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { HostelLogo } from "../brand/HostelLogo";
import { logout } from "../../features/auth/authApi";
import { getCurrentUser, getDashboardPath, type UserRole } from "../../lib/auth";

function formatRole(role?: string) {
    return role ? role.replace("ROLE_", "").toLowerCase() : "user";
}

function getRequestPath(role: UserRole) {
    if (role === "ROLE_ADMIN") return "/admin/accommodation-requests";
    if (role === "ROLE_STUDENT") return "/student/requests";
    if (role === "ROLE_STAFF") return "/staff/requests";
    if (role === "ROLE_CANDIDATE") return "/candidate/requests";

    return "/";
}

export function NavigationBar() {
    const navigate = useNavigate();
    const user = getCurrentUser();
    const [isLoggingOut, setIsLoggingOut] = useState(false);

    async function handleLogout() {
        setIsLoggingOut(true);

        try {
            await logout();
        } catch {
            // Local logout should still happen if the token is expired or the auth service is unavailable.
        } finally {
            localStorage.removeItem("token");
            setIsLoggingOut(false);
            navigate("/login", { replace: true });
        }
    }

    return (
        <header className="app-nav">
            <Link className="app-nav-brand" to={user ? getDashboardPath(user.role) : "/login"}>
                <span className="app-nav-mark">
                    <HostelLogo className="app-logo-icon" />
                </span>
                <span className="app-nav-name">Hostel Management</span>
            </Link>

            <nav className="app-nav-links" aria-label="Primary navigation">
                {user && (
                    <>
                        <Link className="app-nav-link" to={getDashboardPath(user.role)}>
                            Dashboard
                        </Link>
                        <Link className="app-nav-link" to={getRequestPath(user.role)}>
                            Requests
                        </Link>
                    </>
                )}
            </nav>

            <div className="app-nav-account">
                <span className="app-nav-user">{user?.sub}</span>
                <span className="app-nav-role">{formatRole(user?.role)}</span>
                <button className="app-nav-logout" type="button" onClick={handleLogout} disabled={isLoggingOut}>
                    {isLoggingOut ? "Logging out..." : "Logout"}
                </button>
            </div>
        </header>
    );
}
