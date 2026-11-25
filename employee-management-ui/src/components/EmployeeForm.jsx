
import { useEffect, useState } from "react";

const initialForm = {
    fullName: "",
    email: "",
    phone: "",
    position: "",
    salary: ""
};

function EmployeeForm({ employee, onSave, onCancel }) {
    const [form, setForm] = useState(initialForm);
    const [error, setError] = useState("");

    useEffect(() => {
        if (employee) {
            setForm({
                fullName: employee.fullName,
                email: employee.email,
                phone: employee.phone || "",
                position: employee.position,
                salary: employee.salary
            });
        } else {
            setForm(initialForm);
        }
    }, [employee]);

    function handleChange(e) {
        const { name, value } = e.target;

        setForm(prev => ({
            ...prev,
            [name]: value
        }));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");

        try {
            await onSave({
                ...form,
                salary: Number(form.salary)
            });
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <h2>
                {employee ? "Cập nhật nhân viên" : "Thêm nhân viên"}
            </h2>

            <input name="fullName" placeholder="Họ tên"
                value={form.fullName} onChange={handleChange} required />

            <input name="email" type="email" placeholder="Email"
                value={form.email} onChange={handleChange} required />

            <input name="phone" placeholder="Số điện thoại"
                value={form.phone} onChange={handleChange} />

            <input name="position" placeholder="Chức vụ"
                value={form.position} onChange={handleChange} required />

            <input name="salary" type="number" min="0"
                placeholder="Lương"
                value={form.salary} onChange={handleChange} required />

            {error && <p style={{ color: "red" }}>{error}</p>}

            <button type="submit">
                {employee ? "Lưu thay đổi" : "Thêm nhân viên"}
            </button>

            <button type="button" onClick={onCancel}>
                Hủy
            </button>
        </form>
    );
}

export default EmployeeForm;
