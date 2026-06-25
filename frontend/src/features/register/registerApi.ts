import { api } from "../../lib/api";

export type StudentRegistrationForm = {
    admissionNumber: string;
    firstName: string;
    lastName: string;
    gender: string;
    dateOfBirth: string;
    contactNumber: string;
    personalEmail: string;
    fatherName: string;
    motherName: string;
    address: string;
    hostelStatus: boolean;
};

export type CandidateRegistrationForm = {
    candidateCode: string;
    firstName: string;
    lastName: string;
    gender: string;
    dateOfBirth: string;
    email: string;
    contactNumber: string;
    address: string;
    city: string;
    state: string;
    pinCode: string;
    appliedPost: string;
};

export type RegistrationType = "student" | "candidate";

export type RegistrationForm = StudentRegistrationForm | CandidateRegistrationForm;

export async function registerStudent(data: StudentRegistrationForm) {
    const response = await api.post("/student-service-api/public/register/student", data);
    return response.data;
}

export async function registerCandidate(data: CandidateRegistrationForm) {
    const response = await api.post("/other-candidate-service-api/public/register/candidate", data);
    return response.data;
}

export async function getNextStudentAdmissionNumber() {
    const response = await api.get<string>("/student-service-api/public/register/student/next-admission-number");
    return response.data;
}

export async function getNextCandidateCode() {
    const response = await api.get<string>("/other-candidate-service-api/public/register/candidate/next-candidate-code");
    return response.data;
}
