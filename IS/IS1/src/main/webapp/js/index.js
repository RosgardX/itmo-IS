const state = { page: 0, size: 10, sort: 'id', asc: true, filterField: 'name', filterValue: '' };

async function loadTable() {
    const params = new URLSearchParams(state);
    const res = await fetch(`${API}/vehicles?${params}`);
    const data = await res.json();

    const pages = Math.max(1, Math.ceil(data.total / state.size));
    if (state.page >= pages) { state.page = pages - 1; return loadTable(); }

    const rows = document.getElementById('rows');
    rows.innerHTML = '';
    for (const v of data.items) {
        const tr = document.createElement('tr');
        tr.className = 'clickable';
        tr.innerHTML = `
            <td><img class="type-icon" src="${typeImg(v.type)}" alt=""></td>
            <td>${v.id}</td><td>${escapeHtml(v.name)}</td>
            <td>${v.coordinates.x}</td><td>${v.coordinates.y}</td>
            <td>${formatDate(v.creationDate)}</td>
            <td>${v.type ?? ''}</td><td>${v.enginePower}</td><td>${v.numberOfWheels}</td>
            <td>${v.capacity}</td><td>${v.distanceTravelled ?? ''}</td>
            <td>${v.fuelConsumption}</td><td>${v.fuelType ?? ''}</td>
            <td class="row-actions">
                <button data-edit="${v.id}" title="Редактировать">✎</button>
                <button data-delete="${v.id}" title="Удалить">✕</button>
            </td>`;
        rows.appendChild(tr);
    }

    document.getElementById('pageInfo').textContent = `${state.page + 1} / ${pages}`;
    document.getElementById('totalInfo').textContent = `всего: ${data.total}`;
    document.getElementById('prev').disabled = state.page === 0;
    document.getElementById('next').disabled = state.page + 1 >= pages;
    document.querySelectorAll('th[data-sort]').forEach(th =>
        th.dataset.dir = th.dataset.sort === state.sort ? (state.asc ? 'asc' : 'desc') : '');
}

async function deleteVehicle(id) {
    if (!confirm(`Удалить #${id}?`)) return;
    const res = await fetch(`${API}/vehicles/${id}`, { method: 'DELETE' });
    if (!res.ok) alert((await readErrors(res)).join('\n'));
    loadTable();
}

document.getElementById('createBtn').onclick = () => openDialog(null, loadTable);
document.getElementById('prev').onclick = () => { state.page--; loadTable(); };
document.getElementById('next').onclick = () => { state.page++; loadTable(); };
document.getElementById('filterField').onchange = e => { state.filterField = e.target.value; state.page = 0; loadTable(); };
document.getElementById('filterValue').oninput = e => { state.filterValue = e.target.value; state.page = 0; loadTable(); };
document.querySelectorAll('th[data-sort]').forEach(th => th.onclick = () => {
    state.asc = state.sort === th.dataset.sort ? !state.asc : true;
    state.sort = th.dataset.sort;
    loadTable();
});

document.getElementById('rows').onclick = e => {
    if (e.target.dataset.edit) return openDialog(Number(e.target.dataset.edit), loadTable);
    if (e.target.dataset.delete) return deleteVehicle(Number(e.target.dataset.delete));
    const tr = e.target.closest('tr');
    if (tr) location.href = `vehicle.html?id=${tr.children[1].textContent}`;
};

loadTable();
onDataChanged(loadTable);
