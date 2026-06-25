import { type ChangeEvent, type FormEvent, useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import { createStaff, getNextEmployeeCode, type StaffCreateForm } from "./adminApi";
import "../../assets/css/LoginPage.css";
import "../../assets/css/PublicPages.css";

type FieldErrors = Record<string, string>;

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const contactPattern = /^[0-9]{10}$/;

function formatDateForInput(date: Date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

function getMinimumDateOfBirth() {
    const date = new Date();
    date.setFullYear(date.getFullYear() - 25);
    return formatDateForInput(date);
}

function createInitialForm(): StaffCreateForm {
    return {
        employeeCode: "",
        firstName: "",
        lastName: "",
        gender: "",
        dateOfBirth: getMinimumDateOfBirth(),
        contactNumber: "",
        email: "",
        address: "",
        designation: "",
        department: "",
        dateOfJoining: formatDateForInput(new Date()),
    };
}

function calculateAge(dateOfBirth: string) {
    const birthDate = new Date(dateOfBirth);
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDifference = today.getMonth() - birthDate.getMonth();

    if (monthDifference < 0 || (monthDifference === 0 && today.getDate() < birthDate.getDate())) {
        age -= 1;
    }

    return age;
}

function validateStaff(form: StaffCreateForm) {
    const errors: FieldErrors = {};

    if (!form.employeeCode.trim()) errors.employeeCode = "Employee code is required.";
    if (!form.firstName.trim()) errors.firstName = "First name is required.";
    if (!form.lastName.trim()) errors.lastName = "Last name is required.";
    if (!form.gender.trim()) errors.gender = "Gender is required.";

    if (!form.dateOfBirth) {
        errors.dateOfBirth = "Date of birth is required.";
    } else if (calculateAge(form.dateOfBirth) < 25) {
        errors.dateOfBirth = "Candidate must be at least 25 years old";
    }

    if (!form.contactNumber.trim()) errors.contactNumber = "Contact number is required.";
    else if (!contactPattern.test(form.contactNumber)) errors.contactNumber = "Contact number must be 10 digits";
    if (!form.email.trim()) errors.email = "Email is required.";
    else if (!emailPattern.test(form.email)) errors.email = "Invalid Email.";
    if (!form.address.trim()) errors.address = "Address is required.";
    if (!form.designation.trim()) errors.designation = "Designation is required.";
    if (!form.department.trim()) errors.department = "Department is required.";
    if (!form.dateOfJoining) errors.dateOfJoining = "Date of joining is required.";

    return errors;
}

function parseBackendErrors(error: unknown) {
    const errors: FieldErrors = {};
    let alertMessage = "Staff creation failed. Please check the form.";

    if (!axios.isAxiosError(error)) {
        return { errors, alertMessage };
    }

    if (!error.response) {
        return {
            errors,
            alertMessage: "Cannot reach staff service. Check that staff-service is running.",
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

export function AdminStaffCreatePage() {
    const [form, setForm] = useState<StaffCreateForm>(() => createInitialForm());
    const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
    const [alertMessage, setAlertMessage] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isCodeLoading, setIsCodeLoading] = useState(false);
    const maxDateOfBirth = useMemo(() => getMinimumDateOfBirth(), []);

    useEffect(() => {
        let isMounted = true;

        async function loadEmployeeCode() {
            setIsCodeLoading(true);

            try {
                const employeeCode = await getNextEmployeeCode();

                if (isMounted) {
                    setForm((current) => ({ ...current, employeeCode }));
                }
            } catch {
                if (isMounted) {
                    setAlertMessage("Could not generate the next employee code.");
                }
            } finally {
                if (isMounted) {
                    setIsCodeLoading(false);
                }
            }
        }

        void loadEmployeeCode();

        return () => {
            isMounted = false;
        };
    }, []);

    function updateField(event: ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) {
        const { name, value } = event.target;
        setForm((current) => ({
            ...current,
            [name]: value,
        }));
        setFieldErrors((current) => ({ ...current, [name]: "" }));
    }

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();
        setAlertMessage("");
        setSuccessMessage("");

        const validationErrors = validateStaff(form);

        if (Object.keys(validationErrors).length > 0) {
            setFieldErrors(validationErrors);
            setAlertMessage("Please fix the highlighted fields.");
            return;
        }

        setFieldErrors({});
        setIsSubmitting(true);

        try {
            await createStaff(form);
            setSuccessMessage("Staff saved successfully.");

            try {
                const employeeCode = await getNextEmployeeCode();
                setForm({ ...createInitialForm(), employeeCode });
            } catch {
                setForm(createInitialForm());
            }
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
                <p className="landing-kicker">Admin</p>
                <h1 className="landing-title">Staff Create</h1>
                <p className="landing-copy">Create staff profile and login credentials.</p>
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
                <TextField name="employeeCode" label="Employee Code" value={form.employeeCode} error={fieldErrors.employeeCode} onChange={updateField} readOnly />
                <TextField name="firstName" label="First Name" value={form.firstName} error={fieldErrors.firstName} onChange={updateField} />
                <TextField name="lastName" label="Last Name" value={form.lastName} error={fieldErrors.lastName} onChange={updateField} />
                <SelectField name="gender" label="Gender" value={form.gender} error={fieldErrors.gender} onChange={updateField} />
                <TextField name="dateOfBirth" label="Date of Birth" type="date" value={form.dateOfBirth} error={fieldErrors.dateOfBirth} onChange={updateField} max={maxDateOfBirth} />
                <TextField name="dateOfJoining" label="Date of Joining" type="date" value={form.dateOfJoining} error={fieldErrors.dateOfJoining} onChange={updateField} />
                <TextField name="contactNumber" label="Contact Number" value={form.contactNumber} error={fieldErrors.contactNumber} onChange={updateField} />
                <TextField name="email" label="Email" type="email" value={form.email} error={fieldErrors.email} onChange={updateField} />
                <TextField name="designation" label="Designation" value={form.designation} error={fieldErrors.designation} onChange={updateField} />
                <TextField name="department" label="Department" value={form.department} error={fieldErrors.department} onChange={updateField} />
                <TextAreaField name="address" label="Address" value={form.address} error={fieldErrors.address} onChange={updateField} />

                <div className="register-actions">
                    <button className="login-button" type="submit" disabled={isSubmitting || isCodeLoading}>
                        {isSubmitting ? "Submitting..." : isCodeLoading ? "Generating code..." : "Submit"}
                    </button>
                    <div className="register-action-links">
                        <Link className="public-button-link" to="/admin">Dashboard</Link>
                        <Link className="public-link" to="/admin/staff/list">Staff List</Link>
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
    max?: string;
    onChange: (event: ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => void;
};

function TextField({ name, label, value, error, type = "text", readOnly = false, max, onChange }: FieldProps) {
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
                max={max}
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
                <option value="M">Male</option>
                <option value="F">Female</option>
                <option value="Other">Other</option>
            </select>
            {error && <p className="field-error" id={`${name}-error`}>{error}</p>}
        </div>
    );
}
