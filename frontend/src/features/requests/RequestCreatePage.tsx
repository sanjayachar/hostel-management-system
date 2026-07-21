import { type ChangeEvent, type FormEvent, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import {
    createAccommodationRequest,
    type AccommodationRequestForm,
    type RequestAudience,
} from "./requestApi";
import "../../assets/css/LoginPage.css";
import "../../assets/css/PublicPages.css";

type FieldErrors = Record<string, string>;

type RequestCreatePageProps = {
    audience: RequestAudience;
};

const audienceLabels: Record<RequestAudience, string> = {
    student: "Student",
    staff: "Staff",
    candidate: "Candidate",
};

const requestListPaths: Record<RequestAudience, string> = {
    student: "/student/requests",
    staff: "/staff/requests",
    candidate: "/candidate/requests",
};

function formatDateForInput(date: Date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

function calculateNoOfDays(fromDate: string, toDate: string) {
    if (!fromDate || !toDate) return 0;

    const start = new Date(fromDate);
    const end = new Date(toDate);

    if (Number.isNaN(start.getTime()) || Number.isNaN(end.getTime()) || end < start) {
        return 0;
    }

    const millisecondsPerDay = 24 * 60 * 60 * 1000;
    return Math.floor((end.getTime() - start.getTime()) / millisecondsPerDay) + 1;
}

function createInitialForm(): AccommodationRequestForm {
    const today = formatDateForInput(new Date());

    return {
        requestType: "",
        reason: "",
        fromDate: today,
        toDate: today,
        noOfDays: 1,
        noOfPersons: 1,
    };
}

function validateRequest(form: AccommodationRequestForm) {
    const errors: FieldErrors = {};

    if (!form.requestType.trim()) errors.requestType = "Request type is required.";
    if (!form.reason.trim()) errors.reason = "Request reason is required.";
    if (!form.fromDate) errors.fromDate = "From date is required.";
    if (!form.toDate) errors.toDate = "To date is required.";

    if (form.fromDate && form.toDate && new Date(form.toDate) < new Date(form.fromDate)) {
        errors.toDate = "To date must be after From date";
    }

    if (!form.noOfDays || form.noOfDays < 1) errors.noOfDays = "Number of days is required.";
    if (!form.noOfPersons || form.noOfPersons < 1) errors.noOfPersons = "Number of persons is required.";

    return errors;
}

function parseBackendErrors(error: unknown) {
    const errors: FieldErrors = {};
    let alertMessage = "Request creation failed. Please check the form.";

    if (!axios.isAxiosError(error)) {
        return { errors, alertMessage };
    }

    if (!error.response) {
        return {
            errors,
            alertMessage: "Cannot reach the request service. Check that the backend service is running.",
        };
    }

    const data = error.response.data;

    if (data && typeof data === "object" && !Array.isArray(data)) {
        if ("message" in data && typeof data.message === "string") {
            alertMessage = data.message;
        } else {
            Object.entries(data).forEach(([field, message]) => {
                if (typeof message === "string") {
                    errors[field] = message;
                }
            });
        }
    }

    return { errors, alertMessage };
}

export function RequestCreatePage({ audience }: RequestCreatePageProps) {
    const navigate = useNavigate();
    const [form, setForm] = useState<AccommodationRequestForm>(() => createInitialForm());
    const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
    const [alertMessage, setAlertMessage] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const today = useMemo(() => formatDateForInput(new Date()), []);
    const listPath = requestListPaths[audience];
    const roleLabel = audienceLabels[audience];

    function updateField(event: ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) {
        const { name, value } = event.target;

        setForm((current) => {
            const nextForm = {
                ...current,
                [name]: name === "noOfPersons" ? Number(value) : value,
            };

            if (name === "fromDate" || name === "toDate") {
                nextForm.noOfDays = calculateNoOfDays(nextForm.fromDate, nextForm.toDate);
            }

            return nextForm;
        });

        setFieldErrors((current) => ({ ...current, [name]: "" }));
    }

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();
        setAlertMessage("");
        setSuccessMessage("");

        const validationErrors = validateRequest(form);

        if (Object.keys(validationErrors).length > 0) {
            setFieldErrors(validationErrors);
            setAlertMessage("Please fix the highlighted fields.");
            return;
        }

        setFieldErrors({});
        setIsSubmitting(true);

        try {
            await createAccommodationRequest(audience, form);
            setSuccessMessage("Accommodation request saved successfully.");
            setForm(createInitialForm());
            setTimeout(() => navigate(listPath), 450);
        } catch (error) {
            const backendError = parseBackendErrors(error);
            setFieldErrors(backendError.errors);
            setAlertMessage(
                Object.keys(backendError.errors).length > 0
                    ? "Please fix the validation errors returned by the server."
                    : backendError.alertMessage,
            );
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <section className="register-panel">
            <div className="public-header">
                <p className="landing-kicker">{roleLabel}</p>
                <h1 className="landing-title">New Accommodation Request</h1>
                <p className="landing-copy">Submit a request for stay, leave, or related accommodation needs.</p>
            </div>

            {alertMessage && (
                <div className="form-alert form-alert-error" role="alert">
                    <p>{alertMessage}</p>
                    {Object.keys(fieldErrors).length > 0 && (
                        <ul>
                            {Object.entries(fieldErrors).map(([field, message]) => (
                                message ? <li key={field}>{message}</li> : null
                            ))}
                        </ul>
                    )}
                </div>
            )}

            {successMessage && (
                <div className="form-alert form-alert-success" role="status">
                    {successMessage}
                </div>
            )}

            <form className="register-form" onSubmit={handleSubmit} noValidate>
                <SelectField name="requestType" label="Request Type" value={form.requestType} error={fieldErrors.requestType} onChange={updateField} />
                <NumberField name="noOfPersons" label="Number of Persons" value={form.noOfPersons} error={fieldErrors.noOfPersons} onChange={updateField} />
                <TextField name="fromDate" label="From Date" type="date" value={form.fromDate} error={fieldErrors.fromDate} onChange={updateField} min={today} />
                <TextField name="toDate" label="To Date" type="date" value={form.toDate} error={fieldErrors.toDate} onChange={updateField} min={form.fromDate || today} />
                <NumberField name="noOfDays" label="Number of Days" value={form.noOfDays} error={fieldErrors.noOfDays} onChange={updateField} readOnly />
                <TextAreaField name="reason" label="Reason" value={form.reason} error={fieldErrors.reason} onChange={updateField} />

                <div className="register-actions">
                    <button className="login-button" type="submit" disabled={isSubmitting}>
                        {isSubmitting ? "Submitting..." : "Submit"}
                    </button>
                    <div className="register-action-links">
                        <Link className="public-button-link" to={listPath}>Request List</Link>
                        <Link className="public-link" to={`/${audience}`}>Dashboard</Link>
                    </div>
                </div>
            </form>
        </section>
    );
}

type FieldProps = {
    name: string;
    label: string;
    value: string;
    error?: string;
    type?: string;
    readOnly?: boolean;
    min?: string;
    onChange: (event: ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => void;
};

type NumberFieldProps = Omit<FieldProps, "value"> & {
    value: number;
};

function TextField({ name, label, value, error, type = "text", readOnly = false, min, onChange }: FieldProps) {
    return (
        <div className="form-field">
            <label className="login-label" htmlFor={name}>{label}</label>
            <input
                className="login-input"
                id={name}
                name={name}
                type={type}
                value={value}
                onChange={onChange}
                readOnly={readOnly}
                min={min}
                aria-invalid={Boolean(error)}
                aria-describedby={error ? `${name}-error` : undefined}
            />
            {error && <p className="field-error" id={`${name}-error`}>{error}</p>}
        </div>
    );
}

function NumberField({ name, label, value, error, readOnly = false, onChange }: NumberFieldProps) {
    return (
        <div className="form-field">
            <label className="login-label" htmlFor={name}>{label}</label>
            <input
                className="login-input"
                id={name}
                name={name}
                type="number"
                min="1"
                value={value}
                onChange={onChange}
                readOnly={readOnly}
                aria-invalid={Boolean(error)}
                aria-describedby={error ? `${name}-error` : undefined}
            />
            {error && <p className="field-error" id={`${name}-error`}>{error}</p>}
        </div>
    );
}

function TextAreaField({ name, label, value, error, onChange }: FieldProps) {
    return (
        <div className="form-field form-field-wide">
            <label className="login-label" htmlFor={name}>{label}</label>
            <textarea
                className="login-input form-textarea"
                id={name}
                name={name}
                value={value}
                onChange={onChange}
                aria-invalid={Boolean(error)}
                aria-describedby={error ? `${name}-error` : undefined}
            />
            {error && <p className="field-error" id={`${name}-error`}>{error}</p>}
        </div>
    );
}

function SelectField({ name, label, value, error, onChange }: FieldProps) {
    return (
        <div className="form-field">
            <label className="login-label" htmlFor={name}>{label}</label>
            <select
                className="login-input form-select"
                id={name}
                name={name}
                value={value}
                onChange={onChange}
                aria-invalid={Boolean(error)}
                aria-describedby={error ? `${name}-error` : undefined}
            >
                <option value="">Select</option>
                <option value="Accommodation">Accommodation</option>
                <option value="Leave">Leave</option>
                <option value="Guest Stay">Guest Stay</option>
                <option value="Vacation">Vacation</option>
                <option value="Other">Other</option>
            </select>
            {error && <p className="field-error" id={`${name}-error`}>{error}</p>}
        </div>
    );
}
