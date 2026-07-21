import { DashboardCards, type DashboardCardItem } from "../../components/dashboard/DashboardCards";
import type { RequestAudience } from "../requests/requestApi";

type RoleDashboardProps = {
    audience: RequestAudience;
};

const roleLabels: Record<RequestAudience, string> = {
    student: "Student",
    staff: "Staff",
    candidate: "Candidate",
};

const requestPaths: Record<RequestAudience, string> = {
    student: "/student/requests",
    staff: "/staff/requests",
    candidate: "/candidate/requests",
};

const newRequestPaths: Record<RequestAudience, string> = {
    student: "/student/new-request",
    staff: "/staff/new-request",
    candidate: "/candidate/new-request",
};

const chatPaths: Record<RequestAudience, string> = {
    student: "/student/chat",
    staff: "/staff/chat",
    candidate: "/candidate/chat",
};

export function RoleDashboard({ audience }: RoleDashboardProps) {
    const label = roleLabels[audience];
    const cards: DashboardCardItem[] = [
        {
            title: "Request List",
            description: "View accommodation requests",
            to: requestPaths[audience],
            icon: "requests",
        },
        {
            title: "New Request",
            description: "Create accommodation request",
            to: newRequestPaths[audience],
            icon: "newRequest",
        },
        {
            title: "Chat",
            description: "Talk with hostel support",
            to: chatPaths[audience],
            icon: "chat",
        },
    ];

    return (
        <section className="dashboard-page">
            <div className="dashboard-header">
                <p className="dashboard-kicker">{label}</p>
                <h1 className="dashboard-title">Dashboard</h1>
            </div>
            <DashboardCards items={cards} />
        </section>
    );
}
