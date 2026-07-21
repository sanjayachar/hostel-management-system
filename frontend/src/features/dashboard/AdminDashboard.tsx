import { DashboardCards, type DashboardCardItem } from "../../components/dashboard/DashboardCards";

const adminCards: DashboardCardItem[] = [
    {
        title: "Staff Create",
        description: "Register a new staff member",
        to: "/admin/staff/create",
        icon: "staffCreate",
    },
    {
        title: "Staff List",
        description: "View registered staff",
        to: "/admin/staff/list",
        icon: "staffList",
    },
    {
        title: "Student List",
        description: "View registered students",
        to: "/admin/student/list",
        icon: "studentList",
    },
    {
        title: "Accommodation Requests",
        description: "Review request list",
        to: "/admin/accommodation-requests",
        icon: "requests",
    },
    {
        title: "Hostel Setup",
        description: "Create hostels and rooms",
        to: "/admin/hostels",
        icon: "hostel",
    },
    {
        title: "Hostel List",
        description: "View hostel records",
        to: "/admin/hostels/list",
        icon: "hostel",
    },
    {
        title: "Room List",
        description: "View room availability",
        to: "/admin/rooms/list",
        icon: "rooms",
    },
    {
        title: "Other Candidate List",
        description: "View candidate records",
        to: "/admin/candidates",
        icon: "candidates",
    },
    {
        title: "Day To Day Logs",
        description: "Track daily activity",
        to: "/admin/logs",
        icon: "logs",
    },
    {
        title: "Chat",
        description: "Talk with hostel users",
        to: "/admin/chat",
        icon: "chat",
    },
];

export function AdminDashboard() {
    return (
        <section className="dashboard-page">
            <div className="dashboard-header">
                <p className="dashboard-kicker">Admin</p>
                <h1 className="dashboard-title">Dashboard</h1>
            </div>
            <DashboardCards items={adminCards} />
        </section>
    );
}
