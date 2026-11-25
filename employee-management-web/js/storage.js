/* storage.js - Lớp duy nhất làm việc trực tiếp với Local Storage. Dữ liệu được lưu dạng chuỗi JSON. */
const Storage = (() => {
  const KEY_EMPLOYEES = 'ems.employees.v1';
  const KEY_DEPARTMENTS = 'ems.departments.v1';

  const SEED_DEPARTMENTS = [
    { ma: 'IT', ten: 'Công nghệ thông tin' },
    { ma: 'NS', ten: 'Nhân sự' },
    { ma: 'KT', ten: 'Kế toán' },
    { ma: 'KD', ten: 'Kinh doanh' },
  ];

  const SEED_EMPLOYEES = [
    ['NV001', 'Nguyễn Văn An', '1995-03-12', 'Nam', 'an.nguyen@company.vn', '0901234567', 'Nam Định', 'IT', 'Lập trình viên', 18000000, '2021-06-01', 'Đang làm'],
    ['NV002', 'Trần Thị Bình', '1993-11-02', 'Nữ', 'binh.tran@company.vn', '0912345678', 'Hà Nội', 'NS', 'Chuyên viên nhân sự', 14000000, '2020-02-15', 'Đang làm'],
    ['NV003', 'Lê Minh Châu', '1990-07-25', 'Nữ', 'chau.le@company.vn', '0923456789', 'Ninh Bình', 'KT', 'Kế toán trưởng', 25000000, '2018-09-10', 'Đang làm'],
    ['NV004', 'Phạm Quốc Dũng', '1998-01-30', 'Nam', 'dung.pham@company.vn', '0934567890', 'Hà Nội', 'KD', 'Nhân viên kinh doanh', 12000000, '2023-03-20', 'Đang làm'],
    ['NV005', 'Hoàng Thu Hà', '1996-05-18', 'Nữ', 'ha.hoang@company.vn', '0945678901', 'Hải Phòng', 'IT', 'Kiểm thử phần mềm', 15000000, '2022-08-08', 'Đang làm'],
    ['NV006', 'Vũ Đức Huy', '1989-12-09', 'Nam', 'huy.vu@company.vn', '0956789012', 'Nam Định', 'IT', 'Trưởng nhóm kỹ thuật', 32000000, '2017-04-03', 'Đang làm'],
    ['NV007', 'Đặng Mai Lan', '1994-09-14', 'Nữ', 'lan.dang@company.vn', '0967890123', 'Hà Nội', 'KD', 'Trưởng phòng kinh doanh', 28000000, '2019-01-21', 'Đã nghỉ'],
    ['NV008', 'Bùi Anh Tuấn', '2000-02-27', 'Nam', 'tuan.bui@company.vn', '0978901234', 'Thái Bình', 'KT', 'Kế toán viên', 11000000, '2024-05-06', 'Đang làm'],
  ].map(([ma, hoTen, ngaySinh, gioiTinh, email, sdt, diaChi, phongBan, chucVu, luong, ngayVaoLam, trangThai]) =>
    ({ ma, hoTen, ngaySinh, gioiTinh, email, sdt, diaChi, phongBan, chucVu, luong, ngayVaoLam, trangThai }));

  /** Đọc và parse JSON; trả về null nếu chưa có, dữ liệu hỏng hoặc trình duyệt chặn Local Storage. */
  function read(key) {
    try {
      const raw = localStorage.getItem(key);
      return raw ? JSON.parse(raw) : null;
    } catch (err) {
      console.warn('Không đọc được Local Storage:', err);
      return null;
    }
  }

  /** Trả về true nếu ghi thành công (false khi hết dung lượng hoặc chế độ riêng tư chặn ghi). */
  function write(key, value) {
    try {
      localStorage.setItem(key, JSON.stringify(value));
      return true;
    } catch (err) {
      console.warn('Không ghi được Local Storage:', err);
      return false;
    }
  }

  function loadEmployees() {
    const data = read(KEY_EMPLOYEES);
    if (Array.isArray(data)) return data;
    write(KEY_EMPLOYEES, SEED_EMPLOYEES); // lần chạy đầu tiên: nạp dữ liệu mẫu
    return structuredClone(SEED_EMPLOYEES);
  }

  function loadDepartments() {
    const data = read(KEY_DEPARTMENTS);
    if (Array.isArray(data) && data.length) return data;
    write(KEY_DEPARTMENTS, SEED_DEPARTMENTS);
    return structuredClone(SEED_DEPARTMENTS);
  }

  return {
    loadEmployees,
    loadDepartments,
    saveEmployees: (list) => write(KEY_EMPLOYEES, list),
    saveDepartments: (list) => write(KEY_DEPARTMENTS, list),
    reset() {
      try { localStorage.removeItem(KEY_EMPLOYEES); localStorage.removeItem(KEY_DEPARTMENTS); } catch (e) { /* bỏ qua */ }
    },
  };
})();
