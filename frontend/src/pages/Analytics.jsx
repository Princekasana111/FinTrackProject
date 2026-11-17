import { useEffect, useState } from 'react'
import {
  PieChart, Pie, Cell, Legend, Tooltip, ResponsiveContainer,
  LineChart, Line, XAxis, YAxis, CartesianGrid,
} from 'recharts'
import api from '../api/client'
import { formatCurrency } from '../utils/format'

// A pleasant, distinct colour per pie slice.
const COLORS = ['#4e79a7', '#f28e2b', '#e15759', '#76b7b2', '#59a14f', '#edc948', '#b07aa1', '#ff9da7']

/**
 * Analytics = a pie chart (expense by category) + a line chart (monthly trend).
 * Both come from the backend's /analytics endpoints.
 */
export default function Analytics() {
  const [byCategory, setByCategory] = useState([])
  const [trend, setTrend] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let active = true
    Promise.all([
      api.get('/analytics/by-category'),
      api.get('/analytics/monthly-trend'),
    ])
      .then(([catRes, trendRes]) => {
        if (!active) return
        setByCategory(catRes.data.map((d) => ({ name: d.category, value: Number(d.total) })))
        setTrend(trendRes.data.map((d) => ({
          month: d.month,
          income: Number(d.income),
          expense: Number(d.expense),
        })))
      })
      .finally(() => active && setLoading(false))
    return () => { active = false }
  }, [])

  if (loading) return <p className="muted">Loading analytics…</p>

  return (
    <div>
      <h2 className="page-title">Analytics</h2>

      <div className="chart-grid">
        {/* Pie: expense by category */}
        <div className="card">
          <h3 className="card-title">Spending by Category</h3>
          {byCategory.length === 0 ? (
            <p className="muted">No expenses recorded yet.</p>
          ) : (
            <ResponsiveContainer width="100%" height={320}>
              <PieChart>
                <Pie
                  data={byCategory}
                  dataKey="value"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  outerRadius={110}
                  label={(entry) => entry.name}
                >
                  {byCategory.map((entry, index) => (
                    <Cell key={index} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(v) => formatCurrency(v)} />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          )}
        </div>

        {/* Line: monthly income vs expense */}
        <div className="card">
          <h3 className="card-title">Monthly Trend</h3>
          {trend.length === 0 ? (
            <p className="muted">No data yet.</p>
          ) : (
            <ResponsiveContainer width="100%" height={320}>
              <LineChart data={trend} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#eee" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip formatter={(v) => formatCurrency(v)} />
                <Legend />
                <Line type="monotone" dataKey="income" name="Income" stroke="#2e9e6b" strokeWidth={2} />
                <Line type="monotone" dataKey="expense" name="Expense" stroke="#e5544b" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>
    </div>
  )
}
