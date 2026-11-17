import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import Navbar from './components/Navbar'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import Transactions from './pages/Transactions'
import Analytics from './pages/Analytics'

/**
 * Defines every route in the app.
 *  - /login and /register are public.
 *  - /, /transactions, /analytics require a logged-in user (ProtectedRoute).
 * The Navbar only shows when logged in.
 */
export default function App() {
  const { user } = useAuth()

  return (
    <>
      {user && <Navbar />}
      <div className="container">
        <Routes>
          {/* Public routes. If already logged in, skip to dashboard. */}
          <Route path="/login" element={user ? <Navigate to="/" /> : <Login />} />
          <Route path="/register" element={user ? <Navigate to="/" /> : <Register />} />

          {/* Protected routes */}
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/transactions"
            element={
              <ProtectedRoute>
                <Transactions />
              </ProtectedRoute>
            }
          />
          <Route
            path="/analytics"
            element={
              <ProtectedRoute>
                <Analytics />
              </ProtectedRoute>
            }
          />

          {/* Anything else -> dashboard (or login if not authed). */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </div>
    </>
  )
}
