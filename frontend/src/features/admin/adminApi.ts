import { api } from "../../lib/api";
import type { AccommodationRequest } from "../requests/requestApi";

export type StaffRecord = {
    staffId: number;
    employeeCode: string;
    firstName: string;
    lastName: string;
    gender: string;
    contactNumber: string;
    email: string;
    designation: string;
    department: string;
};

export type StaffCreateForm = {
    employeeCode: string;
    firstName: string;
    lastName: string;
    gender: string;
    dateOfBirth: string;
    contactNumber: string;
    email: string;
    address: string;
    designation: string;
    department: string;
    dateOfJoining: string;
};

export type StudentRecord = {
    studentId: number;
    admissionNumber: string;
    firstName: string;
    lastName: string;
    gender: string;
    contactNumber: string;
    personalEmail: string;
    hostelStatus?: boolean;
};

export type CandidateRecord = {
    candidateId: number;
    candidateCode: string;
    firstName: string;
    lastName: string;
    gender: string;
    email: string;
    contactNumber: string;
    appliedPost: string;
};

export type AdminAccommodationRequest = AccommodationRequest & {
    audience: "Student" | "Staff" | "Candidate";
};

export type HostelRecord = {
    hostelId: number;
    hostelCode: string;
    hostelName: string;
    hostelType: string;
    address?: string | null;
};

export type HostelCreateForm = {
    hostelCode: string;
    hostelName: string;
    hostelType: string;
    address: string;
};

export type HostelRoomRecord = {
    roomId: number;
    hostelId: number;
    hostelName: string;
    roomNumber: string;
    floorNumber?: number | null;
    roomType: string;
    capacity: number;
    occupiedCount: number;
    availableBeds: number;
};

export type HostelRoomCreateForm = {
    hostelId: number;
    roomNumber: string;
    floorNumber?: number | null;
    roomType: string;
    capacity: number;
};

export type RequestDecisionForm = {
    status: "Approved" | "Rejected";
    roomId?: number;
    bedNumber?: string;
    decisionNote?: string;
};

export async function getStaffList() {
    const response = await api.get<StaffRecord[]>("/staff-service-api/admin/staffs/list");
    return response.data;
}

export async function getNextEmployeeCode() {
    const response = await api.get<string>("/staff-service-api/admin/staffs/next-employee-code");
    return response.data;
}

export async function createStaff(data: StaffCreateForm) {
    const response = await api.post("/staff-service-api/admin/staffs/register", data);
    return response.data;
}

export async function getStudentList() {
    const response = await api.get<StudentRecord[]>("/student-service-api/admin/students/list");
    return response.data;
}

export async function getCandidateList() {
    const response = await api.get<CandidateRecord[]>("/other-candidate-service-api/admin/candidates/list");
    return response.data;
}

export async function getAdminAccommodationRequests() {
    const [students, staffs, candidates] = await Promise.all([
        api.get<AccommodationRequest[]>("/accomm-service-api/admin/request/details/students"),
        api.get<AccommodationRequest[]>("/accomm-service-api/admin/request/details/staffs"),
        api.get<AccommodationRequest[]>("/accomm-service-api/admin/request/details/candidates"),
    ]);

    return [
        ...students.data.map((request) => ({ ...request, audience: "Student" as const })),
        ...staffs.data.map((request) => ({ ...request, audience: "Staff" as const })),
        ...candidates.data.map((request) => ({ ...request, audience: "Candidate" as const })),
    ];
}

export async function getHostels() {
    const response = await api.get<HostelRecord[]>("/accomm-service-api/admin/hostels");
    return response.data;
}

export async function createHostel(data: HostelCreateForm) {
    const response = await api.post<HostelRecord>("/accomm-service-api/admin/hostels", data);
    return response.data;
}

export async function getHostelRooms(hostelId?: number) {
    const params = hostelId ? `?hostelId=${hostelId}` : "";
    const response = await api.get<HostelRoomRecord[]>(`/accomm-service-api/admin/hostel-rooms${params}`);
    return response.data;
}

export async function createHostelRoom(data: HostelRoomCreateForm) {
    const response = await api.post<HostelRoomRecord>("/accomm-service-api/admin/hostel-rooms", data);
    return response.data;
}

export async function decideAccommodationRequest(requestId: number, data: RequestDecisionForm) {
    const response = await api.post<AccommodationRequest>(
        `/accomm-service-api/admin/request/details/${requestId}/decision`,
        { requestId, ...data },
    );
    return response.data;
}
