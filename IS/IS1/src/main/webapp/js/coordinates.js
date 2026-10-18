async function loadCoordinates() {
    const list = await (await fetch(`${API}/coordinates/usage`)).json();
    const rows = document.getElementById('rows');
    rows.innerHTML = '';
    for (const c of list) {
        const options = list.filter(o => o.id !== c.id)
            .map(o => `<option value="${o.id}">#${o.id} (${o.x}; ${o.y})</option>`).join('');
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${c.id}</td><td>${c.x}</td><td>${c.y}</td><td>${c.vehicles}</td>
            <td>${c.vehicles > 0
            ? `<select id="target-${c.id}">${options || '<option value="">нет других координат</option>'}</select>`
            : '<span class="muted">не нужно</span>'}</td>
            <td><button class="danger" data-delete="${c.id}">Удалить</button></td>`;
        rows.appendChild(tr);
    }
}

document.getElementById('rows').onclick = async e => {
    const id = e.target.dataset.delete;
    if (!id) return;
    const select = document.getElementById(`target-${id}`);
    const query = select && select.value ? `?reassignTo=${select.value}` : '';
    if (!confirm(`Удалить координаты #${id}?` + (query ? ` Машины перейдут к #${select.value}.` : ''))) return;

    const res = await fetch(`${API}/coordinates/${id}${query}`, { method: 'DELETE' });
    showErrors('errors', res.ok ? [] : await readErrors(res));
    loadCoordinates();
};

loadCoordinates();
onDataChanged(loadCoordinates);
