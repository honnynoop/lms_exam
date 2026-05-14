import client from './client'

export const userExamAPI = {
  start: (examId) => client.post(`/user-exams/start/${examId}`),
  submit: (data) => client.post('/user-exams/submit', data),
  getResult: (userExamId) => client.get(`/user-exams/${userExamId}/result`),
  getMyExams: () => client.get('/user-exams/my-exams'),
}
