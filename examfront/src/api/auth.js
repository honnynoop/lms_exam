import client from './client'

export const authAPI = {
  login: (data) => client.post('/auth/login', data),
  register: (data) => client.post('/auth/register', data),
  getCurrentUser: () => client.get('/auth/me'),
  updateProfile: (data) => client.put('/auth/profile', data),  // ✅ NEW
}
