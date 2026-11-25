/* employee-service.js - Quy tắc nghiệp vụ: kiểm tra dữ liệu, CRUD, tìm kiếm/lọc/sắp xếp, thống kê. Không đụng DOM. */
const EmployeeService = (() => {
  const GENDERS = ['Nam', 'Nữ', 'Khác'];
  const STATUSES = ['Đang làm', 'Đã nghỉ'];
  const EMAIL_RE = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*\.[A-Za-z]{2,}$/;
  const PHONE_RE = /^(0|\+84)\d{9}$/;
  const CODE_RE = /^[A-Za-z0-9_-]{1,20}$/;

  let employees = Storage.loadEmployees();
  let departments = Storage.loadDepartments();
  let lastSaveOk = true;

  const persist = () => { lastSaveOk = Storage.saveEmployees(employees); return lastSaveOk; };
  const todayISO = () => new Date().toISOString().slice(0, 10);
  /** Bỏ dấu + hạ chữ thường để "nguyen" tìm được "Nguyễn". */
  const fold = (s) => String(s ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/đ/g, 'd').replace(/Đ/g, 'D').toLowerCase();

  function clean(e) {
    return {
      ma: String(e.ma ?? '').trim().toUpperCase(),
      hoTen: String(e.hoTen ?? '').trim().replace(/\s+/g, ' '),
      ngaySinh: String(e.ngaySinh ?? '').trim(),
      gioiTinh: String(e.gioiTinh ?? '').trim(),
      email: String(e.email ?? '').trim().toLowerCase(),
      sdt: String(e.sdt ?? '').replace(/\s+/g, ''),
      diaChi: String(e.diaChi ?? '').trim(),
      phongBan: String(e.phongBan ?? '').trim().toUpperCase(),
      chucVu: String(e.chucVu ?? '').trim(),
      luong: Number(e.luong),
      ngayVaoLam: String(e.ngayVaoLam ?? '').trim(),
      trangThai: String(e.trangThai ?? '').trim(),
    };
  }

  /** Trả về mảng thông báo lỗi (rỗng nếu hợp lệ). editingCode != null nghĩa là đang sửa nhân viên đó. */
  function validate(e, editingCode = null) {
    const errors = [];
    const today = todayISO();

    if (!e.ma) errors.push('Mã nhân viên là bắt buộc.');
    else if (!CODE_RE.test(e.ma)) errors.push("Mã nhân viên chỉ gồm chữ, số, '-' hoặc '_' và tối đa 20 ký tự.");
    else if (editingCode === null && employees.some((x) => x.ma === e.ma)) errors.push(`Mã nhân viên ${e.ma} đã tồn tại.`);

    if (!e.hoTen) errors.push('Họ tên là bắt buộc (không chỉ chứa khoảng trắng).');
    else if (e.hoTen.length > 100) errors.push('Họ tên tối đa 100 ký tự.');

    if (e.ngaySinh && e.ngaySinh > today) errors.push('Ngày sinh không được là ngày trong tương lai.');
    if (!GENDERS.includes(e.gioiTinh)) errors.push('Giới tính phải là Nam, Nữ hoặc Khác.');

    if (!e.email) errors.push('Email là bắt buộc.');
    else if (!EMAIL_RE.test(e.email)) errors.push('Email không đúng định dạng (ví dụ: ten@congty.vn).');
    else if (employees.some((x) => x.email === e.email && x.ma !== (editingCode ?? e.ma))) errors.push(`Email ${e.email} đã được nhân viên khác sử dụng.`);

    if (e.sdt && !PHONE_RE.test(e.sdt)) errors.push('Số điện thoại phải gồm 10 chữ số bắt đầu bằng 0 (hoặc +84 và 9 chữ số).');
    if (e.diaChi.length > 255) errors.push('Địa chỉ tối đa 255 ký tự.');

    if (!e.phongBan) errors.push('Phòng ban là bắt buộc.');
    else if (!departments.some((d) => d.ma === e.phongBan)) errors.push(`Phòng ban ${e.phongBan} không tồn tại.`);

    if (!e.chucVu) errors.push('Chức vụ là bắt buộc.');
    if (!Number.isFinite(e.luong) || e.luong <= 0) errors.push('Lương cơ bản phải là số lớn hơn 0.');

    if (!e.ngayVaoLam) errors.push('Ngày vào làm là bắt buộc.');
    else if (e.ngayVaoLam > today) errors.push('Ngày vào làm không được là ngày trong tương lai.');
    else if (e.ngaySinh && e.ngayVaoLam <= e.ngaySinh) errors.push('Ngày vào làm phải sau ngày sinh.');

    if (!STATUSES.includes(e.trangThai)) errors.push("Trạng thái phải là 'Đang làm' hoặc 'Đã nghỉ'.");
    return errors;
  }

  /** Mọi thao tác ghi trả về { ok, errors, saved } để giao diện hiển thị thống nhất. */
  function add(input) {
    const e = clean(input);
    const errors = validate(e);
    if (errors.length) return { ok: false, errors };
    employees.push(e);
    return { ok: true, errors: [], saved: persist() };
  }

  function update(code, input) {
    const idx = employees.findIndex((x) => x.ma === code);
    if (idx < 0) return { ok: false, errors: [`Không tìm thấy nhân viên có mã ${code}.`] };
    const e = clean({ ...input, ma: code }); // mã là định danh, không đổi khi sửa
    const errors = validate(e, code);
    if (errors.length) return { ok: false, errors };
    employees[idx] = e;
    return { ok: true, errors: [], saved: persist() };
  }

  function remove(code) {
    const before = employees.length;
    employees = employees.filter((x) => x.ma !== code);
    if (employees.length === before) return { ok: false, errors: [`Không tìm thấy nhân viên có mã ${code}.`] };
    return { ok: true, errors: [], saved: persist() };
  }

  const SORTERS = {
    ma: (a, b) => a.ma.localeCompare(b.ma),
    tenAsc: (a, b) => a.hoTen.localeCompare(b.hoTen, 'vi'),
    tenDesc: (a, b) => b.hoTen.localeCompare(a.hoTen, 'vi'),
    luongAsc: (a, b) => a.luong - b.luong,
    luongDesc: (a, b) => b.luong - a.luong,
  };

  /** Tìm theo mã/họ tên, lọc theo phòng ban/trạng thái, rồi sắp xếp. */
  function query({ keyword = '', dept = '', status = '', sort = 'ma' } = {}) {
    const kw = fold(keyword.trim());
    return employees
      .filter((e) => !kw || fold(e.ma).includes(kw) || fold(e.hoTen).includes(kw))
      .filter((e) => !dept || e.phongBan === dept)
      .filter((e) => !status || e.trangThai === status)
      .sort(SORTERS[sort] ?? SORTERS.ma);
  }

  const averageSalary = (list) => (list.length ? list.reduce((s, e) => s + e.luong, 0) / list.length : null);

  function countByDepartment() {
    return departments.map((d) => {
      const members = employees.filter((e) => e.phongBan === d.ma);
      return { ...d, soLuong: members.length, luongTB: averageSalary(members) };
    });
  }

  function countByStatus() {
    return STATUSES.map((s) => ({ trangThai: s, soLuong: employees.filter((e) => e.trangThai === s).length }));
  }

  /** Xuất / nhập JSON để sao lưu và chuyển đổi dữ liệu. Nhập vào sẽ được kiểm tra từng bản ghi. */
  const exportJSON = () => JSON.stringify({ departments, employees }, null, 2);

  function importJSON(text) {
    let data;
    try { data = JSON.parse(text); } catch { return { ok: false, errors: ['File không phải JSON hợp lệ.'] }; }
    if (!data || !Array.isArray(data.employees) || !Array.isArray(data.departments))
      return { ok: false, errors: ['JSON cần có hai mảng "departments" và "employees".'] };

    const prevDepts = departments, prevEmps = employees;
    departments = data.departments.map((d) => ({ ma: String(d.ma ?? '').toUpperCase(), ten: String(d.ten ?? '') }));
    employees = [];
    const errors = [];
    data.employees.forEach((raw, i) => {
      const e = clean(raw);
      const errs = validate(e);
      if (errs.length) errors.push(`Dòng ${i + 1} (${e.ma || 'không mã'}): ${errs.join(' ')}`);
      else employees.push(e);
    });
    if (errors.length) { departments = prevDepts; employees = prevEmps; return { ok: false, errors }; }
    lastSaveOk = Storage.saveDepartments(departments) && persist();
    return { ok: true, errors: [], saved: lastSaveOk };
  }

  function resetDemoData() {
    Storage.reset();
    employees = Storage.loadEmployees();
    departments = Storage.loadDepartments();
  }

  return {
    GENDERS, STATUSES,
    departments: () => departments,
    all: () => employees,
    find: (code) => employees.find((e) => e.ma === code),
    deptName: (code) => departments.find((d) => d.ma === code)?.ten ?? code,
    add, update, remove, query, averageSalary, countByDepartment, countByStatus,
    exportJSON, importJSON, resetDemoData,
  };
})();
