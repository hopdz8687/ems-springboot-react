import { useEffect, useState } from "react";

import {
    getEmployees,
    createEmployee,
    updateEmployee,
    deleteEmployee
} from "./services/employeeApi";

import EmployeeForm from "./components/EmployeeForm";
import "./App.css";

function App() {
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [showForm, setShowForm] = useState(false);
    const [editingEmployee, setEditingEmployee] = useState(null);
    const [message, setMessage] = useState("");
    const [keyword, setKeyword] = useState("");
    const [activeMenu, setActiveMenu] = useState("overview");

    // Lấy danh sách nhân viên từ Backend
    async function loadEmployees() {
        try {
            setError("");
            const data = await getEmployees();
            setEmployees(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    // Tự động tải dữ liệu khi mở ứng dụng
    useEffect(() => {
        loadEmployees();
    }, []);

    // Thêm hoặc cập nhật nhân viên
    async function handleSave(employee) {
        if (editingEmployee) {
            await updateEmployee(editingEmployee.id, employee);
            showNotification("Cập nhật nhân viên thành công!");
        } else {
            await createEmployee(employee);
            showNotification("Thêm nhân viên thành công!");
        }

        await loadEmployees();

        setShowForm(false);
        setEditingEmployee(null);
    }

    // Hiển thị thông báo
    function showNotification(text) {
        setMessage(text);

        setTimeout(() => {
            setMessage("");
        }, 3000);
    }

    // Xóa nhân viên
    async function handleDelete(id) {
        const confirmed = window.confirm(
            "Bạn có chắc chắn muốn xóa nhân viên này không?"
        );

        if (!confirmed) return;

        try {
            await deleteEmployee(id);
            await loadEmployees();
            showNotification("Xóa nhân viên thành công!");
        } catch (err) {
            setError(err.message);
        }
    }

    // Mở form chỉnh sửa
    function handleEdit(employee) {
        setEditingEmployee(employee);
        setShowForm(true);
    }

    // Mở form thêm mới
    function handleAdd() {
        setEditingEmployee(null);
        setShowForm(true);
    }

    // Đóng form
    function handleCancel() {
        setShowForm(false);
        setEditingEmployee(null);
    }

    // Tìm kiếm nhân viên trên Frontend
    const filteredEmployees = employees.filter(employee =>
        [
            employee.fullName,
            employee.email,
            employee.position
        ].some(value =>
            value?.toLowerCase().includes(keyword.toLowerCase())
        )
    );

    // Thống kê
    const totalEmployees = employees.length;

    const totalPositions = new Set(
        employees.map(employee => employee.position)
    ).size;

    return (
        <div className="dashboard">

            {/* SIDEBAR */}
            <aside className="sidebar">

                <h2 className="brand">
                    EMS<span>.</span>
                </h2>

                <p className="menu-label">MENU</p>

                <div
                    className={`menu-item ${activeMenu === "overview" ? "active" : ""}`}
                    onClick={() => setActiveMenu("overview")}
                >
                    <span>▦</span> Tổng quan
                </div>

                <div
                    className={`menu-item ${activeMenu === "employees" ? "active" : ""}`}
                    onClick={() => setActiveMenu("employees")}
                >
                    <span>♙</span> Nhân viên
                </div>

                <div
                    className={`menu-item ${activeMenu === "departments" ? "active" : ""}`}
                    onClick={() => setActiveMenu("departments")}
                >
                    <span>▤</span> Phòng ban
                </div>

                <div
                    className={`menu-item ${activeMenu === "settings" ? "active" : ""}`}
                    onClick={() => setActiveMenu("settings")}
                >
                    <span>⚙</span> Cài đặt
                </div>

                <div className="sidebar-footer">
                    Employee Management System
                    <small>Version 1.0</small>
                </div>

            </aside>

            {/* MAIN CONTENT */}
            <main className="main-content">

                {/* HEADER */}
                <header className="topbar">

                    <div>
                        <h2>Tổng quan</h2>
                        <p>Quản lý nhân sự nội bộ</p>
                    </div>

                    <div className="admin-info">

                        <div className="avatar">
                            AD
                        </div>

                        <div>
                            <strong>Administrator</strong>
                            <small>Quản trị viên</small>
                        </div>

                    </div>

                </header>

                {/* WELCOME */}
                <section className="welcome">

                    <div>
                        <h1>Xin chào, Administrator!</h1>
                        <p>
                            Chào mừng đến với hệ thống quản lý nhân sự EMS.
                        </p>
                    </div>

                    <button onClick={handleAdd}>
                        + Thêm nhân viên
                    </button>

                </section>

                {/* STATISTICS */}
                <section className="stats-grid">

                    <div className="stat-card">
                        <span>Tổng nhân viên</span>
                        <h2>{totalEmployees}</h2>
                        <small>Nhân sự trong hệ thống</small>
                    </div>

                    <div className="stat-card">
                        <span>Đang hiển thị</span>
                        <h2>{filteredEmployees.length}</h2>
                        <small>Kết quả tìm kiếm</small>
                    </div>

                    <div className="stat-card">
                        <span>Chức vụ</span>
                        <h2>{totalPositions}</h2>
                        <small>Số loại chức vụ</small>
                    </div>

                </section>

                {/* NOTIFICATIONS */}
                {message && (
                    <div className="success">
                        {message}
                    </div>
                )}

                {error && (
                    <div className="error">
                        {error}
                    </div>
                )}

                {/* EMPLOYEE FORM */}
                {showForm && (
                    <EmployeeForm
                        employee={editingEmployee}
                        onSave={handleSave}
                        onCancel={handleCancel}
                    />
                )}

                {/* EMPLOYEE LIST */}
                <section className="employee-section">

                    <div className="section-header">

                        <div>
                            <h2>Danh sách nhân viên</h2>
                            <p>
                                Thông tin nhân sự của công ty
                            </p>
                        </div>

                        <input
                            type="text"
                            placeholder="Tìm theo tên, email, chức vụ..."
                            value={keyword}
                            onChange={e => setKeyword(e.target.value)}
                        />

                    </div>

                    {loading ? (
                        <p>Đang tải dữ liệu...</p>
                    ) : error ? (
                        <p className="error">
                            {error}
                        </p>
                    ) : (
                        <div className="table-wrapper">

                            <table className="employee-table">

                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Họ và tên</th>
                                        <th>Email</th>
                                        <th>Điện thoại</th>
                                        <th>Chức vụ</th>
                                        <th>Lương</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>

                                <tbody>

                                    {filteredEmployees.map(employee => (

                                        <tr key={employee.id}>

                                            <td>
                                                <span className="id-badge">
                                                    #{employee.id}
                                                </span>
                                            </td>

                                            <td>
                                                <strong>
                                                    {employee.fullName}
                                                </strong>
                                            </td>

                                            <td>
                                                {employee.email}
                                            </td>

                                            <td>
                                                {employee.phone || "-"}
                                            </td>

                                            <td>
                                                <span className="position-badge">
                                                    {employee.position}
                                                </span>
                                            </td>

                                            <td className="salary-cell">
                                                {Number(employee.salary)
                                                    .toLocaleString("vi-VN")} đ
                                            </td>

                                            <td>
                                                <div className="action-buttons">

                                                    <button
                                                        className="edit-btn"
                                                        onClick={() =>
                                                            handleEdit(employee)
                                                        }
                                                    >
                                                        Sửa
                                                    </button>

                                                    <button
                                                        className="delete-btn"
                                                        onClick={() =>
                                                            handleDelete(employee.id)
                                                        }
                                                    >
                                                        Xóa
                                                    </button>

                                                </div>
                                            </td>

                                        </tr>

                                    ))}

                                    {filteredEmployees.length === 0 && (
                                        <tr>
                                            <td colSpan="7" className="empty-state">
                                                Không tìm thấy nhân viên phù hợp.
                                            </td>
                                        </tr>
                                    )}

                                </tbody>

                            </table>

                        </div>
                    )}

                </section>

            </main>

        </div>
    );
}

export default App;