const FUEL_TYPES = ['ELECTRICITY', 'DIESEL', 'ANTIMATTER'];
for (const id of ['op1-fuel', 'op3-fuel'])
    document.getElementById(id).innerHTML = FUEL_TYPES.map(f => `<option>${f}</option>`).join('');

const val = id => document.getElementById(id).value;

async function run(url, method, render) {
    const res = await fetch(`${API}/special/${url}`, { method });
    if (!res.ok) {
        showErrors('errors', await readErrors(res));
        document.getElementById('result').innerHTML = '';
        return;
    }
    showErrors('errors', []);
    document.getElementById('result').innerHTML = render(await res.json());
}

function vehiclesTable(list) {
    if (!list.length) return '<p>Ничего не найдено</p>';
    return `<p>Найдено: ${list.length}</p><table><thead><tr>
        <th>ID</th><th>Название</th><th>Тип</th><th>Мощность</th><th>Колёса</th><th>Топливо</th><th>Создана</th>
        </tr></thead><tbody>` + list.map(v => `<tr class="clickable" onclick="location.href='vehicle.html?id=${v.id}'">
        <td>${v.id}</td><td>${escapeHtml(v.name)}</td><td>${v.type}</td><td>${v.enginePower}</td>
        <td>${v.numberOfWheels}</td><td>${v.fuelType}</td><td>${formatDate(v.creationDate)}</td></tr>`).join('') +
        '</tbody></table>';
}

document.getElementById('op1').onclick = () => {
    if (!confirm(`Удалить все объекты с топливом ${val('op1-fuel')}?`)) return;
    run(`fuel-type/${val('op1-fuel')}`, 'DELETE', r => `<p>Удалено объектов: ${r.deleted}</p>`);
};
document.getElementById('op2').onclick = () => run('oldest', 'GET', v => vehiclesTable([v]));
document.getElementById('op3').onclick = () => run(`fuel-type-greater/${val('op3-fuel')}`, 'GET', vehiclesTable);
document.getElementById('op4').onclick = () => {
    if (val('op4-min') === '' || val('op4-max') === '') return showErrors('errors', ['Введите обе границы диапазона']);
    run(`engine-power?min=${val('op4-min')}&max=${val('op4-max')}`, 'GET', vehiclesTable);
};
document.getElementById('op5').onclick = () => {
    if (val('op5-id') === '' || val('op5-count') === '') return showErrors('errors', ['Введите id и число колёс']);
    run(`${val('op5-id')}/add-wheels?count=${val('op5-count')}`, 'POST',
        v => `<p>Теперь у #${v.id} «${escapeHtml(v.name)}» колёс: ${v.numberOfWheels}</p>`);
};
