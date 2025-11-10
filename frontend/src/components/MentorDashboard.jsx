import { useState, useEffect } from 'react';
import mentorService from '../services/mentorService';
import taskService from '../services/taskService';
import bookingService from '../services/bookingService';

function MentorDashboard() {
  const [activeTab, setActiveTab] = useState('profile');
  const [profile, setProfile] = useState(null);
  const [profileForm, setProfileForm] = useState({
    firstName: '',
    lastName: '',
    bio: '',
    roleTitle: '',
    company: '',
    yrsExp: '',
    industries: '',
    expertise: '',
    photoUrl: ''
  });
  const [tasks, setTasks] = useState([]);
  const [taskForm, setTaskForm] = useState({
    title: '',
    description: '',
    duration: '',
    category: 'RESUME_REVIEW'
  });
  const [bookings, setBookings] = useState([]);
  const [editingTask, setEditingTask] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    loadProfile();
    loadTasks();
    loadBookings();
  }, []);

  const loadProfile = async () => {
    try {
      const data = await mentorService.getMyProfile();
      setProfile(data);
      setProfileForm({
        firstName: data.firstName || '',
        lastName: data.lastName || '',
        bio: data.bio || '',
        roleTitle: data.roleTitle || '',
        company: data.company || '',
        yrsExp: data.yrsExp || '',
        industries: data.industries || '',
        expertise: data.expertise || '',
        photoUrl: data.photoUrl || ''
      });
      setTasks(data.tasks || []);
    } catch (error) {
      console.log('Profile not created yet');
    }
  };

  const loadTasks = async () => {
    try {
      const data = await taskService.getAllTasks();
      setTasks(data);
    } catch (error) {
      console.error('Error loading tasks:', error);
    }
  };

  const loadBookings = async () => {
    try {
      const data = await bookingService.getMyBookingsAsMentor();
      setBookings(data);
    } catch (error) {
      console.error('Error loading bookings:', error);
    }
  };

  const handleProfileSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });
    try {
      await mentorService.createOrUpdateProfile(profileForm);
      setMessage({ type: 'success', text: 'Profile updated successfully!' });
      loadProfile();
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to update profile' });
    } finally {
      setLoading(false);
    }
  };

  const handleTaskSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });
    try {
      if (editingTask) {
        await taskService.updateTask(editingTask.taskId, {
          ...taskForm,
          duration: parseInt(taskForm.duration)
        });
        setMessage({ type: 'success', text: 'Task updated successfully!' });
      } else {
        await taskService.createTask({
          ...taskForm,
          duration: parseInt(taskForm.duration)
        });
        setMessage({ type: 'success', text: 'Task created successfully!' });
      }
      setTaskForm({ title: '', description: '', duration: '', category: 'RESUME_REVIEW' });
      setEditingTask(null);
      loadTasks();
      loadProfile();
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to save task' });
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteTask = async (taskId) => {
    if (!window.confirm('Are you sure you want to delete this task?')) return;
    try {
      await taskService.deleteTask(taskId);
      setMessage({ type: 'success', text: 'Task deleted successfully!' });
      loadTasks();
      loadProfile();
    } catch (error) {
      setMessage({ type: 'error', text: error.response?.data || 'Failed to delete task' });
    }
  };

  const handleEditTask = (task) => {
    setEditingTask(task);
    setTaskForm({
      title: task.title,
      description: task.description,
      duration: task.duration.toString(),
      category: task.category
    });
    setActiveTab('tasks');
  };

  const handleBookingResponse = async (bookingId, status, response) => {
    setLoading(true);
    try {
      await bookingService.updateBookingStatus(bookingId, {
        status,
        mentorResp: response
      });
      setMessage({ type: 'success', text: 'Booking updated successfully!' });
      loadBookings();
    } catch (error) {
      setMessage({ type: 'error', text: error.response?.data || 'Failed to update booking' });
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'ACCEPTED': return 'bg-green-100 text-green-800';
      case 'DECLINED': return 'bg-red-100 text-red-800';
      case 'COMPLETED': return 'bg-blue-100 text-blue-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div>
      {/* Tabs */}
      <div className="border-b border-gray-200 mb-6">
        <nav className="-mb-px flex space-x-8">
          <button
            onClick={() => setActiveTab('profile')}
            className={`${
              activeTab === 'profile'
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm`}
          >
            My Profile
          </button>
          <button
            onClick={() => setActiveTab('tasks')}
            className={`${
              activeTab === 'tasks'
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm`}
          >
            My Tasks
          </button>
          <button
            onClick={() => setActiveTab('bookings')}
            className={`${
              activeTab === 'bookings'
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm`}
          >
            Bookings
          </button>
        </nav>
      </div>

      {message.text && (
        <div className={`mb-4 p-4 rounded ${message.type === 'success' ? 'bg-green-50 text-green-800' : 'bg-red-50 text-red-800'}`}>
          {message.text}
        </div>
      )}

      {/* Profile Tab */}
      {activeTab === 'profile' && (
        <div className="bg-white shadow rounded-lg p-6">
          <h2 className="text-2xl font-bold mb-6">Mentor Profile</h2>
          <form onSubmit={handleProfileSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">First Name *</label>
                <input
                  type="text"
                  required
                  value={profileForm.firstName}
                  onChange={(e) => setProfileForm({ ...profileForm, firstName: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Last Name *</label>
                <input
                  type="text"
                  required
                  value={profileForm.lastName}
                  onChange={(e) => setProfileForm({ ...profileForm, lastName: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Bio</label>
              <textarea
                rows="3"
                value={profileForm.bio}
                onChange={(e) => setProfileForm({ ...profileForm, bio: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              />
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Role Title *</label>
                <input
                  type="text"
                  required
                  value={profileForm.roleTitle}
                  onChange={(e) => setProfileForm({ ...profileForm, roleTitle: e.target.value })}
                  placeholder="e.g., Senior Software Engineer"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Company *</label>
                <input
                  type="text"
                  required
                  value={profileForm.company}
                  onChange={(e) => setProfileForm({ ...profileForm, company: e.target.value })}
                  placeholder="e.g., Google"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Years of Experience</label>
                <input
                  type="text"
                  value={profileForm.yrsExp}
                  onChange={(e) => setProfileForm({ ...profileForm, yrsExp: e.target.value })}
                  placeholder="e.g., 5-7 years"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Industries</label>
                <input
                  type="text"
                  value={profileForm.industries}
                  onChange={(e) => setProfileForm({ ...profileForm, industries: e.target.value })}
                  placeholder="e.g., Tech, Finance"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Expertise</label>
              <input
                type="text"
                value={profileForm.expertise}
                onChange={(e) => setProfileForm({ ...profileForm, expertise: e.target.value })}
                placeholder="e.g., Backend Development, System Design"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Photo URL</label>
              <input
                type="url"
                value={profileForm.photoUrl}
                onChange={(e) => setProfileForm({ ...profileForm, photoUrl: e.target.value })}
                placeholder="https://example.com/photo.jpg"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              />
            </div>
            <button
              type="submit"
              disabled={loading}
              className="w-full py-2 px-4 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 disabled:opacity-50"
            >
              {loading ? 'Saving...' : 'Save Profile'}
            </button>
          </form>
        </div>
      )}

      {/* Tasks Tab */}
      {activeTab === 'tasks' && (
        <div>
          <div className="bg-white shadow rounded-lg p-6 mb-6">
            <h2 className="text-2xl font-bold mb-6">{editingTask ? 'Edit Task' : 'Create New Task'}</h2>
            <form onSubmit={handleTaskSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Title *</label>
                <input
                  type="text"
                  required
                  value={taskForm.title}
                  onChange={(e) => setTaskForm({ ...taskForm, title: e.target.value })}
                  placeholder="e.g., Resume Review Session"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Description *</label>
                <textarea
                  rows="3"
                  required
                  value={taskForm.description}
                  onChange={(e) => setTaskForm({ ...taskForm, description: e.target.value })}
                  placeholder="Describe what this session includes..."
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Duration (minutes) *</label>
                  <input
                    type="number"
                    required
                    value={taskForm.duration}
                    onChange={(e) => setTaskForm({ ...taskForm, duration: e.target.value })}
                    placeholder="e.g., 30"
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Category *</label>
                  <select
                    required
                    value={taskForm.category}
                    onChange={(e) => setTaskForm({ ...taskForm, category: e.target.value })}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  >
                    <option value="RESUME_REVIEW">Resume Review</option>
                    <option value="MOCK_INTERVIEW">Mock Interview</option>
                    <option value="CARRER_ADVICE">Career Advice</option>
                  </select>
                </div>
              </div>
              <div className="flex gap-2">
                {editingTask && (
                  <button
                    type="button"
                    onClick={() => {
                      setEditingTask(null);
                      setTaskForm({ title: '', description: '', duration: '', category: 'RESUME_REVIEW' });
                    }}
                    className="flex-1 py-2 px-4 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300"
                  >
                    Cancel
                  </button>
                )}
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 py-2 px-4 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 disabled:opacity-50"
                >
                  {loading ? 'Saving...' : editingTask ? 'Update Task' : 'Create Task'}
                </button>
              </div>
            </form>
          </div>

          <div className="bg-white shadow rounded-lg p-6">
            <h2 className="text-2xl font-bold mb-6">Your Tasks</h2>
            {tasks.length === 0 ? (
              <p className="text-gray-600">No tasks yet. Create one to get started!</p>
            ) : (
              <div className="space-y-4">
                {tasks.map((task) => (
                  <div key={task.taskId} className="border border-gray-200 rounded-lg p-4">
                    <div className="flex justify-between items-start">
                      <div className="flex-1">
                        <h3 className="font-bold text-lg">{task.title}</h3>
                        <p className="text-sm text-gray-600 mb-2">{task.description}</p>
                        <div className="flex gap-4 text-sm text-gray-700">
                          <span><span className="font-semibold">Duration:</span> {task.duration} min</span>
                          <span><span className="font-semibold">Category:</span> {task.category.replace('_', ' ')}</span>
                        </div>
                      </div>
                      <div className="flex gap-2 ml-4">
                        <button
                          onClick={() => handleEditTask(task)}
                          className="px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700 text-sm"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => handleDeleteTask(task.taskId)}
                          className="px-3 py-1 bg-red-600 text-white rounded hover:bg-red-700 text-sm"
                        >
                          Delete
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Bookings Tab */}
      {activeTab === 'bookings' && (
        <div className="bg-white shadow rounded-lg p-6">
          <h2 className="text-2xl font-bold mb-6">Booking Requests</h2>
          {bookings.length === 0 ? (
            <p className="text-gray-600">No bookings yet.</p>
          ) : (
            <div className="space-y-4">
              {bookings.map((booking) => (
                <div key={booking.bookingId} className="border border-gray-200 rounded-lg p-4">
                  <div className="flex justify-between items-start mb-3">
                    <div>
                      <h3 className="font-bold text-lg">
                        {booking.student.firstName} {booking.student.lastName}
                      </h3>
                      <p className="text-sm text-gray-600">{booking.task.title}</p>
                    </div>
                    <span className={`px-3 py-1 rounded-full text-xs font-semibold ${getStatusColor(booking.status)}`}>
                      {booking.status}
                    </span>
                  </div>
                  <div className="space-y-2 mb-3">
                    <p className="text-sm text-gray-700">
                      <span className="font-semibold">Date:</span> {new Date(booking.proposedDate).toLocaleString()}
                    </p>
                    <p className="text-sm text-gray-700">
                      <span className="font-semibold">Duration:</span> {booking.task.duration} minutes
                    </p>
                    {booking.student.major && (
                      <p className="text-sm text-gray-700">
                        <span className="font-semibold">Student Major:</span> {booking.student.major}
                      </p>
                    )}
                    {booking.studentMsg && (
                      <p className="text-sm text-gray-700">
                        <span className="font-semibold">Student message:</span> {booking.studentMsg}
                      </p>
                    )}
                    {booking.mentorResp && (
                      <p className="text-sm text-gray-700">
                        <span className="font-semibold">Your response:</span> {booking.mentorResp}
                      </p>
                    )}
                  </div>
                  {booking.status === 'PENDING' && (
                    <div className="flex gap-2">
                      <button
                        onClick={() => {
                          const response = prompt('Enter a message for the student (optional):');
                          if (response !== null) {
                            handleBookingResponse(booking.bookingId, 'ACCEPTED', response);
                          }
                        }}
                        className="flex-1 py-2 px-4 bg-green-600 text-white rounded-md hover:bg-green-700"
                      >
                        Accept
                      </button>
                      <button
                        onClick={() => {
                          const response = prompt('Enter a reason for declining (optional):');
                          if (response !== null) {
                            handleBookingResponse(booking.bookingId, 'DECLINED', response);
                          }
                        }}
                        className="flex-1 py-2 px-4 bg-red-600 text-white rounded-md hover:bg-red-700"
                      >
                        Decline
                      </button>
                    </div>
                  )}
                  {booking.status === 'ACCEPTED' && (
                    <button
                      onClick={() => handleBookingResponse(booking.bookingId, 'COMPLETED', booking.mentorResp || '')}
                      className="w-full py-2 px-4 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                    >
                      Mark as Completed
                    </button>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default MentorDashboard;