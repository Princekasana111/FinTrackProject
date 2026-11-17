import { createContext, useContext, useState } from 'react'
import api from '../api/client'

/**
 * AuthContext holds the current user + token and exposes login/register/logout.
 * Any component can read it via the useAuth() hook — no prop drilling.
 */
const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  // Initialise from localStorage so a page refresh keeps you logged in.
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user')
    return stored ? JSON.parse(stored) : null
  })

  // Persist auth data and update state after a successful login/register.
  const persist = (data) => {
    const u = {
      id: data.userId,
      name: data.name,
      email: data.email,
      role: data.role,
    }
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify(u))
    setUser(u)
  }

  const login = async (email, password) => {
    const { data } = await api.post('/auth/login', { email, password })
    persist(data)
  }

  const register = async (name, email, password) => {
    const { data } = await api.post('/auth/register', { name, email, password })
    persist(data)
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

// Custom hook so components just call useAuth().
export function useAuth() {
  return useContext(AuthContext)
}
