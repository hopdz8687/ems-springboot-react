
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

    // Phân trang
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize] = useState(10);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    // Làm mới dữ liệu sau khi thêm, sửa, xóa
    const [reloadKey, setReloadKey] = useState(0);

    // Tải dữ liệu từ Backend theo từng trang
    useEffect(() => {
        let isActive = true;

        async function loadEmployees() {
            try {
                setLoading(true);
                setError("");

                const data = await getEmployees(
                    currentPage,
                    pageSize,
                    keyword
                );

                if (!isActive) return;

                setEmployees(data.content);
                setTotalPages(data.totalPages);
                setTotalElements(data.totalElements);
                setCurrentPage(data.number);
            } catch (err) {
                if (isActive) {
                    setError(err.message);
                }
            } finally {
                if (isActive) {
                    setLoading(false);
                }
            }
        }

        // Tránh gọi API liên tục khi người dùng đang nhập tìm kiếm
        const timer = setTimeout(() => {
            loadEmployees();
        }, 300);

        return () => {
            isActive = false;
            clearTimeout(timer);
        };
    }, [currentPage, pageSize, keyword, reloadKey]);

    // Hiển thị thông báo
    function showNotification(text) {
        setMessage(text);

        setTimeout(() => {
            setMessage("");
        }, 3000);
    }

    // Thêm hoặc cập nhật nhân viên
    async function handleSave(employee) {
        try {
            if (editingEmployee) {
                await updateEmployee(editingEmployee.id, employee);
                showNotification("Cập nhật nhân viên thành công!");
            } else {
                await createEmployee(employee);
                showNotification("Thêm nhân viên thành công!");
            }

            setShowForm(false);
            setEditingEmployee(null);

            // Sau khi thêm mới, quay về trang đầu
            setCurrentPage(0);
            setReloadKey(prev => prev + 1);
        } catch (err) {
            setError(err.message);
        }
    }

    // Xóa nhân viên
    async function handleDelete(id) {
        const confirmed = window.confirm(
            "Bạn có chắc chắn muốn xóa nhân viên này không?"
        );

        if (!confirmed) return;

        try {
            await deleteEmployee(id);

            showNotification("Xóa nhân viên thành công!");

            // Nếu xóa bản ghi cuối cùng của trang,
            // chuyển về trang trước nếu cần
            if (employees.length === 1 && currentPage > 0) {
                setCurrentPage(prev => prev - 1);
            }

            setReloadKey(prev => prev + 1);
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

    // Chuyển trang
    function handlePageChange(page) {
        if (page < 0 || page >= totalPages) return;

        setCurrentPage(page);
    }

    // Tìm kiếm
    function handleSearch(e) {
        setKeyword(e.target.value);
        setCurrentPage(0);
    }

    // Thống kê
    const totalEmployees = totalElements;

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
                        <h2>{employees.length}</h2>
                        <small>Nhân viên trên trang hiện tại</small>
                    </div>

                    <div className="stat-card">
                        <span>Chức vụ</span>
                        <h2>{totalPositions}</h2>
                        <small>Số loại chức vụ trên trang hiện tại</small>
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
                            onChange={handleSearch}
                        />

                    </div>

                    {loading ? (
                        <p>Đang tải dữ liệu...</p>
                    ) : error ? (
                        <p className="error">
                            {error}
                        </p>
                    ) : (
                        <>
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

                                        {employees.map(employee => (

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

                                        {employees.length === 0 && (
                                            <tr>
                                                <td colSpan="7" className="empty-state">
                                                    Không tìm thấy nhân viên phù hợp.
                                                </td>
                                            </tr>
                                        )}

                                    </tbody>

                                </table>

                            </div>

                            {/* PAGINATION */}
                            <div className="pagination">

                                <button
                                    disabled={currentPage === 0 || loading}
                                    onClick={() =>
                                        handlePageChange(currentPage - 1)
                                    }
                                >
                                    Trước
                                </button>

                                <span>
                                    Trang {totalPages === 0 ? 0 : currentPage + 1}
                                    {" / "}
                                    {totalPages}
                                </span>

                                <button
                                    disabled={
                                        currentPage >= totalPages - 1 ||
                                        totalPages === 0 ||
                                        loading
                                    }
                                    onClick={() =>
                                        handlePageChange(currentPage + 1)
                                    }
                                >
                                    Sau
                                </button>

                            </div>

                            <p className="pagination-info">
                                Tổng cộng {totalElements} nhân viên
                                {" · "}
                                Hiển thị {employees.length} nhân viên mỗi trang
                            </p>
                        </>
                    )}

                </section>

            </main>

        </div>
    );
}

export default App;
