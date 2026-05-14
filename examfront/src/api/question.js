import client from './client'

export const questionAPI = {
  getAll: (params) => client.get('/questions', { params }),
  getByCategoryId: (categoryId, params) => client.get(`/questions/category/${categoryId}`, { params }),
  getById: (id) => client.get(`/questions/${id}`),
  create: (data) => client.post('/questions', data),
  update: (id, data) => client.put(`/questions/${id}`, data),
  delete: (id) => client.delete(`/questions/${id}`),
}
