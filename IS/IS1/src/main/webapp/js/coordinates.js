const state = {offset: 0, size: 20};

async function loadCoordinates() {
    const res = await fetch(`${API}/coordinates?${new URLSearchParams(state)}`);
    const data = await res.json();

    if (state.offset > 0 && state.offset >= data.total) {
        state.offset = Math.max(0, state.offset - state.size);
        return loadCoordinates();
    }

    const rows = document.getElementById('rows');
    rows.innerHTML = '';
    for (const c of data.items) {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${c.id}</td><td>${c.x}</td><td>${c.y}</td><td>${c.vehicles}</td>
            <td>${c.vehicles > 0
            ? `<input id="target-${c.id}" type="number" min="1" step="1" placeholder="ID координат">`
            : '<span class="muted">не нужно</span>'}</td>
            <td><button class="danger" data-delete="${c.id}">Удалить</button></td>`;
        rows.appendChild(tr);
    }

    const last = Math.min(state.offset + state.size, data.total);
    document.getElementById('pageInfo').textContent = data.total ? `${state.offset + 1}–${last}` : '0';
    document.getElementById('totalInfo').textContent = `всего: ${data.total}`;
    document.getElementById('prev').disabled = state.offset === 0;
    document.getElementById('next').disabled = last >= data.total;
}

document.getElementById('rows').onclick = async e => {
    const id = e.target.dataset.delete;
    if (!id) return;
    const target = document.getElementById(`target-${id}`);
    const query = target && target.value ? `?reassignTo=${target.value}` : '';
    if (!confirm(`Удалить координаты #${id}?` + (query ? ` Машины перейдут к #${target.value}.` : ''))) return;

    const res = await fetch(`${API}/coordinates/${id}${query}`, {method: 'DELETE'});
    showErrors('errors', res.ok ? [] : await readErrors(res));
    loadCoordinates();
};

document.getElementById('prev').onclick = () => {
    state.offset = Math.max(0, state.offset - state.size);
    loadCoordinates();
};
document.getElementById('next').onclick = () => {
    state.offset += state.size;
    loadCoordinates();
};

loadCoordinates();
onDataChanged(loadCoordinates);
