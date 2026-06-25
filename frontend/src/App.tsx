import { Navigate, Route, Routes } from "react-router-dom";
import { LoginPage } from "./features/auth/LoginPage";
import { LandingPage } from "./features/landing/LandingPage";
import { RegisterPage } from "./features/register/RegisterPage";
import { getCurrentUser, getDashboardPath, type UserRole } from "./lib/auth";
import { RequestListPage } from "./features/requests/RequestListPage";
import { RequestCreatePage } from "./features/requests/RequestCreatePage";
import { NavigationBar } from "./components/navigation/NavigationBar";
import { AdminDashboard } from "./features/dashboard/AdminDashboard";
import { RoleDashboard } from "./features/dashboard/RoleDashboard";
import { AdminStaffListPage } from "./features/admin/AdminStaffListPage";
import { AdminStaffCreatePage } from "./features/admin/AdminStaffCreatePage";
import { AdminStudentListPage } from "./features/admin/AdminStudentListPage";
import { AdminCandidateListPage } from "./features/admin/AdminCandidateListPage";
import { AdminAccommodationRequestsPage } from "./features/admin/AdminAccommodationRequestsPage";
import { AdminHostelManagementPage } from "./features/admin/AdminHostelManagementPage";
import { AdminHostelListPage } from "./features/admin/AdminHostelListPage";
import { AdminRoomListPage } from "./features/admin/AdminRoomListPage";
import { AdminLogsPage } from "./features/admin/AdminLogsPage";
import { ChatPage } from "./features/chat/ChatPage";
import "./assets/css/AppLayout.css";
import type { ReactNode } from "react";

function ProtectedRoute({
                          allowedRole,
                          children,
                        }: {
  allowedRole: UserRole;
  children: ReactNode;
}) {
  const user = getCurrentUser();

  if (!user) return <Navigate to="/" replace />;
  if (user.role !== allowedRole) return <Navigate to={getDashboardPath(user.role)} replace />;

  return (
      <div className="app-shell">
        <NavigationBar />
        <main className="app-main">{children}</main>
      </div>
  );
}

function App() {
  return (
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login/:audience" element={<LoginPage />} />
        <Route path="/register/:type" element={<RegisterPage />} />

        <Route
            path="/admin"
            element={
              <ProtectedRoute allowedRole="ROLE_ADMIN">
                <AdminDashboard />
              </ProtectedRoute>
            }
        />

        <Route
            path="/admin/staff/create"
            element={
              <ProtectedRoute allowedRole="ROLE_ADMIN">
                <AdminStaffCreatePage />
              </ProtectedRoute>
            }
        />

        <Route
            path="/admin/staff/list"
            element={
              <ProtectedRoute allowedRole="ROLE_ADMIN">
                <AdminStaffListPage />
              </ProtectedRoute>
            }
        />

        <Route
            path="/admin/student/list"
            element={
              <ProtectedRoute allowedRole="ROLE_ADMIN">
                <AdminStudentListPage />
              </ProtectedRoute>
            }
        />

        <Route
            path="/admin/accommodation-requests"
            element={
              <ProtectedRoute allowedRole="ROLE_ADMIN">
                <AdminAccommodationRequestsPage />
              </ProtectedRoute>
            }
        />

        <Route
            path="/admin/hostels"
            element={
              <ProtectedRoute allowedRole="ROLE_ADMIN">
                <AdminHostelManagementPage />
              </ProtectedRoute>
            }
        />

        <Route
            path="/admin/hostels/list"
            element={
              <ProtectedRoute allowedRole="ROLE_ADMIN">
                <AdminHostelListPage />
              </ProtectedRoute>
            }
        />

        <Route
            path="/admin/rooms/list"
            element={
              <ProtectedRoute allowedRole="ROLE_ADMIN">
                <AdminRoomListPage />
              </ProtectedRoute>
            }
        />

        <Route
            path="/admin/candidates"
            element={
              <ProtectedRoute allowedRole="ROLE_ADMIN">
                <AdminCandidateListPage />
              </ProtectedRoute>
            }
        />

        <Route
            path="/admin/logs"
            element={
              <ProtectedRoute allowedRole="ROLE_ADMIN">
                <AdminLogsPage />
              </ProtectedRoute>
            }
        />

        <Route
            path="/admin/chat"
            element={
              <ProtectedRoute allowedRole="ROLE_ADMIN">
                <ChatPage />
              </ProtectedRoute>
            }
        />

        <Route
            path="/student"
            element={
              <ProtectedRoute allowedRole="ROLE_STUDENT">
                <RoleDashboard audience="student" />
              </ProtectedRoute>
            }
        />

        <Route
            path="/student/requests"
            element={
              <ProtectedRoute allowedRole="ROLE_STUDENT">
                <RequestListPage audience="student" />
              </ProtectedRoute>
            }
        />

        <Route
            path="/student/new-request"
            element={
              <ProtectedRoute allowedRole="ROLE_STUDENT">
                <RequestCreatePage audience="student" />
              </ProtectedRoute>
            }
        />

        <Route
            path="/student/chat"
            element={
              <ProtectedRoute allowedRole="ROLE_STUDENT">
                <ChatPage />
              </ProtectedRoute>
            }
        />

        <Route
            path="/staff"
            element={
              <ProtectedRoute allowedRole="ROLE_STAFF">
                <RoleDashboard audience="staff" />
              </ProtectedRoute>
            }
        />

        <Route
            path="/staff/requests"
            element={
              <ProtectedRoute allowedRole="ROLE_STAFF">
                <RequestListPage audience="staff" />
              </ProtectedRoute>
            }
        />

        <Route
            path="/staff/new-request"
            element={
              <ProtectedRoute allowedRole="ROLE_STAFF">
                <RequestCreatePage audience="staff" />
              </ProtectedRoute>
            }
        />

        <Route
            path="/staff/chat"
            element={
              <ProtectedRoute allowedRole="ROLE_STAFF">
                <ChatPage />
              </ProtectedRoute>
            }
        />

        <Route
            path="/candidate"
            element={
              <ProtectedRoute allowedRole="ROLE_CANDIDATE">
                <RoleDashboard audience="candidate" />
              </ProtectedRoute>
            }
        />

        <Route
            path="/candidate/requests"
            element={
              <ProtectedRoute allowedRole="ROLE_CANDIDATE">
                <RequestListPage audience="candidate" />
              </ProtectedRoute>
            }
        />

        <Route
            path="/candidate/new-request"
            element={
              <ProtectedRoute allowedRole="ROLE_CANDIDATE">
                <RequestCreatePage audience="candidate" />
              </ProtectedRoute>
            }
        />

        <Route
            path="/candidate/chat"
            element={
              <ProtectedRoute allowedRole="ROLE_CANDIDATE">
                <ChatPage />
              </ProtectedRoute>
            }
        />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
  );
}

export default App;
