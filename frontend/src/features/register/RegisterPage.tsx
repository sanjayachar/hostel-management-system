import { type ChangeEvent, type FormEvent, useEffect, useMemo, useState } from "react";
import { Link, Navigate, useParams } from "react-router-dom";
import axios from "axios";
import {
    type CandidateRegistrationForm,
    getNextCandidateCode,
    getNextStudentAdmissionNumber,
    registerCandidate,
    registerStudent,
    type RegistrationType,
    type StudentRegistrationForm,
} from "./registerApi";
import { getCurrentUser, getDashboardPath } from "../../lib/auth";
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
    date.setFullYear(date.getFullYear() - 20);
    return formatDateForInput(date);
}

function createStudentInitialForm(): StudentRegistrationForm {
    return {
        admissionNumber: "",
        firstName: "",
        lastName: "",
        gender: "",
        dateOfBirth: getMinimumDateOfBirth(),
        contactNumber: "",
        personalEmail: "",
        fatherName: "",
        motherName: "",
        address: "",
        hostelStatus: false,
    };
}

function createCandidateInitialForm(): CandidateRegistrationForm {
    return {
        candidateCode: "",
        firstName: "",
        lastName: "",
        gender: "",
        dateOfBirth: getMinimumDateOfBirth(),
        email: "",
        contactNumber: "",
        address: "",
        city: "",
        state: "",
        pinCode: "",
        appliedPost: "",
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

function validateMinimumAge(value: string, label: string, errors: FieldErrors) {
    if (!value) {
        errors.dateOfBirth = `${label} date of birth is required.`;
        return;
    }

    const birthDate = new Date(value);

    if (Number.isNaN(birthDate.getTime()) || birthDate > new Date()) {
        errors.dateOfBirth = "Date of birth must be a valid past date.";
        return;
    }

    if (calculateAge(value) < 20) {
        errors.dateOfBirth = "Age must be at least 20 years.";
    }
}

function validateStudent(form: StudentRegistrationForm) {
    const errors: FieldErrors = {};

    if (!form.admissionNumber.trim()) errors.admissionNumber = "Student Admisssion number is required.";
    if (!form.firstName.trim()) errors.firstName = "Student first name is required.";
    if (!form.lastName.trim()) errors.lastName = "Student last name is required.";
    if (!form.gender.trim()) errors.gender = "Student gender is required.";
    validateMinimumAge(form.dateOfBirth, "Student", errors);
    if (!form.contactNumber.trim()) errors.contactNumber = "Student contact number is required.";
    else if (!contactPattern.test(form.contactNumber)) errors.contactNumber = "Contact number must be 10 digits.";
    if (!form.personalEmail.trim()) errors.personalEmail = "Student personal email is required.";
    else if (!emailPattern.test(form.personalEmail)) errors.personalEmail = "Invalid email format.";
    if (!form.address.trim()) errors.address = "Student address is required.";

    return errors;
}

function validateCandidate(form: CandidateRegistrationForm) {
    const errors: FieldErrors = {};

    if (!form.candidateCode.trim()) errors.candidateCode = "Candidate code is required.";
    if (!form.firstName.trim()) errors.firstName = "First name is required.";
    if (!form.lastName.trim()) errors.lastName = "Last name is required.";
    if (!form.gender.trim()) errors.gender = "Gender is required.";
    validateMinimumAge(form.dateOfBirth, "Candidate", errors);
    if (!form.email.trim()) errors.email = "Email is required.";
    else if (!emailPattern.test(form.email)) errors.email = "Invalid Email.";
    if (!form.contactNumber.trim()) errors.contactNumber = "Contact number is required.";
    else if (!contactPattern.test(form.contactNumber)) errors.contactNumber = "Contact number must be 10 digits.";
    if (!form.address.trim()) errors.address = "Address is required.";
    if (!form.appliedPost.trim()) errors.appliedPost = "Applied post is required.";

    return errors;
}

function parseBackendErrors(error: unknown) {
    const errors: FieldErrors = {};
    let alertMessage = "Registration failed. Please check the form.";

    if (!axios.isAxiosError(error)) {
        return { errors, alertMessage };
    }

    if (!error.response) {
        return {
            errors,
            alertMessage: "Cannot reach registration service. Check that the backend service is running.",
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

function getFormTitle(type: RegistrationType) {
    return type === "student" ? "Student Registration" : "Candidate Registration";
}

export function RegisterPage() {
    const { type } = useParams<{ type: RegistrationType }>();
    const user = getCurrentUser();
    const registrationType = type === "candidate" ? "candidate" : type === "student" ? "student" : null;
    const [studentForm, setStudentForm] = useState<StudentRegistrationForm>(() => createStudentInitialForm());
    const [candidateForm, setCandidateForm] = useState<CandidateRegistrationForm>(() => createCandidateInitialForm());
    const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
    const [alertMessage, setAlertMessage] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isCodeLoading, setIsCodeLoading] = useState(false);

    const title = useMemo(() => registrationType ? getFormTitle(registrationType) : "Registration", [registrationType]);
    const maxDateOfBirth = useMemo(() => getMinimumDateOfBirth(), []);

    useEffect(() => {
        if (!registrationType) return;

        let isMounted = true;

        async function loadGeneratedCode() {
            setIsCodeLoading(true);

            try {
                if (registrationType === "student") {
                    const admissionNumber = await getNextStudentAdmissionNumber();

                    if (isMounted) {
                        setStudentForm((current) => ({ ...current, admissionNumber }));
                    }
                } else {
                    const candidateCode = await getNextCandidateCode();

                    if (isMounted) {
                        setCandidateForm((current) => ({ ...current, candidateCode }));
                    }
                }
            } catch {
                if (isMounted) {
                    setAlertMessage("Could not generate the next registration code. Check the backend service.");
                }
            } finally {
                if (isMounted) {
                    setIsCodeLoading(false);
                }
            }
        }

        void loadGeneratedCode();

        return () => {
            isMounted = false;
        };
    }, [registrationType]);

    if (user) {
        return <Navigate to={getDashboardPath(user.role)} replace />;
    }

    if (!registrationType) {
        return <Navigate to="/" replace />;
    }

    const isStudent = registrationType === "student";
    const loginPath = isStudent ? "/login/student" : "/login/candidate";

    function updateStudentField(event: ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) {
        const { name, value } = event.target;
        setStudentForm((current) => ({
            ...current,
            [name]: value,
        }));
        setFieldErrors((current) => ({ ...current, [name]: "" }));
    }

    function updateCandidateField(event: ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) {
        const { name, value } = event.target;
        setCandidateForm((current) => ({
            ...current,
            [name]: value,
        }));
        setFieldErrors((current) => ({ ...current, [name]: "" }));
    }

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();
        setAlertMessage("");
        setSuccessMessage("");

        const validationErrors = isStudent ? validateStudent(studentForm) : validateCandidate(candidateForm);

        if (Object.keys(validationErrors).length > 0) {
            setFieldErrors(validationErrors);
            setAlertMessage("Please fix the highlighted fields.");
            return;
        }

        setFieldErrors({});
        setIsSubmitting(true);

        try {
            if (isStudent) {
                await registerStudent(studentForm);
            } else {
                await registerCandidate(candidateForm);
            }

            setSuccessMessage(`${isStudent ? "Student" : "Candidate"} registered successfully. You can login after credentials are issued.`);

            if (isStudent) {
                try {
                    const admissionNumber = await getNextStudentAdmissionNumber();
                    setStudentForm({ ...createStudentInitialForm(), admissionNumber });
                } catch {
                    setStudentForm(createStudentInitialForm());
                }
            } else {
                try {
                    const candidateCode = await getNextCandidateCode();
                    setCandidateForm({ ...createCandidateInitialForm(), candidateCode });
                } catch {
                    setCandidateForm(createCandidateInitialForm());
                }
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
        <main className="public-page">
            <section className="register-panel">
                <div className="public-header">
                    <p className="landing-kicker">Hostel Management System</p>
                    <h1 className="landing-title">{title}</h1>
                    <p className="landing-copy">Fill the required details to create your profile.</p>
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
                    {isStudent ? (
                        <>
                            <TextField name="admissionNumber" label="Admission Number" value={studentForm.admissionNumber} error={fieldErrors.admissionNumber} onChange={updateStudentField} readOnly />
                            <TextField name="firstName" label="First Name" value={studentForm.firstName} error={fieldErrors.firstName} onChange={updateStudentField} />
                            <TextField name="lastName" label="Last Name" value={studentForm.lastName} error={fieldErrors.lastName} onChange={updateStudentField} />
                            <SelectField name="gender" label="Gender" value={studentForm.gender} error={fieldErrors.gender} onChange={updateStudentField} />
                            <TextField name="dateOfBirth" label="Date of Birth" type="date" value={studentForm.dateOfBirth} error={fieldErrors.dateOfBirth} onChange={updateStudentField} max={maxDateOfBirth} />
                            <TextField name="contactNumber" label="Contact Number" value={studentForm.contactNumber} error={fieldErrors.contactNumber} onChange={updateStudentField} />
                            <TextField name="personalEmail" label="Personal Email" type="email" value={studentForm.personalEmail} error={fieldErrors.personalEmail} onChange={updateStudentField} />
                            <TextField name="fatherName" label="Father Name" value={studentForm.fatherName} error={fieldErrors.fatherName} onChange={updateStudentField} />
                            <TextField name="motherName" label="Mother Name" value={studentForm.motherName} error={fieldErrors.motherName} onChange={updateStudentField} />
                            <TextAreaField name="address" label="Address" value={studentForm.address} error={fieldErrors.address} onChange={updateStudentField} />
                        </>
                    ) : (
                        <>
                            <TextField name="candidateCode" label="Candidate Code" value={candidateForm.candidateCode} error={fieldErrors.candidateCode} onChange={updateCandidateField} readOnly />
                            <TextField name="firstName" label="First Name" value={candidateForm.firstName} error={fieldErrors.firstName} onChange={updateCandidateField} />
                            <TextField name="lastName" label="Last Name" value={candidateForm.lastName} error={fieldErrors.lastName} onChange={updateCandidateField} />
                            <SelectField name="gender" label="Gender" value={candidateForm.gender} error={fieldErrors.gender} onChange={updateCandidateField} />
                            <TextField name="dateOfBirth" label="Date of Birth" type="date" value={candidateForm.dateOfBirth} error={fieldErrors.dateOfBirth} onChange={updateCandidateField} max={maxDateOfBirth} />
                            <TextField name="email" label="Email" type="email" value={candidateForm.email} error={fieldErrors.email} onChange={updateCandidateField} />
                            <TextField name="contactNumber" label="Contact Number" value={candidateForm.contactNumber} error={fieldErrors.contactNumber} onChange={updateCandidateField} />
                            <TextField name="appliedPost" label="Applied Post" value={candidateForm.appliedPost} error={fieldErrors.appliedPost} onChange={updateCandidateField} />
                            <TextField name="city" label="City" value={candidateForm.city} error={fieldErrors.city} onChange={updateCandidateField} />
                            <TextField name="state" label="State" value={candidateForm.state} error={fieldErrors.state} onChange={updateCandidateField} />
                            <TextField name="pinCode" label="Pin Code" value={candidateForm.pinCode} error={fieldErrors.pinCode} onChange={updateCandidateField} />
                            <TextAreaField name="address" label="Address" value={candidateForm.address} error={fieldErrors.address} onChange={updateCandidateField} />
                        </>
                    )}

                    <div className="register-actions">
                        <button className="login-button" type="submit" disabled={isSubmitting || isCodeLoading}>
                            {isSubmitting ? "Submitting..." : isCodeLoading ? "Generating code..." : "Submit"}
                        </button>
                        <div className="register-action-links">
                            <Link className="public-button-link" to="/">Landing Page</Link>
                            <Link className="public-link" to={loginPath}>Back to login</Link>
                        </div>
                    </div>
                </form>
            </section>
        </main>
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
