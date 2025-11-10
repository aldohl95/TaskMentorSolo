import axios from 'axios';
import authHeader from '../utils/authHeader';

const API_URL = 'http://localhost:8080/api/bookings';

const createBooking = async (bookingData) => {
  const response = await axios.post(API_URL, bookingData, {
    headers: authHeader()
  });
  return response.data;
};

const updateBookingStatus = async (bookingId, statusData) => {
  const response = await axios.put(`${API_URL}/${bookingId}`, statusData, {
    headers: authHeader()
  });
  return response.data;
};

const getBooking = async (bookingId) => {
  const response = await axios.get(`${API_URL}/${bookingId}`, {
    headers: authHeader()
  });
  return response.data;
};

const getMyBookingsAsStudent = async () => {
  const response = await axios.get(`${API_URL}/student`, {
    headers: authHeader()
  });
  return response.data;
};

const getMyBookingsAsMentor = async () => {
  const response = await axios.get(`${API_URL}/mentor`, {
    headers: authHeader()
  });
  return response.data;
};

const getPendingBookingsAsMentor = async () => {
  const response = await axios.get(`${API_URL}/mentor/pending`, {
    headers: authHeader()
  });
  return response.data;
};

const bookingService = {
  createBooking,
  updateBookingStatus,
  getBooking,
  getMyBookingsAsStudent,
  getMyBookingsAsMentor,
  getPendingBookingsAsMentor
};

export default bookingService;