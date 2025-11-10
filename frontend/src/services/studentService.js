import axios from 'axios';
import authHeader from '../utils/authHeader';

const API_URL = 'http://localhost:8080/api/students';

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

const getStudentProfile = async (studentId) => {
  const response = await axios.get(`${API_URL}/${studentId}/profile`, {
    headers: authHeader()
  });
  return response.data;
};

const studentService = {
  createOrUpdateProfile,
  getMyProfile,
  getStudentProfile
};

export default studentService;