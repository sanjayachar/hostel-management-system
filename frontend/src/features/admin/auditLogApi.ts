import { api } from "../../lib/api";

export type AuditLogRecord = {
    auditLogId: number;
    eventId: string;
    eventTime: string;
    serviceName: string;
    level: string;
    actorUserId?: number | null;
    actorUsername?: string | null;
    actorRole?: string | null;
    className?: string | null;
    methodName?: string | null;
    actionName?: string | null;
    requestPath?: string | null;
    httpMethod?: string | null;
    status: string;
    message?: string | null;
    errorMessage?: string | null;
    durationMs?: number | null;
    createdAt?: string;
};

export type AuditLogFilters = {
    serviceName?: string;
    level?: string;
    status?: string;
    fromTime?: string;
    toTime?: string;
    search?: string;
};

export type AuditLogPage = {
    content: AuditLogRecord[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
};

export async function getAuditLogs(filters: AuditLogFilters, page = 0, size = 20) {
    const params = new URLSearchParams({
        page: String(page),
        size: String(size),
    });

    Object.entries(filters).forEach(([key, value]) => {
        if (value?.trim()) {
            params.set(key, value.trim());
        }
    });

    const response = await api.get<AuditLogPage>(`/audit-log-service-api/admin/logs?${params.toString()}`);
    return response.data;
}
