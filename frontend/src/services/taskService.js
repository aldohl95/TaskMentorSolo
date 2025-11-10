import axios from 'axios';
import authHeader from '../utils/authHeader';

const API_URL = 'http://localhost:8080/api/tasks';

const createTask = async (taskData) => {
  const response = await axios.post(API_URL, taskData, {
    headers: authHeader()
  });
  return response.data;
};

const updateTask = async (taskId, taskData) => {
  const response = await axios.put(`${API_URL}/${taskId}`, taskData, {
    headers: authHeader()
  });
  return response.data;
};

const deleteTask = async (taskId) => {
  const response = await axios.delete(`${API_URL}/${taskId}`, {
    headers: authHeader()
  });
  return response.data;
};

const getTask = async (taskId) => {
  const response = await axios.get(`${API_URL}/${taskId}`, {
    headers: authHeader()
  });
  return response.data;
};

const getTasksByMentor = async (mentorId) => {
  const response = await axios.get(`${API_URL}/mentor/${mentorId}`, {
    headers: authHeader()
  });
  return response.data;
};

const getTasksByCategory = async (category) => {
  const response = await axios.get(`${API_URL}/category/${category}`, {
    headers: authHeader()
  });
  return response.data;
};

const getAllTasks = async () => {
  const response = await axios.get(API_URL, {
    headers: authHeader()
  });
  return response.data;
};

const taskService = {
  createTask,
  updateTask,
  deleteTask,
  getTask,
  getTasksByMentor,
  getTasksByCategory,
  getAllTasks
};

export default taskService;