import { DataTablePage, type DataColumn } from "../../components/data/DataTablePage";
import { getStaffList, type StaffRecord } from "./adminApi";

const columns: DataColumn<StaffRecord>[] = [
    { header: "Employee Code", render: (staff) => staff.employeeCode },
    { header: "Name", render: (staff) => `${staff.firstName} ${staff.lastName}` },
    { header: "Designation", render: (staff) => staff.designation },
    { header: "Department", render: (staff) => staff.department },
    { header: "Email", render: (staff) => staff.email },
    { header: "Contact", render: (staff) => staff.contactNumber },
];

export function AdminStaffListPage() {
    return (
        <DataTablePage
            title="Staff List"
            kicker="Admin"
            emptyMessage="No staff records found."
            loadData={getStaffList}
            columns={columns}
            getRowKey={(staff) => staff.staffId}
            getSearchText={(staff) => [
                staff.employeeCode,
                staff.firstName,
                staff.lastName,
                staff.designation,
                staff.department,
                staff.email,
                staff.contactNumber,
            ].join(" ")}
            searchPlaceholder="Search staff"
        />
    );
}
