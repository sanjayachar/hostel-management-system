import { type ReactNode, useEffect, useMemo, useState } from "react";
import axios from "axios";

export type DataColumn<T> = {
    header: string;
    render: (item: T) => ReactNode;
};

type DataTablePageProps<T> = {
    title: string;
    kicker: string;
    emptyMessage: string;
    loadData: () => Promise<T[]>;
    columns: DataColumn<T>[];
    getRowKey: (item: T) => string | number;
    getSearchText?: (item: T) => string;
    searchPlaceholder?: string;
    pageSize?: number;
};

export function DataTablePage<T>({
                                     title,
                                     kicker,
                                     emptyMessage,
                                     loadData,
                                     columns,
                                     getRowKey,
                                     getSearchText,
                                     searchPlaceholder = "Search records",
                                     pageSize = 10,
                                 }: DataTablePageProps<T>) {
    const [rows, setRows] = useState<T[]>([]);
    const [search, setSearch] = useState("");
    const [page, setPage] = useState(0);
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(true);

    const filteredRows = useMemo(() => {
        const searchValue = search.trim().toLowerCase();

        if (!searchValue) return rows;

        return rows.filter((row) => {
            const searchableText = getSearchText ? getSearchText(row) : JSON.stringify(row);
            return searchableText.toLowerCase().includes(searchValue);
        });
    }, [getSearchText, rows, search]);

    const totalPages = Math.ceil(filteredRows.length / pageSize);
    const visibleRows = useMemo(() => {
        const start = page * pageSize;
        return filteredRows.slice(start, start + pageSize);
    }, [filteredRows, page, pageSize]);

    const countLabel = useMemo(() => {
        if (search.trim()) {
            return `${filteredRows.length} of ${rows.length} records`;
        }

        if (rows.length === 1) return "1 record";
        return `${rows.length} records`;
    }, [filteredRows.length, rows.length, search]);

    useEffect(() => {
        setPage(0);
    }, [search]);

    useEffect(() => {
        let isMounted = true;

        async function fetchRows() {
            setIsLoading(true);
            setError("");

            try {
                const data = await loadData();

                if (isMounted) {
                    setRows(Array.isArray(data) ? data : []);
                }
            } catch (err) {
                if (!isMounted) return;

                if (axios.isAxiosError(err)) {
                    if (err.response?.status === 401 || err.response?.status === 403) {
                        setError("You are not allowed to view this list.");
                        return;
                    }

                    if (!err.response) {
                        setError("Cannot reach the backend service for this list.");
                        return;
                    }

                    setError(`List failed: ${err.response.status}`);
                    return;
                }

                setError("List failed. Please try again.");
            } finally {
                if (isMounted) {
                    setIsLoading(false);
                }
            }
        }

        void fetchRows();

        return () => {
            isMounted = false;
        };
    }, [loadData]);

    return (
        <section className="request-page">
            <div className="request-page-header">
                <div>
                    <p className="request-page-kicker">{kicker}</p>
                    <h1 className="request-page-title">{title}</h1>
                    <p className="request-page-summary">{countLabel}</p>
                </div>
            </div>

            {error && <p className="request-error" role="alert">{error}</p>}

            <div className="request-table-panel">
                {isLoading ? (
                    <div className="request-state">Loading records...</div>
                ) : rows.length === 0 ? (
                            <div className="request-state">{emptyMessage}</div>
                ) : (
                    <>
                        <div className="list-filter-panel">
                            <label className="list-search-field">
                                <span>Search</span>
                                <input
                                    value={search}
                                    onChange={(event) => setSearch(event.target.value)}
                                    placeholder={searchPlaceholder}
                                />
                            </label>
                        </div>
                        {filteredRows.length === 0 ? (
                            <div className="request-state">No matching records found.</div>
                        ) : (
                            <div className="request-table-scroll">
                                <table className="request-table">
                                    <thead>
                                    <tr>
                                        {columns.map((column) => (
                                            <th key={column.header}>{column.header}</th>
                                        ))}
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {visibleRows.map((row) => (
                                        <tr key={getRowKey(row)}>
                                            {columns.map((column) => (
                                                <td key={column.header}>{column.render(row)}</td>
                                            ))}
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </>
                )}
            </div>

            {!isLoading && rows.length > 0 && (
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
