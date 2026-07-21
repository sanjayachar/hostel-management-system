import { Navigate } from "react-router-dom";
import { DashboardCards, type DashboardCardItem } from "../../components/dashboard/DashboardCards";
import { getCurrentUser, getDashboardPath } from "../../lib/auth";
import "../../assets/css/PublicPages.css";

const loginCards: DashboardCardItem[] = [
    {
        title: "Admin Login",
        description: "Manage staff, students, requests, and logs",
        to: "/login/admin",
        icon: "admin",
    },
    {
        title: "Staff Login",
        description: "Access staff requests and hostel workflows",
        to: "/login/staff",
        icon: "staff",
    },
    {
        title: "Student Login",
        description: "View and create accommodation requests",
        to: "/login/student",
        icon: "student",
    },
    {
        title: "Candidate Login",
        description: "Access candidate accommodation requests",
        to: "/login/candidate",
        icon: "candidate",
    },
];

export function LandingPage() {
    const user = getCurrentUser();

    if (user) {
        return <Navigate to={getDashboardPath(user.role)} replace />;
    }

    return (
        <main className="public-page">
            <section className="landing-hero">
                <p className="landing-kicker">Hostel Management System</p>
                <h1 className="landing-title">Choose your portal</h1>
                <p className="landing-copy">
                    Sign in with your assigned role to continue.
                </p>
            </section>

            <section className="landing-card-section" aria-label="Login portals">
                <DashboardCards items={loginCards} />
            </section>
        </main>
    );
}
