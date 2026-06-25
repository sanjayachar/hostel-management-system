import { DataTablePage, type DataColumn } from "../../components/data/DataTablePage";
import { getCandidateList, type CandidateRecord } from "./adminApi";

const columns: DataColumn<CandidateRecord>[] = [
    { header: "Candidate Code", render: (candidate) => candidate.candidateCode },
    { header: "Name", render: (candidate) => `${candidate.firstName} ${candidate.lastName}` },
    { header: "Gender", render: (candidate) => candidate.gender },
    { header: "Applied Post", render: (candidate) => candidate.appliedPost },
    { header: "Email", render: (candidate) => candidate.email },
    { header: "Contact", render: (candidate) => candidate.contactNumber },
];

export function AdminCandidateListPage() {
    return (
        <DataTablePage
            title="Other Candidate List"
            kicker="Admin"
            emptyMessage="No candidate records found."
            loadData={getCandidateList}
            columns={columns}
            getRowKey={(candidate) => candidate.candidateId}
            getSearchText={(candidate) => [
                candidate.candidateCode,
                candidate.firstName,
                candidate.lastName,
                candidate.gender,
                candidate.appliedPost,
                candidate.email,
                candidate.contactNumber,
            ].join(" ")}
            searchPlaceholder="Search candidates"
        />
    );
}
