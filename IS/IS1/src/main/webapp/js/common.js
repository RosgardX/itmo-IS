const API = 'api';

function typeImg(type) {
    return type ? `img/${type}.svg` : '';
}

function formatDate(s) {
    if (!s) return '';
    const d = new Date(String(s).replace(/\[.*\]$/, ''));
    return isNaN(d) ? s : d.toLocaleString();
}

function escapeHtml(s) {
    return String(s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

async function readErrors(res) {
    const body = await res.json().catch(() => ({}));
    return body.errors || [`Ошибка сервера (${res.status})`];
}

function showErrors(elementId, errors) {
    document.getElementById(elementId).innerHTML = errors.map(m => `<li>${escapeHtml(m)}</li>`).join('');
}

const MENU = [['index.html', 'Объекты'], ['coordinates.html', 'Координаты'], ['special.html', 'Спецоперации']];
const currentPage = location.pathname.split('/').pop() || 'index.html';
document.body.insertAdjacentHTML('afterbegin', '<nav>' + MENU.map(([href, title]) =>
    `<a href="${href}" class="${href === currentPage ? 'active' : ''}">${title}</a>`).join('') + '</nav>');

function onDataChanged(callback) {
    const proto = location.protocol === 'https:' ? 'wss' : 'ws';
    const ctx = location.pathname.substring(0, location.pathname.indexOf('/', 1));
    const ws = new WebSocket(`${proto}://${location.host}${ctx}/ws/vehicles`);
    ws.onmessage = () => callback();
    ws.onclose = () => setTimeout(() => onDataChanged(callback), 2000);
}

document.body.insertAdjacentHTML('beforeend', `
<dialog id="vehicleDialog">
    <form id="vehicleForm">
        <h2 id="dialogTitle">Новый объект</h2>
        <div class="form-grid">
            <label for="f-name">Название</label>
            <input id="f-name" name="name" required maxlength="255">

            <label for="f-type">Тип</label>
            <select id="f-type" name="type">
                <option value="CAR">Машина (CAR)</option>
                <option value="SUBMARINE">Подводная лодка (SUBMARINE)</option>
                <option value="BICYCLE">Велосипед (BICYCLE)</option>
                <option value="HOVERBOARD">Гироскутер (HOVERBOARD)</option>
            </select>

            <label for="f-enginePower">Мощность двигателя</label>
            <input id="f-enginePower" name="enginePower" type="number" min="1" step="1" required>

            <label for="f-numberOfWheels">Количество колёс</label>
            <input id="f-numberOfWheels" name="numberOfWheels" type="number" min="1" step="1" required>

            <label for="f-capacity">Вместимость</label>
            <input id="f-capacity" name="capacity" type="number" min="1" step="1" required>

            <label for="f-distanceTravelled">Пробег <small>(необязательно)</small></label>
            <input id="f-distanceTravelled" name="distanceTravelled" type="number" min="0.001" step="any">

            <label for="f-fuelConsumption">Расход топлива</label>
            <input id="f-fuelConsumption" name="fuelConsumption" type="number" min="1" step="1" required>

            <label for="f-fuelType">Тип топлива</label>
            <select id="f-fuelType" name="fuelType">
                <option value="ELECTRICITY">Электричество (ELECTRICITY)</option>
                <option value="DIESEL">Дизель (DIESEL)</option>
                <option value="ANTIMATTER">Антиматерия (ANTIMATTER)</option>
            </select>
        </div>
        <fieldset>
            <legend>Координаты</legend>
            <div class="form-grid">
                <label for="coordinatesSelect">Существующие</label>
                <select id="coordinatesSelect" name="coordinatesId"></select>

                <label for="f-x">X <small>(больше -415)</small></label>
                <input id="f-x" name="x" type="number" step="any">

                <label for="f-y">Y <small>(не больше 663)</small></label>
                <input id="f-y" name="y" type="number" step="any" max="663">
            </div>
        </fieldset>
        <ul id="formErrors" class="errors"></ul>
        <div class="actions">
            <button type="button" id="cancelBtn">Отмена</button>
            <button type="submit" class="primary">Сохранить</button>
        </div>
    </form>
</dialog>`);

let editingId = null;
let afterSave = null;

function toggleXY() {
    const form = document.getElementById('vehicleForm');
    const useExisting = form.elements.coordinatesId.value !== '';
    for (const name of ['x', 'y']) {
        form.elements[name].disabled = useExisting;
        form.elements[name].required = !useExisting;
    }
}

async function openDialog(id, onSaved) {
    editingId = id;
    afterSave = onSaved;
    const form = document.getElementById('vehicleForm');
    form.reset();
    document.getElementById('formErrors').innerHTML = '';
    document.getElementById('dialogTitle').textContent = id ? `Редактирование #${id}` : 'Новый объект';

    const coords = await (await fetch(`${API}/coordinates`)).json();
    document.getElementById('coordinatesSelect').innerHTML =
        '<option value="">— новые (ввести X и Y) —</option>' +
        coords.map(c => `<option value="${c.id}">#${c.id} (${c.x}; ${c.y})</option>`).join('');

    if (id) {
        const v = await (await fetch(`${API}/vehicles/${id}`)).json();
        for (const name of ['name', 'type', 'enginePower', 'numberOfWheels', 'capacity',
            'distanceTravelled', 'fuelConsumption', 'fuelType']) {
            form.elements[name].value = v[name] ?? '';
        }
        form.elements.coordinatesId.value = v.coordinates.id;
    }
    toggleXY();
    document.getElementById('vehicleDialog').showModal();
}

function formToVehicle(form) {
    const f = new FormData(form);
    const num = name => (f.get(name) ?? '') === '' ? undefined : Number(f.get(name));
    return {
        name: f.get('name'),
        type: f.get('type'),
        enginePower: num('enginePower'),
        numberOfWheels: num('numberOfWheels'),
        capacity: num('capacity'),
        distanceTravelled: num('distanceTravelled'),
        fuelConsumption: num('fuelConsumption'),
        fuelType: f.get('fuelType'),
        coordinates: f.get('coordinatesId') ? { id: Number(f.get('coordinatesId')) } : { x: num('x'), y: num('y') }
    };
}

document.getElementById('coordinatesSelect').onchange = toggleXY;

document.getElementById('vehicleForm').onsubmit = async e => {
    e.preventDefault();
    const body = JSON.stringify(formToVehicle(e.target));
    const headers = { 'Content-Type': 'application/json' };
    const res = editingId
        ? await fetch(`${API}/vehicles/${editingId}`, { method: 'PUT', headers, body })
        : await fetch(`${API}/vehicles`, { method: 'POST', headers, body });

    if (res.ok) {
        document.getElementById('vehicleDialog').close();
        if (afterSave) afterSave(await res.json());
    } else {
        showErrors('formErrors', await readErrors(res));
    }
};
document.getElementById('cancelBtn').onclick = () => document.getElementById('vehicleDialog').close();
