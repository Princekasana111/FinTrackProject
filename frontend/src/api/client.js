import axios from 'axios'

/**
 * A single configured axios instance used by the whole app.
 *
 * Two interceptors do the heavy lifting:
 *  1. Request: attach the JWT (if we have one) to every outgoing call.
 *  2. Response: if the server says 401 (token invalid/expired), log the user
 *     out and bounce them to the login page.
 */
const api = axios.create({
  baseURL: '/api',
})

// 1. Attach the token from localStorage on every request.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 2. Handle expired/invalid tokens globally.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      // Avoid redirect loops if we're already on the login page.
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  },
)

export default api
