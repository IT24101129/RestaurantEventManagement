// Chef Dashboard JS - loads kitchen data and wires actions

document.addEventListener('DOMContentLoaded', () => {
  loadKitchenData();
  bindActionHandlers();
  // Auto-refresh every 30s
  setInterval(loadKitchenData, 30000);
});

async function loadKitchenData() {
  try {
    const res = await fetch('/kitchen/api/orders', { headers: { 'Accept': 'application/json' } });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const ct = res.headers.get('content-type') || '';
    if (!ct.includes('application/json')) {
      throw new Error('Unexpected content (are you logged in?)');
    }
    const data = await res.json();

    renderOrdersTable(data);
    updateStats(data);
  } catch (e) {
    console.error('Failed to load kitchen data:', e);
    showToast('Failed to load kitchen data', 'danger');
  }
}

function renderOrdersTable(data) {
  const tbody = document.querySelector('#dataTable tbody');
  if (!tbody) return;

  const allOrders = [];
  (data.pendingOrders || []).forEach(o => allOrders.push({ ...o, _statusLabel: 'PENDING', _statusClass: 'warning' }));
  (data.inPreparationOrders || []).forEach(o => allOrders.push({ ...o, _statusLabel: 'PREPARING', _statusClass: 'info' }));
  (data.readyOrders || []).forEach(o => allOrders.push({ ...o, _statusLabel: 'READY', _statusClass: 'success' }));

  if (allOrders.length === 0) {
    tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No active orders</td></tr>';
    return;
  }

  tbody.innerHTML = allOrders.map(o => {
    const id = o.id ?? '-';
    const tableName = o.table?.name ?? o.tableName ?? '-';
    const items = Array.isArray(o.items) ? o.items.map(i => i.itemName || i.name).join(', ') : (o.itemsSummary || '-');
    const priority = o.priority || 'Normal';
    const time = o.createdAt ? formatTime(o.createdAt) : '-';

    return `
      <tr data-order-id="${id}">
        <td>#${id}</td>
        <td>${tableName}</td>
        <td>${escapeHtml(items)}</td>
        <td><span class="badge bg-${priorityBadge(priority)}">${priority}</span></td>
        <td>${time}</td>
        <td><span class="badge bg-${o._statusClass}">${o._statusLabel}</span></td>
        <td>
          <button class="btn btn-sm btn-success js-complete" ${o._statusLabel === 'COMPLETED' ? 'disabled' : ''}>Complete</button>
          <button class="btn btn-sm btn-warning js-update">Update</button>
        </td>
      </tr>`;
  }).join('');
}

function updateStats(data) {
  const activeOrders = (data.pendingOrders?.length || 0) + (data.inPreparationOrders?.length || 0) + (data.readyOrders?.length || 0);
  const pendingTasks = (data.pendingTasksCount ?? data.pendingTasks?.length ?? 0);
  const lowStock = (data.lowStockItemsCount ?? data.lowStockItems?.length ?? 0);
  const completedToday = data.completedToday || 0; // optional field if provided

  setText('#chefActiveOrders', activeOrders);
  setText('#chefPendingTasks', pendingTasks);
  setText('#chefLowStock', lowStock);
  setText('#chefCompletedToday', completedToday);
}

function setText(selector, value) {
  const el = document.querySelector(selector);
  if (el) el.textContent = String(value);
}

function bindActionHandlers() {
  document.addEventListener('click', async (e) => {
    const btnComplete = e.target.closest('.js-complete');
    if (btnComplete) {
      const row = btnComplete.closest('tr');
      const id = row?.getAttribute('data-order-id');
      if (!id) return;
      await updateOrderStatus(id, 'COMPLETED');
      return;
    }

    const btnUpdate = e.target.closest('.js-update');
    if (btnUpdate) {
      const row = btnUpdate.closest('tr');
      const id = row?.getAttribute('data-order-id');
      if (!id) return;
      // For demo: toggle to IN_PREPARATION
      await updateOrderStatus(id, 'IN_PREPARATION');
      return;
    }
  });
}

async function updateOrderStatus(orderId, status) {
  try {
    const res = await fetch(`/kitchen/orders/${orderId}/status`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({ status })
    });
    const json = await res.json().catch(() => ({}));
    if (!res.ok || json.success === false) throw new Error(json.error || `HTTP ${res.status}`);
    showToast('Order updated', 'success');
    await loadKitchenData();
  } catch (e) {
    console.error('Failed to update order:', e);
    showToast('Failed to update order', 'danger');
  }
}

function showToast(msg, type = 'info') {
  // Simple fallback via alert classes injected at top of container
  const container = document.querySelector('.container-fluid') || document.body;
  const div = document.createElement('div');
  div.className = `alert alert-${type} alert-dismissible fade show`;
  div.role = 'alert';
  div.innerHTML = `${escapeHtml(msg)}<button type="button" class="btn-close" data-bs-dismiss="alert"></button>`;
  container.prepend(div);
  setTimeout(() => div.remove(), 4000);
}

function formatTime(iso) {
  try {
    const d = new Date(iso);
    return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  } catch { return '-'; }
}

function priorityBadge(p) {
  const v = String(p || '').toUpperCase();
  if (v.includes('URGENT')) return 'danger';
  if (v.includes('HIGH')) return 'warning';
  if (v.includes('LOW')) return 'secondary';
  return 'primary';
}

function escapeHtml(s) {
  return String(s ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}
