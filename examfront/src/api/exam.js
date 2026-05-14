import client from './client'

export const examAPI = {
  getAll: (params) => client.get('/exams', { params }),
  getActive: () => client.get('/exams/active'),  // ✅ NEW: 공개된 시험만
  getById: (id) => client.get(`/exams/${id}`),
  getQuestions: (id) => client.get(`/exams/${id}/questions`),
  getStatistics: (id) => client.get(`/exams/${id}/statistics`),  // ✅ NEW
  create: (data) => client.post('/exams', data),
  activate: (id) => client.put(`/exams/${id}/activate`),  // ✅ NEW
  deactivate: (id) => client.put(`/exams/${id}/deactivate`),  // ✅ NEW
  publish: (id) => client.put(`/exams/${id}/publish`),
  delete: (id) => client.delete(`/exams/${id}`),
}
