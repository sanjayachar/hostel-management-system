import { DataTablePage, type DataColumn } from "../../components/data/DataTablePage";
import { getHostels, type HostelRecord } from "./adminApi";

const columns: DataColumn<HostelRecord>[] = [
    { header: "Code", render: (hostel) => hostel.hostelCode },
    { header: "Name", render: (hostel) => hostel.hostelName },
    { header: "Type", render: (hostel) => hostel.hostelType },
    { header: "Address", render: (hostel) => hostel.address || "-" },
];

export function AdminHostelListPage() {
    return (
        <DataTablePage
            title="Hostel List"
            kicker="Admin"
            emptyMessage="No hostels found."
            loadData={getHostels}
            columns={columns}
            getRowKey={(hostel) => hostel.hostelId}
            getSearchText={(hostel) => [
                hostel.hostelCode,
                hostel.hostelName,
                hostel.hostelType,
                hostel.address,
            ].join(" ")}
            searchPlaceholder="Search hostels"
        />
    );
}
