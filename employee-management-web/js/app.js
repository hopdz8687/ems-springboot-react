/* app.js - Giao diện: gắn sự kiện, vẽ lại DOM, gọi EmployeeService. Không chứa quy tắc nghiệp vụ. */
(() => {
  const $ = (sel) => document.querySelector(sel);
  const moneyFmt = new Intl.NumberFormat('vi-VN');
  const money = (n) => (n == null ? '–' : `${moneyFmt.format(Math.round(n))} ₫`);
  const dateVN = (iso) => (iso ? iso.split('-').reverse().join('/') : '–');

  /** Tạo phần tử bằng textContent (không dùng innerHTML) nên dữ liệu người dùng nhập không thể chèn mã HTML/JS. */
  function el(tag, props = {}, ...children) {
    const node = document.createElement(tag);
    Object.entries(props).forEach(([k, v]) => {
      if (k === 'class') node.className = v;
      else if (k === 'text') node.textContent = v;
      else if (k.startsWith('on')) node.addEventListener(k.slice(2), v);
      else node.setAttribute(k, v);
    });
    children.flat().forEach((c) => c != null && node.append(c));
    return node;
  }

  const filters = { keyword: '', dept: '', status: '', sort: 'ma' };
  let editingCode = null; // null = đang thêm mới

  // ---------- Vẽ giao diện ----------
  function renderSummary() {
    const all = EmployeeService.all();
    $('#sumEmployees').textContent = all.length;
    $('#sumDepartments').textContent = EmployeeService.departments().length;
    $('#sumSalary').textContent = money(EmployeeService.averageSalary(all));
  }

  function renderTable() {
    const list = EmployeeService.query(filters);
    const tbody = $('#tbody');
    tbody.replaceChildren(...list.map((e) => el('tr', {},
      el('td', { class: 'code', text: e.ma }),
      el('td', { class: 'name' }, el('strong', { text: e.hoTen }), el('small', { text: e.email })),
      el('td', { text: EmployeeService.deptName(e.phongBan) }),
      el('td', { text: e.chucVu }),
      el('td', { class: 'num', text: money(e.luong) }),
      el('td', {}, el('span', { class: `badge ${e.trangThai === 'Đang làm' ? 'on' : 'off'}`, text: e.trangThai })),
      el('td', { class: 'row-actions' },
        el('button', { type: 'button', class: 'btn sm', 'aria-label': `Sửa ${e.hoTen}`, text: 'Sửa', onclick: () => openForm(e.ma) }),
        el('button', { type: 'button', class: 'btn sm danger-ghost', 'aria-label': `Xóa ${e.hoTen}`, text: 'Xóa', onclick: () => askDelete(e) })),
    )));
    $('#empty').hidden = list.length > 0;
    $('.table-wrap').hidden = list.length === 0;
    $('#listFoot').textContent = list.length
      ? `Hiển thị ${list.length}/${EmployeeService.all().length} nhân viên · Lương trung bình của danh sách này: ${money(EmployeeService.averageSalary(list))}`
      : '';
  }

  function barItem(label, count, max, extra) {
    return el('li', {},
      el('div', { class: 'row' }, el('span', { text: label }), el('b', { text: count })),
      el('div', { class: 'bar', 'aria-hidden': 'true' }, el('i', { style: `width:${max ? (count / max) * 100 : 0}%` })),
      extra ? el('small', { text: extra }) : null);
  }

  function renderStats() {
    const byDept = EmployeeService.countByDepartment();
    const max = Math.max(1, ...byDept.map((d) => d.soLuong));
    $('#deptBars').replaceChildren(...byDept.map((d) =>
      barItem(d.ten, d.soLuong, max, d.luongTB == null ? 'Chưa có nhân viên' : `Lương TB ${money(d.luongTB)}`)));
    const byStatus = EmployeeService.countByStatus();
    const maxS = Math.max(1, ...byStatus.map((s) => s.soLuong));
    $('#statusBars').replaceChildren(...byStatus.map((s) => barItem(s.trangThai, s.soLuong, maxS)));
  }

  function renderDeptOptions() {
    const depts = EmployeeService.departments();
    const opts = (withAll) => [
      ...(withAll ? [el('option', { value: '', text: 'Tất cả' })] : []),
      ...depts.map((d) => el('option', { value: d.ma, text: d.ten })),
    ];
    $('#fDept').replaceChildren(...opts(true));
    $('#fDept').value = filters.dept;
    $('#empForm').elements.phongBan.replaceChildren(...opts(false));
  }

  const renderAll = () => { renderSummary(); renderTable(); renderStats(); };

  // ---------- Thông báo ----------
  let toastTimer;
  function toast(message, warn = false) {
    const t = $('#toast');
    t.textContent = message;
    t.classList.toggle('warn', warn);
    t.classList.add('show');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => t.classList.remove('show'), warn ? 6000 : 2800);
  }

  function afterWrite(result, okMessage) {
    if (!result.saved) toast('Đã cập nhật nhưng không lưu được vào Local Storage (trình duyệt đang chặn hoặc hết dung lượng).', true);
    else toast(okMessage);
  }

  // ---------- Form thêm / sửa ----------
  const form = $('#empForm');
  const formDialog = $('#formDialog');

  function showErrors(errors) {
    const box = $('#formErrors');
    if (!errors.length) { box.hidden = true; box.replaceChildren(); return; }
    box.replaceChildren(el('strong', { text: 'Chưa lưu được. Vui lòng sửa các lỗi sau:' }),
      el('ul', {}, errors.map((e) => el('li', { text: e }))));
    box.hidden = false;
    box.scrollIntoView({ block: 'nearest' });
  }

  function openForm(code = null) {
    editingCode = code;
    form.reset();
    showErrors([]);
    $('#formTitle').textContent = code ? `Sửa nhân viên ${code}` : 'Thêm nhân viên';
    form.elements.ma.disabled = Boolean(code); // mã là định danh nên không cho đổi
    if (code) {
      const e = EmployeeService.find(code);
      Object.keys(e).forEach((k) => { if (form.elements[k]) form.elements[k].value = e[k] ?? ''; });
    } else {
      form.elements.ngayVaoLam.value = new Date().toISOString().slice(0, 10);
    }
    form.elements.ngaySinh.max = form.elements.ngayVaoLam.max = new Date().toISOString().slice(0, 10);
    formDialog.showModal();
    (code ? form.elements.hoTen : form.elements.ma).focus();
  }

  form.addEventListener('submit', (ev) => {
    ev.preventDefault();
    const data = Object.fromEntries(new FormData(form).entries());
    if (editingCode) data.ma = editingCode; // field disabled không có trong FormData
    const result = editingCode ? EmployeeService.update(editingCode, data) : EmployeeService.add(data);
    if (!result.ok) { showErrors(result.errors); return; }
    formDialog.close();
    afterWrite(result, editingCode ? 'Đã cập nhật nhân viên.' : 'Đã thêm nhân viên mới.');
    renderAll();
  });
  $('#btnCancel').addEventListener('click', () => formDialog.close());
  $('#btnAdd').addEventListener('click', () => openForm());

  // ---------- Xóa có xác nhận ----------
  const confirmDialog = $('#confirmDialog');
  let pendingDelete = null;
  function askDelete(e) {
    pendingDelete = e.ma;
    $('#confirmTitle').textContent = `Xóa ${e.hoTen}?`;
    $('#confirmText').textContent = `Nhân viên ${e.ma} sẽ bị xóa vĩnh viễn khỏi trình duyệt này. Hành động không thể hoàn tác.`;
    confirmDialog.showModal();
  }
  confirmDialog.addEventListener('close', () => {
    if (confirmDialog.returnValue === 'ok' && pendingDelete) {
      const result = EmployeeService.remove(pendingDelete);
      result.ok ? afterWrite(result, 'Đã xóa nhân viên.') : toast(result.errors[0], true);
      renderAll();
    }
    pendingDelete = null;
    confirmDialog.returnValue = '';
  });

  // ---------- Tìm kiếm / lọc / sắp xếp ----------
  $('#fKeyword').addEventListener('input', (e) => { filters.keyword = e.target.value; renderTable(); });
  $('#fDept').addEventListener('change', (e) => { filters.dept = e.target.value; renderTable(); });
  $('#fStatus').addEventListener('change', (e) => { filters.status = e.target.value; renderTable(); });
  $('#fSort').addEventListener('change', (e) => { filters.sort = e.target.value; renderTable(); });

  // ---------- Xuất / nhập JSON, khôi phục ----------
  $('#btnExport').addEventListener('click', () => {
    const blob = new Blob([EmployeeService.exportJSON()], { type: 'application/json' });
    const a = el('a', { href: URL.createObjectURL(blob), download: `ems-${new Date().toISOString().slice(0, 10)}.json` });
    document.body.append(a); a.click(); a.remove(); URL.revokeObjectURL(a.href);
    toast('Đã xuất file JSON.');
  });
  $('#btnImport').addEventListener('click', () => $('#fileImport').click());
  $('#fileImport').addEventListener('change', async (e) => {
    const file = e.target.files[0];
    e.target.value = '';
    if (!file) return;
    const result = EmployeeService.importJSON(await file.text());
    if (!result.ok) { toast(`Không nhập được: ${result.errors[0]}`, true); return; }
    renderDeptOptions(); renderAll();
    afterWrite(result, 'Đã nhập dữ liệu từ file JSON.');
  });
  $('#btnReset').addEventListener('click', () => {
    if (!window.confirm('Xóa toàn bộ dữ liệu hiện tại và khôi phục 8 nhân viên mẫu?')) return;
    EmployeeService.resetDemoData();
    renderDeptOptions(); renderAll();
    toast('Đã khôi phục dữ liệu mẫu.');
  });

  renderDeptOptions();
  renderAll();
})();
