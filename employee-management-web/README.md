# EMS – Web prototype (HTML/CSS/JavaScript + Local Storage)

Giao diện quản lý nhân viên chạy hoàn toàn trên trình duyệt. **Không gọi Java backend**; dữ liệu nằm trong Local Storage của trình duyệt hiện tại nên không dùng chung giữa các máy/trình duyệt.

## Chạy
Mở `index.html` bằng trình duyệt (không cần server). Lần đầu trang tự nạp 8 nhân viên mẫu.

## Chức năng
- Tổng quan: số nhân viên, số phòng ban, lương trung bình.
- Danh sách: tìm theo mã/họ tên (không phân biệt dấu), lọc phòng ban/trạng thái, sắp xếp họ tên/lương, sửa/xóa (xóa có hộp thoại xác nhận).
- Form dùng chung cho thêm mới và cập nhật; báo đủ danh sách lỗi một lần.
- Thống kê theo phòng ban và theo trạng thái; lương trung bình của danh sách đang lọc.
- Xuất/nhập JSON để sao lưu; khôi phục dữ liệu mẫu.

## Cấu trúc
```
index.html            khung giao diện
css/style.css         giao diện, responsive, hỗ trợ chế độ tối
js/storage.js         đọc/ghi Local Storage (JSON), dữ liệu mẫu
js/employee-service.js  quy tắc nghiệp vụ, tìm/lọc/sắp xếp, thống kê (không đụng DOM)
js/app.js             vẽ giao diện và gắn sự kiện
```

## Lưu ý
- Quy tắc kiểm tra dữ liệu giống phần Java (xem `docs/requirements.md` của project Java).
- Font Be Vietnam Pro tải từ Google Fonts; khi offline trang tự dùng font hệ thống.
- Dữ liệu bị xóa nếu người dùng xóa dữ liệu trình duyệt; hãy xuất JSON để sao lưu.
