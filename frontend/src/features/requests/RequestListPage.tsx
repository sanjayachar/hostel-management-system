import { useEffect, useMemo, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { getRequestList, type AccommodationRequest, type RequestAudience } from "./requestApi";

type RequestListPageProps = {
    audience: RequestAudience;
};

const audienceLabels: Record<RequestAudience, string> = {
    student: "Student Requests",
    staff: "Staff Requests",
    candidate: "Candidate Requests",
};

const newRequestPaths: Record<RequestAudience, string> = {
    student: "/student/new-request",
    staff: "/staff/new-request",
    candidate: "/candidate/new-request",
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

export function RequestListPage({ audience }: RequestListPageProps) {
    const navigate = useNavigate();
    const [requests, setRequests] = useState<AccommodationRequest[]>([]);
    const [search, setSearch] = useState("");
    const [page, setPage] = useState(0);
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const pageSize = 10;

    const title = audienceLabels[audience];

    const filteredRequests = useMemo(() => {
        const searchValue = search.trim().toLowerCase();

        if (!searchValue) return requests;

        return requests.filter((request) => [
            request.requestType,
            request.reason,
            request.fromDate,
            request.toDate,
            request.noOfDays,
            request.noOfPersons,
            request.status,
            request.hostelName,
            request.roomNumber,
            request.bedNumber,
            request.allocationStatus,
            request.decisionNote,
        ].join(" ").toLowerCase().includes(searchValue));
    }, [requests, search]);

    const totalPages = Math.ceil(filteredRequests.length / pageSize);
    const visibleRequests = useMemo(() => {
        const start = page * pageSize;
        return filteredRequests.slice(start, start + pageSize);
    }, [filteredRequests, page]);

    const requestCountLabel = useMemo(() => {
        if (search.trim()) {
            return `${filteredRequests.length} of ${requests.length} requests`;
        }

        if (requests.length === 1) return "1 request";
        return `${requests.length} requests`;
    }, [filteredRequests.length, requests.length, search]);

    useEffect(() => {
        setPage(0);
    }, [search]);

    useEffect(() => {
        let isMounted = true;

        async function loadRequests() {
            setIsLoading(true);
            setError("");

            try {
                const data = await getRequestList(audience);

                if (isMounted) {
                    setRequests(Array.isArray(data) ? data : []);
                }
            } catch (err) {
                if (!isMounted) return;

                if (axios.isAxiosError(err)) {
                    if (err.response?.status === 401 || err.response?.status === 403) {
                        setError("You are not allowed to view this request list.");
                        return;
                    }

                    if (!err.response) {
                        setError("Cannot reach the request service. Check that the backend service is running.");
                        return;
                    }

                    setError(`Request list failed: ${err.response.status}`);
                    return;
                }

                setError("Request list failed. Please try again.");
            } finally {
                if (isMounted) {
                    setIsLoading(false);
                }
            }
        }

        void loadRequests();

        return () => {
            isMounted = false;
        };
    }, [audience]);

    function handleNewRequest() {
        navigate(newRequestPaths[audience]);
    }

    return (
        <section className="request-page">
            <div className="request-page-header">
                <div>
                    <p className="request-page-kicker">Accommodation</p>
                    <h1 className="request-page-title">{title}</h1>
                    <p className="request-page-summary">{requestCountLabel}</p>
                </div>

                <button className="request-new-button" type="button" onClick={handleNewRequest}>
                    New Request
                </button>
            </div>

            {error && <p className="request-error" role="alert">{error}</p>}

            <div className="request-table-panel">
                {isLoading ? (
                    <div className="request-state">Loading requests...</div>
                ) : requests.length === 0 ? (
                    <div className="request-state">No requests found.</div>
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
                                <table className="request-table request-user-table">
                                    <thead>
                                    <tr>
                                        <th>Type</th>
                                        <th>Reason</th>
                                        <th>From</th>
                                        <th>To</th>
                                        <th>Days</th>
                                        <th>Persons</th>
                                        <th>Status</th>
                                        <th>Hostel</th>
                                        <th>Room</th>
                                        <th>Bed</th>
                                        <th>Note</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {visibleRequests.map((request) => (
                                        <tr key={request.requestId}>
                                            <td>{request.requestType || "-"}</td>
                                            <td>{request.reason}</td>
                                            <td>{formatDate(request.fromDate)}</td>
                                            <td>{formatDate(request.toDate)}</td>
                                            <td>{request.noOfDays}</td>
                                            <td>{request.noOfPersons}</td>
                                            <td>
                                                <span className={getStatusClass(request.status)}>
                                                    {request.status ?? "Pending"}
                                                </span>
                                            </td>
                                            <td>{request.hostelName || "-"}</td>
                                            <td>{request.roomNumber || "-"}</td>
                                            <td>{request.bedNumber || "-"}</td>
                                            <td>{request.decisionNote || (request.hostelName ? "Allotted" : "-")}</td>
                                        </tr>
                                    ))}
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
