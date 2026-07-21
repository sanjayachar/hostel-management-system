import { useEffect, useMemo, useState } from "react";
import axios from "axios";
import {
    decideAccommodationRequest,
    getAdminAccommodationRequests,
    getHostelRooms,
    type AdminAccommodationRequest,
    type HostelRoomRecord,
} from "./adminApi";

type DecisionDraft = {
    roomId: string;
    bedNumber: string;
    decisionNote: string;
};

function formatDate(value?: string) {
    if (!value) return "-";
    return new Intl.DateTimeFormat("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric",
    }).format(new Date(value));
}

function getStatusClass(status?: string) {
    const normalizedStatus = status?.toLowerCase() ?? "pending";

    if (normalizedStatus === "approved") return "request-status request-status-approved";
    if (normalizedStatus === "rejected") return "request-status request-status-rejected";
    if (normalizedStatus === "cancelled") return "request-status request-status-cancelled";

    return "request-status request-status-pending";
}

function getErrorMessage(error: unknown, fallback: string) {
    if (!axios.isAxiosError(error)) return fallback;

    const responseData = error.response?.data;

    if (responseData && typeof responseData === "object" && "message" in responseData) {
        return String(responseData.message);
    }

    if (!error.response) return "Cannot reach accommodation service.";

    return `${fallback}: ${error.response.status}`;
}

function getRowKey(request: AdminAccommodationRequest) {
    return `${request.audience}-${request.requestId}`;
}

function isPending(request: AdminAccommodationRequest) {
    return (request.status ?? "Pending").toLowerCase() === "pending";
}

export function AdminAccommodationRequestsPage() {
    const [requests, setRequests] = useState<AdminAccommodationRequest[]>([]);
    const [rooms, setRooms] = useState<HostelRoomRecord[]>([]);
    const [decisionDrafts, setDecisionDrafts] = useState<Record<string, DecisionDraft>>({});
    const [search, setSearch] = useState("");
    const [page, setPage] = useState(0);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [busyRequestKey, setBusyRequestKey] = useState("");
    const pageSize = 10;

    const filteredRequests = useMemo(() => {
        const searchValue = search.trim().toLowerCase();

        if (!searchValue) return requests;

        return requests.filter((request) => [
            request.audience,
            request.requesterName,
            request.requesterCode,
            request.userId,
            request.requestType,
            request.reason,
            request.fromDate,
            request.toDate,
            request.status,
            request.hostelName,
            request.roomNumber,
            request.noOfDays,
            request.noOfPersons,
        ].join(" ").toLowerCase().includes(searchValue));
    }, [requests, search]);

    const totalPages = Math.ceil(filteredRequests.length / pageSize);
    const visibleRequests = useMemo(() => {
        const start = page * pageSize;
        return filteredRequests.slice(start, start + pageSize);
    }, [filteredRequests, page]);

    const countLabel = useMemo(() => {
        if (search.trim()) return `${filteredRequests.length} of ${requests.length} requests`;
        if (requests.length === 1) return "1 request";
        return `${requests.length} requests`;
    }, [filteredRequests.length, requests.length, search]);

    async function loadData() {
        setIsLoading(true);
        setError("");

        try {
            const [requestData, roomData] = await Promise.all([
                getAdminAccommodationRequests(),
                getHostelRooms(),
            ]);
            setRequests(requestData);
            setRooms(roomData);
        } catch (err) {
            setError(getErrorMessage(err, "Accommodation request list failed"));
        } finally {
            setIsLoading(false);
        }
    }

    useEffect(() => {
        void loadData();
    }, []);

    useEffect(() => {
        setPage(0);
    }, [search]);

    function getDecisionDraft(request: AdminAccommodationRequest) {
        return decisionDrafts[getRowKey(request)] ?? { roomId: "", bedNumber: "", decisionNote: "" };
    }

    function updateDecisionDraft(request: AdminAccommodationRequest, patch: Partial<DecisionDraft>) {
        const rowKey = getRowKey(request);
        setDecisionDrafts((current) => {
            const existingDraft = current[rowKey] ?? { roomId: "", bedNumber: "", decisionNote: "" };

            return {
                ...current,
                [rowKey]: {
                    ...existingDraft,
                    ...patch,
                },
            };
        });
    }

    async function handleDecision(request: AdminAccommodationRequest, status: "Approved" | "Rejected") {
        const rowKey = getRowKey(request);
        const draft = getDecisionDraft(request);
        setError("");
        setSuccess("");

        if (status === "Approved" && !draft.roomId) {
            setError("Please select a room before approving.");
            return;
        }

        setBusyRequestKey(rowKey);

        try {
            const updatedRequest = await decideAccommodationRequest(request.requestId, {
                status,
                roomId: draft.roomId ? Number(draft.roomId) : undefined,
                bedNumber: draft.bedNumber.trim(),
                decisionNote: draft.decisionNote.trim(),
            });

            setRequests((current) => current.map((item) => (
                getRowKey(item) === rowKey ? { ...item, ...updatedRequest, audience: item.audience } : item
            )));
            setDecisionDrafts((current) => {
                const next = { ...current };
                delete next[rowKey];
                return next;
            });
            setRooms(await getHostelRooms());
            setSuccess(`Request ${status.toLowerCase()} successfully.`);
        } catch (err) {
            setError(getErrorMessage(err, `Request ${status.toLowerCase()} failed`));
        } finally {
            setBusyRequestKey("");
        }
    }

    return (
        <section className="request-page">
            <div className="request-page-header">
                <div>
                    <p className="request-page-kicker">Admin</p>
                    <h1 className="request-page-title">Accommodation Request List</h1>
                    <p className="request-page-summary">{countLabel}</p>
                </div>
                <button className="list-secondary-button" type="button" onClick={() => void loadData()} disabled={isLoading}>
                    Refresh
                </button>
            </div>

            {error && <p className="request-error" role="alert">{error}</p>}
            {success && <p className="request-success" role="status">{success}</p>}

            <div className="request-table-panel">
                {isLoading ? (
                    <div className="request-state">Loading requests...</div>
                ) : requests.length === 0 ? (
                    <div className="request-state">No accommodation requests found.</div>
                ) : (
                    <>
                        <div className="list-filter-panel">
                            <label className="list-search-field">
                                <span>Search</span>
                                <input
                                    value={search}
                                    onChange={(event) => setSearch(event.target.value)}
                                    placeholder="Search requests"
                                />
                            </label>
                        </div>
                        {filteredRequests.length === 0 ? (
                            <div className="request-state">No matching requests found.</div>
                        ) : (
                            <div className="request-table-scroll">
                                <table className="request-table request-approval-table">
                                    <thead>
                                    <tr>
                                        <th>User Type</th>
                                        <th>Requested By</th>
                                        <th>Type</th>
                                        <th>Reason</th>
                                        <th>From</th>
                                        <th>To</th>
                                        <th>Persons</th>
                                        <th>Status</th>
                                        <th>Allocation</th>
                                        <th>Decision</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {visibleRequests.map((request) => {
                                        const draft = getDecisionDraft(request);
                                        const rowKey = getRowKey(request);
                                        const availableRooms = rooms.filter((room) => room.availableBeds >= (request.noOfPersons || 1));

                                        return (
                                            <tr key={rowKey}>
                                                <td>{request.audience}</td>
                                                <td>
                                                    <span className="requester-label">
                                                        <span>{request.requesterName || `User #${request.userId ?? "-"}`}</span>
                                                        <span>{request.requesterCode || "-"}</span>
                                                    </span>
                                                </td>
                                                <td>{request.requestType || "-"}</td>
                                                <td>{request.reason}</td>
                                                <td>{formatDate(request.fromDate)}</td>
                                                <td>{formatDate(request.toDate)}</td>
                                                <td>{request.noOfPersons}</td>
                                                <td>
                                                    <span className={getStatusClass(request.status)}>
                                                        {request.status ?? "Pending"}
                                                    </span>
                                                </td>
                                                <td>
                                                    {request.hostelName ? (
                                                        <span className="allocation-label">
                                                            {request.hostelName} / {request.roomNumber}
                                                            {request.bedNumber ? ` / ${request.bedNumber}` : ""}
                                                        </span>
                                                    ) : "-"}
                                                </td>
                                                <td>
                                                    {isPending(request) ? (
                                                        <div className="request-decision-controls">
                                                            <select value={draft.roomId} onChange={(event) => updateDecisionDraft(request, { roomId: event.target.value })}>
                                                                <option value="">Select room</option>
                                                                {availableRooms.map((room) => (
                                                                    <option key={room.roomId} value={room.roomId}>
                                                                        {room.hostelName} - {room.roomNumber} ({room.availableBeds} free)
                                                                    </option>
                                                                ))}
                                                            </select>
                                                            <input
                                                                value={draft.bedNumber}
                                                                onChange={(event) => updateDecisionDraft(request, { bedNumber: event.target.value })}
                                                                placeholder="Bed"
                                                            />
                                                            <input
                                                                value={draft.decisionNote}
                                                                onChange={(event) => updateDecisionDraft(request, { decisionNote: event.target.value })}
                                                                placeholder="Note"
                                                            />
                                                            <div className="request-decision-buttons">
                                                                <button
                                                                    className="request-approve-button"
                                                                    type="button"
                                                                    onClick={() => void handleDecision(request, "Approved")}
                                                                    disabled={busyRequestKey === rowKey || availableRooms.length === 0}
                                                                >
                                                                    Approve
                                                                </button>
                                                                <button
                                                                    className="request-reject-button"
                                                                    type="button"
                                                                    onClick={() => void handleDecision(request, "Rejected")}
                                                                    disabled={busyRequestKey === rowKey}
                                                                >
                                                                    Reject
                                                                </button>
                                                            </div>
                                                        </div>
                                                    ) : (
                                                        request.decisionNote || "-"
                                                    )}
                                                </td>
                                            </tr>
                                        );
                                    })}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </>
                )}
            </div>

            {!isLoading && requests.length > 0 && (
                <div className="list-pagination">
                    <button className="list-secondary-button" type="button" onClick={() => setPage((current) => current - 1)} disabled={page === 0}>
                        Previous
                    </button>
                    <span>
                        Page {totalPages === 0 ? 0 : page + 1} of {totalPages}
                    </span>
                    <button
                        className="list-secondary-button"
                        type="button"
                        onClick={() => setPage((current) => current + 1)}
                        disabled={totalPages === 0 || page >= totalPages - 1}
                    >
                        Next
                    </button>
                </div>
            )}
        </section>
    );
}
