import { formatCurrency } from '../utils/format'

/**
 * A single headline metric card (Income / Expense / Balance).
 * `tone` selects an accent colour via a CSS class.
 */
export default function StatCard({ label, value, tone = 'neutral', isCurrency = true }) {
  return (
    <div className={`stat-card tone-${tone}`}>
      <span className="stat-label">{label}</span>
      <span className="stat-value">
        {isCurrency ? formatCurrency(value) : value}
      </span>
    </div>
  )
}
