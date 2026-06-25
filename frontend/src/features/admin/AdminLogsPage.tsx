import { type ChangeEvent, type FormEvent, useEffect, useMemo, useState } from "react";
import axios from "axios";
import { getAuditLogs, type AuditLogFilters, type AuditLogPage, type AuditLogRecord } from "./auditLogApi";

const emptyPage: AuditLogPage = {
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size: 20,
};

const services = [
    { label: "All Services", value: "" },
    { label: "Auth", value: "auth-service" },
    { label: "Student", value: "student-service" },
    { label: "Staff", value: "staff-service" },
    { label: "Accommodation", value: "accommodation-service" },
    { label: "Other Candidate", value: "other-candidate-service" },
];

function formatDateTime(value?: string) {
    if (!value) return "-";

    return new Intl.DateTimeFormat("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
    }).format(new Date(value));
}

function formatDuration(value?: number | null) {
    if (value === null || value === undefined) return "-";
    return `${value} ms`;
}

function getLevelClass(level: string) {
    return level.toUpperCase() === "ERROR" ? "log-level log-level-error" : "log-level log-level-info";
}

function getStatusClass(status: string) {
    return status.toUpperCase() === "ERROR"
        ? "request-status request-status-rejected"
        : "request-status request-status-approved";
}

function getErrorMessage(error: unknown) {
    if (!axios.isAxiosError(error)) {
        return "Could not load audit logs.";
    }

    if (error.response?.status === 401 || error.response?.status === 403) {
        return "Only admin users can view audit logs.";
    }

    if (!error.response) {
        return "Cannot reach audit-log-service. Check that it is running on port 8086.";
    }

    return `Audit logs failed: ${error.response.status}`;
}

function getDisplayAction(log: AuditLogRecord) {
    return log.actionName || log.methodName || "-";
}

export function AdminLogsPage() {
    const [draftFilters, setDraftFilters] = useState<AuditLogFilters>({});
    const [appliedFilters, setAppliedFilters] = useState<AuditLogFilters>({});
    const [logPage, setLogPage] = useState<AuditLogPage>(emptyPage);
    const [page, setPage] = useState(0);
    const [reloadKey, setReloadKey] = useState(0);
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(true);

    const countLabel = useMemo(() => {
        if (logPage.totalElements === 1) return "1 log";
        return `${logPage.totalElements} logs`;
    }, [logPage.totalElements]);

    useEffect(() => {
        let isMounted = true;

        async function loadLogs() {
            setIsLoading(true);
            setError("");

            try {
                const data = await getAuditLogs(appliedFilters, page);

                if (isMounted) {
                    setLogPage(data);
                }
            } catch (err) {
                if (isMounted) {
                    setError(getErrorMessage(err));
                    setLogPage(emptyPage);
                }
            } finally {
                if (isMounted) {
                    setIsLoading(false);
                }
            }
        }

        void loadLogs();

        return () => {
            isMounted = false;
        };
    }, [appliedFilters, page, reloadKey]);

    function updateFilter(event: ChangeEvent<HTMLInputElement | HTMLSelectElement>) {
        const { name, value } = event.target;
        setDraftFilters((current) => ({
            ...current,
            [name]: value,
        }));
    }

    function applyFilters(event: FormEvent) {
        event.preventDefault();
        setPage(0);
        setAppliedFilters(draftFilters);
    }

    function resetFilters() {
        setDraftFilters({});
        setAppliedFilters({});
        setPage(0);
    }

    return (
        <section className="request-page logs-page">
            <div className="request-page-header">
                <div>
                    <p className="request-page-kicker">Admin</p>
                    <h1 className="request-page-title">Day To Day Logs</h1>
                    <p className="request-page-summary">{countLabel}</p>
                </div>
                <button className="request-new-button" type="button" onClick={() => setReloadKey((current) => current + 1)}>
                    Refresh
                </button>
            </div>

            <form className="logs-filter-panel" onSubmit={applyFilters}>
                <label className="logs-filter-field">
                    <span>Service</span>
                    <select name="serviceName" value={draftFilters.serviceName ?? ""} onChange={updateFilter}>
                        {services.map((service) => (
                            <option key={service.value || "all"} value={service.value}>
                                {service.label}
                            </option>
                        ))}
                    </select>
                </label>

                <label className="logs-filter-field">
                    <span>Level</span>
                    <select name="level" value={draftFilters.level ?? ""} onChange={updateFilter}>
                        <option value="">All Levels</option>
                        <option value="INFO">Info</option>
                        <option value="ERROR">Error</option>
                    </select>
                </label>

                <label className="logs-filter-field">
                    <span>Status</span>
                    <select name="status" value={draftFilters.status ?? ""} onChange={updateFilter}>
                        <option value="">All Status</option>
                        <option value="SUCCESS">Success</option>
                        <option value="ERROR">Error</option>
                    </select>
                </label>

                <label className="logs-filter-field">
                    <span>From</span>
                    <input name="fromTime" type="datetime-local" value={draftFilters.fromTime ?? ""} onChange={updateFilter} />
                </label>

                <label className="logs-filter-field">
                    <span>To</span>
                    <input name="toTime" type="datetime-local" value={draftFilters.toTime ?? ""} onChange={updateFilter} />
                </label>

                <label className="logs-filter-field logs-filter-search">
                    <span>Search</span>
                    <input
                        name="search"
                        value={draftFilters.search ?? ""}
                        onChange={updateFilter}
                        placeholder="Method, user, message"
                    />
                </label>

                <div className="logs-filter-actions">
                    <button className="request-new-button" type="submit">
                        Apply
                    </button>
                    <button className="logs-secondary-button" type="button" onClick={resetFilters}>
                        Reset
                    </button>
                </div>
            </form>

            {error && <p className="request-error" role="alert">{error}</p>}

            <div className="request-table-panel">
                {isLoading ? (
                    <div className="request-state">Loading logs...</div>
                ) : logPage.content.length === 0 ? (
                    <div className="request-state">No audit logs found.</div>
                ) : (
                    <div className="request-table-scroll">
                        <table className="request-table logs-table">
                            <thead>
                            <tr>
                                <th>Time</th>
                                <th>Service</th>
                                <th>Level</th>
                                <th>User</th>
                                <th>Action</th>
                                <th>Status</th>
                                <th>Duration</th>
                                <th>Message</th>
                            </tr>
                            </thead>
                            <tbody>
                            {logPage.content.map((log) => (
                                <tr key={log.auditLogId}>
                                    <td>{formatDateTime(log.eventTime)}</td>
                                    <td>
                                        <span className="logs-service-name">{log.serviceName}</span>
                                        <span className="logs-path">{log.httpMethod} {log.requestPath}</span>
                                    </td>
                                    <td>
                                        <span className={getLevelClass(log.level)}>{log.level}</span>
                                    </td>
                                    <td>
                                        <span className="logs-user-name">{log.actorUsername || "-"}</span>
                                        <span className="logs-role">{log.actorRole || "-"}</span>
                                    </td>
                                    <td>
                                        <span className="logs-action">{getDisplayAction(log)}</span>
                                        <span className="logs-class-name">{log.className || "-"}</span>
                                    </td>
                                    <td>
                                        <span className={getStatusClass(log.status)}>{log.status}</span>
                                    </td>
                                    <td>{formatDuration(log.durationMs)}</td>
                                    <td>
                                        <span className="logs-message">{log.errorMessage || log.message || "-"}</span>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>

            <div className="logs-pagination">
                <button className="logs-secondary-button" type="button" onClick={() => setPage((current) => current - 1)} disabled={page === 0}>
                    Previous
                </button>
                <span>
                    Page {logPage.totalPages === 0 ? 0 : page + 1} of {logPage.totalPages}
                </span>
                <button
                    className="logs-secondary-button"
                    type="button"
                    onClick={() => setPage((current) => current + 1)}
                    disabled={logPage.totalPages === 0 || page >= logPage.totalPages - 1}
                >
                    Next
                </button>
            </div>
        </section>
    );
}
