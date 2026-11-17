const id = Number(new URLSearchParams(location.search).get('id'));

async function loadCard() {
    const res = await fetch(`${API}/vehicles/${id}`);
    const card = document.getElementById('card');
    if (!res.ok) {
        card.innerHTML = `<p class="errors">Машина #${id} не найдена (возможно, её удалили)</p>`;
        return;
    }
    const v = await res.json();
    document.title = `${v.name} - IS1`;

    const row = (label, value) => `<dt>${label}</dt><dd>${value === null || value === undefined || value === '' ? '—' : escapeHtml(value)}</dd>`;

    card.innerHTML = `
        <img class="card-img" src="${typeImg(v.type)}" alt="${v.type ?? ''}">
        <div class="card-body">
            <h1>${escapeHtml(v.name)} <span class="muted">#${v.id}</span></h1>
            <dl>
                ${row('Тип', v.type)}
                ${row('Создана', formatDate(v.creationDate))}
                ${row('Мощность двигателя', v.enginePower)}
                ${row('Колёс', v.numberOfWheels)}
                ${row('Вместимость', v.capacity)}
                ${row('Пробег', v.distanceTravelled)}
                ${row('Расход топлива', v.fuelConsumption)}
                ${row('Топливо', v.fuelType)}
            </dl>
            <h2>Связанные координаты #${v.coordinates.id}</h2>
            <dl>
                ${row('X', v.coordinates.x)}
                ${row('Y', v.coordinates.y)}
            </dl>
            <div class="actions">
                <button id="editBtn" class="primary">Редактировать</button>
                <button id="deleteBtn" class="danger">Удалить</button>
            </div>
        </div>`;

    document.getElementById('editBtn').onclick = () => openDialog(id, loadCard);
    document.getElementById('deleteBtn').onclick = async () => {
        if (!confirm(`Удалить #${id}?`)) return;
        await fetch(`${API}/vehicles/${id}`, { method: 'DELETE' });
        location.href = 'index.html';
    };
}

loadCard();
onDataChanged(loadCard);
