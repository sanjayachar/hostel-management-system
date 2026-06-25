import { DataTablePage, type DataColumn } from "../../components/data/DataTablePage";
import { getStudentList, type StudentRecord } from "./adminApi";

const columns: DataColumn<StudentRecord>[] = [
    { header: "Admission No", render: (student) => student.admissionNumber },
    { header: "Name", render: (student) => `${student.firstName} ${student.lastName}` },
    { header: "Gender", render: (student) => student.gender },
    { header: "Email", render: (student) => student.personalEmail },
    { header: "Contact", render: (student) => student.contactNumber },
    { header: "Hostel", render: (student) => (student.hostelStatus ? "Yes" : "No") },
];

export function AdminStudentListPage() {
    return (
        <DataTablePage
            title="Student List"
            kicker="Admin"
            emptyMessage="No student records found."
            loadData={getStudentList}
            columns={columns}
            getRowKey={(student) => student.studentId}
            getSearchText={(student) => [
                student.admissionNumber,
                student.firstName,
                student.lastName,
                student.gender,
                student.personalEmail,
                student.contactNumber,
                student.hostelStatus ? "yes hostel" : "no hostel",
            ].join(" ")}
            searchPlaceholder="Search students"
        />
    );
}
