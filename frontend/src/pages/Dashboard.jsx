import useAuth from '../auth/useAuth';
import StudentDashboard from '../components/StudentDashboard';
import MentorDashboard from '../components/MentorDashboard';

function Dashboard() {
  // ProtectedRoute guarantees a user here; logging out re-renders the route and redirects.
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex justify-between items-center">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">TaskMentor</h1>
            <p className="text-sm text-gray-600">
              {user.role === 'STUDENT' ? 'Student' : 'Mentor'} Dashboard
            </p>
          </div>
          <div className="flex items-center space-x-4">
            <span className="text-sm text-gray-700">{user.email}</span>
            <button
              onClick={logout}
              className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500"
            >
              Logout
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {user.role === 'STUDENT' ? <StudentDashboard /> : <MentorDashboard />}
      </main>
    </div>
  );
}

export default Dashboard;