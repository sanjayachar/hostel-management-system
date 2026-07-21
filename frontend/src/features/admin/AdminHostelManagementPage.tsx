import { type FormEvent, useEffect, useMemo, useState } from "react";
import axios from "axios";
import {
    createHostel,
    createHostelRoom,
    getHostelRooms,
    getHostels,
    type HostelCreateForm,
    type HostelRecord,
    type HostelRoomCreateForm,
    type HostelRoomRecord,
} from "./adminApi";

const emptyHostelForm: HostelCreateForm = {
    hostelCode: "",
    hostelName: "",
    hostelType: "",
    address: "",
};

const emptyRoomForm: HostelRoomCreateForm = {
    hostelId: 0,
    roomNumber: "",
    floorNumber: null,
    roomType: "",
    capacity: 1,
};

function getErrorMessage(error: unknown, fallback: string) {
    if (!axios.isAxiosError(error)) return fallback;

    const responseData = error.response?.data;

    if (responseData && typeof responseData === "object" && "message" in responseData) {
        return String(responseData.message);
    }

    if (!error.response) return "Cannot reach accommodation service.";

    return `${fallback}: ${error.response.status}`;
}

export function AdminHostelManagementPage() {
    const [hostels, setHostels] = useState<HostelRecord[]>([]);
    const [rooms, setRooms] = useState<HostelRoomRecord[]>([]);
    const [hostelForm, setHostelForm] = useState<HostelCreateForm>(emptyHostelForm);
    const [roomForm, setRoomForm] = useState<HostelRoomCreateForm>(emptyRoomForm);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [isSavingHostel, setIsSavingHostel] = useState(false);
    const [isSavingRoom, setIsSavingRoom] = useState(false);

    const hostelOptions = useMemo(() => hostels.filter((hostel) => hostel.hostelId), [hostels]);

    async function loadData() {
        setIsLoading(true);
        setError("");

        try {
            const [hostelData, roomData] = await Promise.all([getHostels(), getHostelRooms()]);
            setHostels(hostelData);
            setRooms(roomData);
            setRoomForm((current) => ({
                ...current,
                hostelId: current.hostelId || hostelData[0]?.hostelId || 0,
            }));
        } catch (err) {
            setError(getErrorMessage(err, "Hostel setup failed"));
        } finally {
            setIsLoading(false);
        }
    }

    useEffect(() => {
        void loadData();
    }, []);

    async function handleHostelSubmit(event: FormEvent) {
        event.preventDefault();
        setError("");
        setSuccess("");

        if (!hostelForm.hostelCode.trim() || !hostelForm.hostelName.trim() || !hostelForm.hostelType.trim()) {
            setError("Hostel code, name, and type are required.");
            return;
        }

        setIsSavingHostel(true);

        try {
            const savedHostel = await createHostel({
                ...hostelForm,
                hostelCode: hostelForm.hostelCode.trim(),
                hostelName: hostelForm.hostelName.trim(),
                hostelType: hostelForm.hostelType.trim(),
                address: hostelForm.address.trim(),
            });

            setHostels((current) => [...current, savedHostel]);
            setRoomForm((current) => ({ ...current, hostelId: savedHostel.hostelId }));
            setHostelForm(emptyHostelForm);
            setSuccess("Hostel saved successfully.");
        } catch (err) {
            setError(getErrorMessage(err, "Hostel save failed"));
        } finally {
            setIsSavingHostel(false);
        }
    }

    async function handleRoomSubmit(event: FormEvent) {
        event.preventDefault();
        setError("");
        setSuccess("");

        if (!roomForm.hostelId || !roomForm.roomNumber.trim() || !roomForm.roomType.trim() || roomForm.capacity < 1) {
            setError("Hostel, room number, room type, and valid capacity are required.");
            return;
        }

        setIsSavingRoom(true);

        try {
            const savedRoom = await createHostelRoom({
                ...roomForm,
                roomNumber: roomForm.roomNumber.trim(),
                roomType: roomForm.roomType.trim(),
                floorNumber: roomForm.floorNumber || null,
            });

            setRooms((current) => [...current, savedRoom]);
            setRoomForm((current) => ({
                ...emptyRoomForm,
                hostelId: current.hostelId,
            }));
            setSuccess("Room saved successfully.");
        } catch (err) {
            setError(getErrorMessage(err, "Room save failed"));
        } finally {
            setIsSavingRoom(false);
        }
    }

    return (
        <section className="request-page hostel-management-page">
            <div className="request-page-header">
                <div>
                    <p className="request-page-kicker">Admin</p>
                    <h1 className="request-page-title">Hostel Setup</h1>
                    <p className="request-page-summary">{rooms.length} rooms</p>
                </div>
                <button className="list-secondary-button" type="button" onClick={() => void loadData()} disabled={isLoading}>
                    Refresh
                </button>
            </div>

            {error && <p className="request-error" role="alert">{error}</p>}
            {success && <p className="request-success" role="status">{success}</p>}

            <div className="hostel-management-grid">
                <form className="hostel-form-panel" onSubmit={handleHostelSubmit} noValidate>
                    <h2>Hostel Create</h2>
                    <label className="hostel-form-field">
                        <span>Code</span>
                        <input value={hostelForm.hostelCode} onChange={(event) => setHostelForm((current) => ({ ...current, hostelCode: event.target.value }))} />
                    </label>
                    <label className="hostel-form-field">
                        <span>Name</span>
                        <input value={hostelForm.hostelName} onChange={(event) => setHostelForm((current) => ({ ...current, hostelName: event.target.value }))} />
                    </label>
                    <label className="hostel-form-field">
                        <span>Type</span>
                        <select value={hostelForm.hostelType} onChange={(event) => setHostelForm((current) => ({ ...current, hostelType: event.target.value }))}>
                            <option value="">Select</option>
                            <option value="Boys">Boys</option>
                            <option value="Girls">Girls</option>
                            <option value="Staff">Staff</option>
                            <option value="Guest">Guest</option>
                        </select>
                    </label>
                    <label className="hostel-form-field hostel-form-field-wide">
                        <span>Address</span>
                        <textarea value={hostelForm.address} onChange={(event) => setHostelForm((current) => ({ ...current, address: event.target.value }))} />
                    </label>
                    <button className="request-new-button" type="submit" disabled={isSavingHostel}>
                        {isSavingHostel ? "Saving..." : "Save Hostel"}
                    </button>
                </form>

                <form className="hostel-form-panel" onSubmit={handleRoomSubmit} noValidate>
                    <h2>Room Create</h2>
                    <label className="hostel-form-field">
                        <span>Hostel</span>
                        <select value={roomForm.hostelId || ""} onChange={(event) => setRoomForm((current) => ({ ...current, hostelId: Number(event.target.value) }))}>
                            <option value="">Select</option>
                            {hostelOptions.map((hostel) => (
                                <option key={hostel.hostelId} value={hostel.hostelId}>
                                    {hostel.hostelName}
                                </option>
                            ))}
                        </select>
                    </label>
                    <label className="hostel-form-field">
                        <span>Room No</span>
                        <input value={roomForm.roomNumber} onChange={(event) => setRoomForm((current) => ({ ...current, roomNumber: event.target.value }))} />
                    </label>
                    <label className="hostel-form-field">
                        <span>Floor</span>
                        <input type="number" value={roomForm.floorNumber ?? ""} onChange={(event) => setRoomForm((current) => ({ ...current, floorNumber: event.target.value ? Number(event.target.value) : null }))} />
                    </label>
                    <label className="hostel-form-field">
                        <span>Room Type</span>
                        <input value={roomForm.roomType} onChange={(event) => setRoomForm((current) => ({ ...current, roomType: event.target.value }))} />
                    </label>
                    <label className="hostel-form-field">
                        <span>Capacity</span>
                        <input type="number" min={1} value={roomForm.capacity} onChange={(event) => setRoomForm((current) => ({ ...current, capacity: Number(event.target.value) }))} />
                    </label>
                    <button className="request-new-button" type="submit" disabled={isSavingRoom || hostelOptions.length === 0}>
                        {isSavingRoom ? "Saving..." : "Save Room"}
                    </button>
                </form>
            </div>

            <div className="request-table-panel">
                {isLoading ? (
                    <div className="request-state">Loading hostels...</div>
                ) : rooms.length === 0 ? (
                    <div className="request-state">No rooms created.</div>
                ) : (
                    <div className="request-table-scroll">
                        <table className="request-table">
                            <thead>
                            <tr>
                                <th>Hostel</th>
                                <th>Room</th>
                                <th>Floor</th>
                                <th>Type</th>
                                <th>Capacity</th>
                                <th>Occupied</th>
                                <th>Available</th>
                            </tr>
                            </thead>
                            <tbody>
                            {rooms.map((room) => (
                                <tr key={room.roomId}>
                                    <td>{room.hostelName}</td>
                                    <td>{room.roomNumber}</td>
                                    <td>{room.floorNumber ?? "-"}</td>
                                    <td>{room.roomType}</td>
                                    <td>{room.capacity}</td>
                                    <td>{room.occupiedCount}</td>
                                    <td>{room.availableBeds}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </section>
    );
}
