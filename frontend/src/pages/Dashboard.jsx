import { useEffect, useState } from 'react'
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid, Legend,
} from 'recharts'
import api from '../api/client'
import StatCard from '../components/StatCard'
import { formatCurrency } from '../utils/format'

/**
 * Dashboard = headline cards + a recent income-vs-expense bar chart.
 * It fetches two endpoints in parallel when the page mounts.
 */
export default function Dashboard() {
  const [summary, setSummary] = useState(null)
  const [trend, setTrend] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let active = true
    Promise.all([
      api.get('/analytics/summary'),
      api.get('/analytics/monthly-trend'),
    ])
      .then(([summaryRes, trendRes]) => {
        if (!active) return
        setSummary(summaryRes.data)
        // Keep the last 6 months for a clean chart.
        setTrend(trendRes.data.slice(-6))
      })
      .finally(() => active && setLoading(false))
    return () => { active = false }
  }, [])

  if (loading) return <p className="muted">Loading dashboard…</p>

  return (
    <div>
      <h2 className="page-title">Dashboard</h2>

      <div className="stat-grid">
        <StatCard label="Total Income" value={summary.totalIncome} tone="income" />
        <StatCard label="Total Expense" value={summary.totalExpense} tone="expense" />
        <StatCard label="Balance" value={summary.balance} tone="balance" />
        <StatCard
          label="Transactions"
          value={summary.transactionCount}
          tone="neutral"
          isCurrency={false}
        />
      </div>

      <div className="card">
        <h3 className="card-title">Income vs Expense (last 6 months)</h3>
        {trend.length === 0 ? (
          <p className="muted">No data yet. Add some transactions to see the chart.</p>
        ) : (
          <ResponsiveContainer width="100%" height={320}>
            <BarChart data={trend} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#eee" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip formatter={(v) => formatCurrency(v)} />
              <Legend />
              <Bar dataKey="income" name="Income" fill="#2e9e6b" radius={[4, 4, 0, 0]} />
              <Bar dataKey="expense" name="Expense" fill="#e5544b" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        )}
      </div>
    </div>
  )
}
