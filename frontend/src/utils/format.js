/**
 * Small formatting helpers shared across pages.
 */

// Format a number as Indian Rupees, e.g. 60000 -> "₹60,000.00".
export function formatCurrency(value) {
  const num = Number(value ?? 0)
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 2,
  }).format(num)
}

// Turn an API error into a readable message for the UI.
export function errorMessage(err, fallback = 'Something went wrong') {
  return (
    err?.response?.data?.message ||
    err?.response?.data?.fieldErrors &&
      Object.values(err.response.data.fieldErrors)[0] ||
    fallback
  )
}
