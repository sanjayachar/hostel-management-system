export type DashboardIconName =
    | "admin"
    | "student"
    | "staff"
    | "candidate"
    | "staffCreate"
    | "staffList"
    | "studentList"
    | "requests"
    | "candidates"
    | "logs"
    | "chat"
    | "hostel"
    | "rooms"
    | "newRequest";

type DashboardIconProps = {
    name: DashboardIconName;
    className?: string;
};

const iconPaths: Record<DashboardIconName, string[]> = {
    admin: [
        "M12 3l8 4v5c0 5-3.5 8-8 9-4.5-1-8-4-8-9V7l8-4Z",
        "M9 12l2 2 4-5",
    ],
    student: [
        "M3 8l9-4 9 4-9 4-9-4Z",
        "M7 10v5c0 2 2 4 5 4s5-2 5-4v-5",
        "M21 8v7",
    ],
    staff: [
        "M8 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z",
        "M2 21a6 6 0 0 1 12 0",
        "M18 8h4M18 12h4M18 16h4",
    ],
    candidate: [
        "M8 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z",
        "M16 11a3 3 0 1 0 0-6",
        "M2 21a6 6 0 0 1 12 0",
        "M14 18a5 5 0 0 1 8 3",
    ],
    staffCreate: [
        "M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z",
        "M4 21a8 8 0 0 1 16 0",
        "M19 8h4M21 6v4",
    ],
    staffList: [
        "M8 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z",
        "M2 21a6 6 0 0 1 12 0",
        "M18 8h4M18 12h4M18 16h4",
    ],
    studentList: [
        "M3 8l9-4 9 4-9 4-9-4Z",
        "M7 10v5c0 2 2 4 5 4s5-2 5-4v-5",
        "M21 8v7",
    ],
    requests: [
        "M7 3h8l4 4v14H7V3Z",
        "M15 3v5h4",
        "M10 12h6M10 16h6",
    ],
    candidates: [
        "M8 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z",
        "M16 11a3 3 0 1 0 0-6",
        "M2 21a6 6 0 0 1 12 0",
        "M14 18a5 5 0 0 1 8 3",
    ],
    logs: [
        "M5 4h14v16H5V4Z",
        "M9 8h6M9 12h6M9 16h3",
        "M17 15l2 2 3-4",
    ],
    chat: [
        "M4 5h16v10H8l-4 4V5Z",
        "M8 9h8",
        "M8 12h5",
    ],
    hostel: [
        "M4 21V9l8-6 8 6v12",
        "M8 21v-8h8v8",
        "M9 10h2M13 10h2M9 14h2M13 14h2",
    ],
    rooms: [
        "M4 21V5h16v16",
        "M8 9h3v4H8V9Z",
        "M13 9h3v4h-3V9Z",
        "M8 16h8",
    ],
    newRequest: [
        "M12 5v14M5 12h14",
        "M4 4h16v16H4V4Z",
    ],
};

export function DashboardIcon({ name, className }: DashboardIconProps) {
    return (
        <svg
            className={className}
            viewBox="0 0 24 24"
            aria-hidden="true"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.8"
            strokeLinecap="round"
            strokeLinejoin="round"
        >
            {iconPaths[name].map((path) => (
                <path key={path} d={path} />
            ))}
        </svg>
    );
}
