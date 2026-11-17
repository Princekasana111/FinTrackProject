import { useEffect, useState } from 'react'
import api from '../api/client'
import { formatCurrency, errorMessage } from '../utils/format'

/**
 * Full CRUD for transactions:
 *  - list them (newest first)
 *  - add one (with a category picker + inline "add category")
 *  - delete one
 * Every change re-fetches so the UI always reflects the server.
 */
export default function Transactions() {
  const [transactions, setTransactions] = useState([])
  const [categories, setCategories] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  const today = new Date().toISOString().slice(0, 10)
  const emptyForm = { amount: '', type: 'EXPENSE', date: today, categoryId: '', note: '' }
  const [form, setForm] = useState(emptyForm)
  const [newCategory, setNewCategory] = useState('')

  // Load transactions + categories together.
  const loadAll = async () => {
    try {
      const [txRes, catRes] = await Promise.all([
        api.get('/transactions'),
        api.get('/categories'),
      ])
      setTransactions(txRes.data)
      setCategories(catRes.data)
      // Default the category dropdown to the first available category.
      if (catRes.data.length && !form.categoryId) {
        setForm((f) => ({ ...f, categoryId: catRes.data[0].id }))
      }
    } catch (err) {
      setError(errorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadAll()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const update = (key) => (e) => setForm({ ...form, [key]: e.target.value })

  const addTransaction = async (e) => {
    e.preventDefault()
    setError('')
    try {
      await api.post('/transactions', {
        amount: parseFloat(form.amount),
        type: form.type,
        date: form.date,
        categoryId: Number(form.categoryId),
        note: form.note,
      })
      setForm({ ...emptyForm, categoryId: form.categoryId })
      await loadAll()
    } catch (err) {
      setError(errorMessage(err))
    }
  }

  const deleteTransaction = async (id) => {
    if (!confirm('Delete this transaction?')) return
    try {
      await api.delete(`/transactions/${id}`)
      await loadAll()
    } catch (err) {
      setError(errorMessage(err))
    }
  }

  const addCategory = async () => {
    if (!newCategory.trim()) return
    try {
      const { data } = await api.post('/categories', { name: newCategory.trim() })
      setNewCategory('')
      setCategories((c) => [...c, data])
      setForm((f) => ({ ...f, categoryId: data.id }))
    } catch (err) {
      setError(errorMessage(err))
    }
  }

  if (loading) return <p className="muted">Loading transactions…</p>

  return (
    <div>
      <h2 className="page-title">Transactions</h2>
      {error && <div className="alert alert-error">{error}</div>}

      {/* Add-transaction form */}
      <div className="card">
        <h3 className="card-title">Add a transaction</h3>
        <form className="tx-form" onSubmit={addTransaction}>
          <label className="field">
            <span>Amount</span>
            <input type="number" step="0.01" min="0.01" value={form.amount}
                   onChange={update('amount')} required />
          </label>

          <label className="field">
            <span>Type</span>
            <select value={form.type} onChange={update('type')}>
              <option value="EXPENSE">Expense</option>
              <option value="INCOME">Income</option>
            </select>
          </label>

          <label className="field">
            <span>Date</span>
            <input type="date" value={form.date} onChange={update('date')} required />
          </label>

          <label className="field">
            <span>Category</span>
            <select value={form.categoryId} onChange={update('categoryId')} required>
              {categories.length === 0 && <option value="">No categories</option>}
              {categories.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </label>

          <label className="field field-wide">
            <span>Note (optional)</span>
            <input type="text" value={form.note} onChange={update('note')} />
          </label>

          <button className="btn btn-primary" type="submit">Add</button>
        </form>

        {/* Inline add-category */}
        <div className="add-category">
          <input
            type="text"
            placeholder="New category name"
            value={newCategory}
            onChange={(e) => setNewCategory(e.target.value)}
          />
          <button className="btn btn-ghost" type="button" onClick={addCategory}>
            + Add category
          </button>
        </div>
      </div>

      {/* Transactions table */}
      <div className="card">
        <h3 className="card-title">History ({transactions.length})</h3>
        {transactions.length === 0 ? (
          <p className="muted">No transactions yet.</p>
        ) : (
          <div className="table-scroll">
            <table className="table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Category</th>
                  <th>Note</th>
                  <th>Type</th>
                  <th className="right">Amount</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {transactions.map((t) => (
                  <tr key={t.id}>
                    <td>{t.date}</td>
                    <td>{t.categoryName}</td>
                    <td className="muted">{t.note || '—'}</td>
                    <td>
                      <span className={`badge ${t.type === 'INCOME' ? 'badge-income' : 'badge-expense'}`}>
                        {t.type}
                      </span>
                    </td>
                    <td className={`right ${t.type === 'INCOME' ? 'text-income' : 'text-expense'}`}>
                      {t.type === 'INCOME' ? '+' : '−'}{formatCurrency(t.amount)}
                    </td>
                    <td className="right">
                      <button className="btn btn-danger btn-sm"
                              onClick={() => deleteTransaction(t.id)}>
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
