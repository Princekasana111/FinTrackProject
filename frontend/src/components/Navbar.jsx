import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

/**
 * Top navigation bar. NavLink automatically adds an "active" class to the
 * current page's link, which we style in index.css.
 */
export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <div className="brand">💰 FinTrack</div>
        <div className="nav-links">
          <NavLink to="/" end>Dashboard</NavLink>
          <NavLink to="/transactions">Transactions</NavLink>
          <NavLink to="/analytics">Analytics</NavLink>
        </div>
        <div className="nav-user">
          <span className="hello">Hi, {user?.name?.split(' ')[0]}</span>
          <button className="btn btn-ghost" onClick={handleLogout}>Logout</button>
        </div>
      </div>
    </nav>
  )
}
