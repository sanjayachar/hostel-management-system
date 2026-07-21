import { api } from "../../lib/api";

export type RequestAudience = "student" | "staff" | "candidate";

export type AccommodationRequest = {
    requestId: number;
    requestType: string;
    reason: string;
    fromDate: string;
    toDate: string;
    noOfDays: number;
    noOfPersons: number;
    status?: string;
    decisionNote?: string;
    userId?: number;
    userRole?: string;
    requesterCode?: string;
    requesterName?: string;
    allocationId?: number;
    hostelId?: number;
    hostelName?: string;
    roomId?: number;
    roomNumber?: string;
    bedNumber?: string;
    allocationStatus?: string;
};

export type AccommodationRequestForm = {
    requestType: string;
    reason: string;
    fromDate: string;
    toDate: string;
    noOfDays: number;
    noOfPersons: number;
};

const requestListEndpoints: Record<RequestAudience, string> = {
    student: "/student-service-api/student/request/list",
    staff: "/staff-service-api/staff/request/list",
    candidate: "/other-candidate-service-api/candidate/request/list",
};

export async function getRequestList(audience: RequestAudience) {
    const response = await api.get<AccommodationRequest[]>(requestListEndpoints[audience]);
    return response.data;
}

const requestCreateEndpoints: Record<RequestAudience, string> = {
    student: "/student-service-api/student/saveRequest",
    staff: "/staff-service-api/staff/request",
    candidate: "/other-candidate-service-api/candidate/saveRequest",
};

export async function createAccommodationRequest(audience: RequestAudience, data: AccommodationRequestForm) {
    const response = await api.post(requestCreateEndpoints[audience], data);
    return response.data;
}
