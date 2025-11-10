import { useState, useEffect } from 'react';
import studentService from '../services/studentService';
import mentorService from '../services/mentorService';
import bookingService from '../services/bookingService';

function StudentDashboard() {
  const [activeTab, setActiveTab] = useState('profile');
  const [profile, setProfile] = useState(null);
  const [profileForm, setProfileForm] = useState({
    firstName: '',
    lastName: '',
    bio: '',
    major: '',
    gradYear: '',
    interests: ''
  });
  const [mentors, setMentors] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedMentor, setSelectedMentor] = useState(null);
  const [bookingForm, setBookingForm] = useState({
    taskId: '',
    proposedDate: '',
    studentMsg: ''
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  useEffect(() => {
    loadProfile();
    loadMentors();
    loadBookings();
  }, []);

  const loadProfile = async () => {
    try {
      const data = await studentService.getMyProfile();
      setProfile(data);
      setProfileForm({
        firstName: data.firstName || '',
        lastName: data.lastName || '',
        bio: data.bio || '',
        major: data.major || '',
        gradYear: data.gradYear || '',
        interests: data.interests || ''
      });
    } catch (error) {
      console.log('Profile not created yet');
    }
  };

  const loadMentors = async () => {
    try {
      const data = await mentorService.searchMentors();
      setMentors(data);
    } catch (error) {
      console.error('Error loading mentors:', error);
    }
  };

  const loadBookings = async () => {
    try {
      const data = await bookingService.getMyBookingsAsStudent();
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
      await studentService.createOrUpdateProfile(profileForm);
      setMessage({ type: 'success', text: 'Profile updated successfully!' });
      loadProfile();
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to update profile' });
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    try {
      const data = await mentorService.searchMentors({ keyword: searchKeyword });
      setMentors(data);
    } catch (error) {
      console.error('Error searching mentors:', error);
    }
  };

  const handleBookingSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });
    try {
      await bookingService.createBooking({
        mentorId: selectedMentor.mentorId,
        taskId: parseInt(bookingForm.taskId),
        proposedDate: bookingForm.proposedDate,
        studentMsg: bookingForm.studentMsg
      });
      setMessage({ type: 'success', text: 'Booking created successfully!' });
      setSelectedMentor(null);
      setBookingForm({ taskId: '', proposedDate: '', studentMsg: '' });
      loadBookings();
    } catch (error) {
      setMessage({ type: 'error', text: error.response?.data || 'Failed to create booking' });
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
            onClick={() => setActiveTab('mentors')}
            className={`${
              activeTab === 'mentors'
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm`}
          >
            Find Mentors
          </button>
          <button
            onClick={() => setActiveTab('bookings')}
            className={`${
              activeTab === 'bookings'
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm`}
          >
            My Bookings
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
          <h2 className="text-2xl font-bold mb-6">Student Profile</h2>
          <form onSubmit={handleProfileSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">First Name</label>
                <input
                  type="text"
                  required
                  value={profileForm.firstName}
                  onChange={(e) => setProfileForm({ ...profileForm, firstName: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Last Name</label>
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
                <label className="block text-sm font-medium text-gray-700 mb-1">Major</label>
                <input
                  type="text"
                  value={profileForm.major}
                  onChange={(e) => setProfileForm({ ...profileForm, major: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Graduation Year</label>
                <input
                  type="number"
                  value={profileForm.gradYear}
                  onChange={(e) => setProfileForm({ ...profileForm, gradYear: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Career Interests</label>
              <input
                type="text"
                value={profileForm.interests}
                onChange={(e) => setProfileForm({ ...profileForm, interests: e.target.value })}
                placeholder="e.g., Software Engineering, Data Science"
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

      {/* Find Mentors Tab */}
      {activeTab === 'mentors' && (
        <div>
          <div className="bg-white shadow rounded-lg p-6 mb-6">
            <div className="flex gap-2">
              <input
                type="text"
                value={searchKeyword}
                onChange={(e) => setSearchKeyword(e.target.value)}
                placeholder="Search by name, company, expertise..."
                className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              />
              <button
                onClick={handleSearch}
                className="px-6 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                Search
              </button>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {mentors.map((mentor) => (
              <div key={mentor.mentorId} className="bg-white shadow rounded-lg p-6">
                <div className="flex items-center mb-4">
                  {mentor.photoUrl ? (
                    <img src={mentor.photoUrl} alt={mentor.firstName} className="w-16 h-16 rounded-full mr-4" />
                  ) : (
                    <div className="w-16 h-16 rounded-full bg-indigo-100 flex items-center justify-center mr-4">
                      <span className="text-xl font-bold text-indigo-600">
                        {mentor.firstName?.[0]}{mentor.lastName?.[0]}
                      </span>
                    </div>
                  )}
                  <div>
                    <h3 className="font-bold text-lg">{mentor.firstName} {mentor.lastName}</h3>
                    <p className="text-sm text-gray-600">{mentor.roleTitle}</p>
                  </div>
                </div>
                <p className="text-sm text-gray-700 mb-2">{mentor.company}</p>
                <p className="text-sm text-gray-600 mb-4">{mentor.bio?.substring(0, 100)}...</p>
                {mentor.expertise && (
                  <p className="text-xs text-gray-500 mb-4">
                    <span className="font-semibold">Expertise:</span> {mentor.expertise}
                  </p>
                )}
                <button
                  onClick={() => setSelectedMentor(mentor)}
                  className="w-full py-2 px-4 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                >
                  Book Session
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* My Bookings Tab */}
      {activeTab === 'bookings' && (
        <div className="bg-white shadow rounded-lg p-6">
          <h2 className="text-2xl font-bold mb-6">My Bookings</h2>
          {bookings.length === 0 ? (
            <p className="text-gray-600">No bookings yet. Find a mentor to get started!</p>
          ) : (
            <div className="space-y-4">
              {bookings.map((booking) => (
                <div key={booking.bookingId} className="border border-gray-200 rounded-lg p-4">
                  <div className="flex justify-between items-start mb-2">
                    <div>
                      <h3 className="font-bold text-lg">
                        {booking.mentor.firstName} {booking.mentor.lastName}
                      </h3>
                      <p className="text-sm text-gray-600">{booking.task.title}</p>
                    </div>
                    <span className={`px-3 py-1 rounded-full text-xs font-semibold ${getStatusColor(booking.status)}`}>
                      {booking.status}
                    </span>
                  </div>
                  <p className="text-sm text-gray-700 mb-2">
                    <span className="font-semibold">Date:</span> {new Date(booking.proposedDate).toLocaleString()}
                  </p>
                  <p className="text-sm text-gray-700 mb-2">
                    <span className="font-semibold">Duration:</span> {booking.task.duration} minutes
                  </p>
                  {booking.studentMsg && (
                    <p className="text-sm text-gray-700 mb-2">
                      <span className="font-semibold">Your message:</span> {booking.studentMsg}
                    </p>
                  )}
                  {booking.mentorResp && (
                    <p className="text-sm text-gray-700">
                      <span className="font-semibold">Mentor response:</span> {booking.mentorResp}
                    </p>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Booking Modal */}
      {selectedMentor && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h2 className="text-2xl font-bold mb-4">Book Session with {selectedMentor.firstName}</h2>
            <form onSubmit={handleBookingSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Select Task</label>
                <select
                  required
                  value={bookingForm.taskId}
                  onChange={(e) => setBookingForm({ ...bookingForm, taskId: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                >
                  <option value="">Choose a task...</option>
                  {selectedMentor.tasks?.map((task) => (
                    <option key={task.taskId} value={task.taskId}>
                      {task.title} ({task.duration} min)
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Proposed Date & Time</label>
                <input
                  type="datetime-local"
                  required
                  value={bookingForm.proposedDate}
                  onChange={(e) => setBookingForm({ ...bookingForm, proposedDate: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Message (Optional)</label>
                <textarea
                  rows="3"
                  value={bookingForm.studentMsg}
                  onChange={(e) => setBookingForm({ ...bookingForm, studentMsg: e.target.value })}
                  placeholder="Tell the mentor about your goals..."
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
              <div className="flex gap-2">
                <button
                  type="button"
                  onClick={() => setSelectedMentor(null)}
                  className="flex-1 py-2 px-4 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 py-2 px-4 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 disabled:opacity-50"
                >
                  {loading ? 'Booking...' : 'Book'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default StudentDashboard;