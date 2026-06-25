import { DataTablePage, type DataColumn } from "../../components/data/DataTablePage";
import { getHostelRooms, type HostelRoomRecord } from "./adminApi";

const columns: DataColumn<HostelRoomRecord>[] = [
    { header: "Hostel", render: (room) => room.hostelName },
    { header: "Room", render: (room) => room.roomNumber },
    { header: "Floor", render: (room) => room.floorNumber ?? "-" },
    { header: "Type", render: (room) => room.roomType },
    { header: "Capacity", render: (room) => room.capacity },
    { header: "Occupied", render: (room) => room.occupiedCount },
    { header: "Available", render: (room) => room.availableBeds },
];

export function AdminRoomListPage() {
    return (
        <DataTablePage
            title="Room List"
            kicker="Admin"
            emptyMessage="No rooms found."
            loadData={getHostelRooms}
            columns={columns}
            getRowKey={(room) => room.roomId}
            getSearchText={(room) => [
                room.hostelName,
                room.roomNumber,
                room.floorNumber,
                room.roomType,
                room.capacity,
                room.occupiedCount,
                room.availableBeds,
            ].join(" ")}
            searchPlaceholder="Search rooms"
        />
    );
}
