import axios from 'axios';
import authHeader from '../utils/authHeader';

const API_URL = 'http://localhost:8080/api/mentors';

const createOrUpdateProfile = async (profileData) => {
  const response = await axios.post(`${API_URL}/profile`, profileData, {
    headers: authHeader()
  });
  return response.data;
};

const getMyProfile = async () => {
  const response = await axios.get(`${API_URL}/profile`, {
    headers: authHeader()
  });
  return response.data;
};

const getMentorProfile = async (mentorId) => {
  const response = await axios.get(`${API_URL}/${mentorId}/profile`);
  return response.data;
};

const searchMentors = async (params = {}) => {
  const response = await axios.get(`${API_URL}/search`, {
    params
  });
  return response.data;
};

const mentorService = {
  createOrUpdateProfile,
  getMyProfile,
  getMentorProfile,
  searchMentors
};

export default mentorService;