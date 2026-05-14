import client from './client'

export const userAPI = {
  getAll: () => client.get('/users'),
  getById: (id) => client.get(`/users/${id}`),
}
