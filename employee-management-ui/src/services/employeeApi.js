
const API_URL = "http://localhost:8080/api/employees";

// GET: Lấy danh sách nhân viên
export async function getEmployees() {
    const response = await fetch(API_URL);

    if (!response.ok) {
        throw new Error("Không thể tải danh sách nhân viên");
    }

    return response.json();
}

// POST: Thêm nhân viên
export async function createEmployee(employee) {
    const response = await fetch(API_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(employee)
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(
            Object.values(data).join(", ") ||
            "Không thể thêm nhân viên"
        );
    }

    return data;
}

// PUT: Cập nhật nhân viên
export async function updateEmployee(id, employee) {
    const response = await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(employee)
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(
            Object.values(data).join(", ") ||
            "Không thể cập nhật nhân viên"
        );
    }

    return data;
}

// DELETE: Xóa nhân viên
export async function deleteEmployee(id) {
    const response = await fetch(`${API_URL}/${id}`, {
        method: "DELETE"
    });

    if (!response.ok) {
        throw new Error("Không thể xóa nhân viên");
    }

    return response.text();
}
