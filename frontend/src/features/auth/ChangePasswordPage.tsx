import { type FormEvent, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { changePassword } from "./authApi";
import { getCurrentUser, getDashboardPath, setPasswordChangeRequired } from "../../lib/auth";
import "../../assets/css/ChangePasswordPage.css";

type PasswordForm = {
    currentPassword: string;
    newPassword: string;
    confirmPassword: string;
};

type PasswordErrors = Partial<Record<keyof PasswordForm, string>>;

const initialForm: PasswordForm = {
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
};

function validatePasswordForm(form: PasswordForm) {
    const errors: PasswordErrors = {};

    if (!form.currentPassword.trim()) {
        errors.currentPassword = "Current password is required.";
    }

    if (!form.newPassword.trim()) {
        errors.newPassword = "New password is required.";
    } else if (form.newPassword.length < 8) {
        errors.newPassword = "Password must be at least 8 characters.";
    } else if (form.newPassword === form.currentPassword) {
        errors.newPassword = "New password must be different from current password.";
    }

    if (!form.confirmPassword.trim()) {
        errors.confirmPassword = "Confirm password is required.";
    } else if (form.confirmPassword !== form.newPassword) {
        errors.confirmPassword = "New password and confirm password do not match.";
    }

    return errors;
}

function getServerError(error: unknown) {
    if (!axios.isAxiosError(error)) {
        return "Password change failed. Please try again.";
    }

    const data = error.response?.data;

    if (typeof data === "string" && data.trim()) {
        return data;
    }

    if (data && typeof data === "object") {
        const response = data as { message?: string; error?: string; detail?: string };
        return response.message || response.error || response.detail || `Password change failed: ${error.response?.status}`;
    }

    if (!error.response) {
        return "Cannot reach auth service. Check that auth-service is running.";
    }

    return `Password change failed: ${error.response.status}`;
}

export function ChangePasswordPage() {
    const navigate = useNavigate();
    const user = getCurrentUser();
    const [form, setForm] = useState<PasswordForm>(initialForm);
    const [errors, setErrors] = useState<PasswordErrors>({});
    const [serverError, setServerError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    function updateField(field: keyof PasswordForm, value: string) {
        setForm((current) => ({ ...current, [field]: value }));
        setErrors((current) => ({ ...current, [field]: undefined }));
        setServerError("");
        setSuccessMessage("");
    }

    async function handleSubmit(event: FormEvent) {
        event.preventDefault();
        setServerError("");
        setSuccessMessage("");

        const validationErrors = validatePasswordForm(form);
        setErrors(validationErrors);

        if (Object.keys(validationErrors).length > 0 || !user) {
            return;
        }

        setIsSubmitting(true);

        try {
            await changePassword(form);
            setPasswordChangeRequired(false);
            setSuccessMessage("Password changed successfully.");
            navigate(getDashboardPath(user.role), { replace: true });
        } catch (error) {
            setServerError(getServerError(error));
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <section className="password-page" aria-labelledby="change-password-title">
            <div className="password-panel">
                <div className="password-header">
                    <p className="password-kicker">Account Security</p>
                    <h1 id="change-password-title" className="password-title">Change Password</h1>
                    <p className="password-copy">
                        Set a new password for {user?.sub}.
                    </p>
                </div>

                <form className="password-form" onSubmit={handleSubmit} noValidate>
                    <div className="password-field">
                        <label className="password-label" htmlFor="currentPassword">Current Password</label>
                        <input
                            id="currentPassword"
                            className="password-input"
                            type="password"
                            value={form.currentPassword}
                            onChange={(event) => updateField("currentPassword", event.target.value)}
                            autoComplete="current-password"
                            required
                        />
                        {errors.currentPassword && <p className="password-field-error">{errors.currentPassword}</p>}
                    </div>

                    <div className="password-field">
                        <label className="password-label" htmlFor="newPassword">New Password</label>
                        <input
                            id="newPassword"
                            className="password-input"
                            type="password"
                            value={form.newPassword}
                            onChange={(event) => updateField("newPassword", event.target.value)}
                            autoComplete="new-password"
                            required
                        />
                        {errors.newPassword && <p className="password-field-error">{errors.newPassword}</p>}
                    </div>

                    <div className="password-field">
                        <label className="password-label" htmlFor="confirmPassword">Confirm Password</label>
                        <input
                            id="confirmPassword"
                            className="password-input"
                            type="password"
                            value={form.confirmPassword}
                            onChange={(event) => updateField("confirmPassword", event.target.value)}
                            autoComplete="new-password"
                            required
                        />
                        {errors.confirmPassword && <p className="password-field-error">{errors.confirmPassword}</p>}
                    </div>

                    {serverError && (
                        <p className="password-alert password-alert-error" role="alert">
                            {serverError}
                        </p>
                    )}

                    {successMessage && (
                        <p className="password-alert password-alert-success" role="status">
                            {successMessage}
                        </p>
                    )}

                    <button className="password-button" type="submit" disabled={isSubmitting}>
                        {isSubmitting ? "Updating..." : "Update Password"}
                    </button>
                </form>
            </div>
        </section>
    );
}
