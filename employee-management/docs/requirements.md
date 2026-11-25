# Đặc tả và bảng đối chiếu yêu cầu

| Yêu cầu trong đề bài | Cách đáp ứng | Vị trí |
|---|---|---|
| Thêm / xem danh sách / xem chi tiết / sửa / xóa có xác nhận | Menu 1, 2, 3, 4, 5 (xóa yêu cầu gõ `y`) | `NhanVienController` |
| Tìm theo mã hoặc họ tên | Menu 6, `LIKE` trên hai cột, không phân biệt hoa thường/dấu | `NhanVienRepository.search` |
| Lọc theo phòng ban, trạng thái | Menu 7, 8 (cùng dùng `BoLoc`) | `BoLoc`, `where()` |
| Sắp xếp theo họ tên hoặc lương | Menu 9, enum `SapXep` | `SapXep` |
| Thống kê số lượng theo phòng ban | Menu 10, `LEFT JOIN` + `GROUP BY` (phòng trống hiện số 0) | `countByDepartment` |
| Lương trung bình của danh sách phù hợp | Menu 11, `AVG` với đúng bộ lọc; danh sách rỗng thì báo không tính được | `averageSalary` |
| Không trùng mã / email | Service kiểm tra trước + UNIQUE/PK ở CSDL | `NhanVienService.kiemTra`, `ems.sql` |
| Trường bắt buộc, định dạng | `NhanVienValidator` gom toàn bộ lỗi trong một lần báo | `NhanVienValidator` |
| Phòng ban hợp lệ | Service kiểm tra tồn tại + khóa ngoại | `NhanVienService`, FK |
| Thông báo rõ ràng khi lỗi | `ValidationException` liệt kê từng lỗi; lỗi DB được dịch sang tiếng Việt | `exception/`, `translate()` |
| Dữ liệu lưu trong MySQL | JDBC + `ems.sql` | `repository/` |
| Web: Local Storage, JSON | `storage.js` lưu JSON; có xuất/nhập JSON; dữ liệu còn sau khi tải lại trang | `employee-management-web/js/` |

## Quy tắc dữ liệu
| Trường | Quy tắc |
|---|---|
| Mã nhân viên | Bắt buộc, duy nhất, chữ/số/`-`/`_`, tối đa 20 ký tự, tự chuyển chữ hoa |
| Họ tên | Bắt buộc, không chỉ khoảng trắng, tối đa 100, gộp khoảng trắng thừa |
| Ngày sinh | Tùy chọn; nếu có thì không ở tương lai |
| Giới tính | Nam / Nữ / Khác |
| Email | Bắt buộc, đúng định dạng cơ bản, duy nhất (lưu chữ thường) |
| Số điện thoại | Tùy chọn; nếu có: 10 chữ số bắt đầu bằng 0, hoặc `+84` + 9 chữ số |
| Phòng ban | Bắt buộc, phải tồn tại |
| Chức vụ | Bắt buộc |
| Lương cơ bản | > 0 |
| Ngày vào làm | Bắt buộc, không ở tương lai, phải sau ngày sinh |
| Trạng thái | Đang làm / Đã nghỉ |

## Giả định đã chọn (có thể đổi theo yêu cầu của người hướng dẫn)
- Ngày sinh và số điện thoại không bắt buộc (đề bài không ghi "bắt buộc").
- Mã nhân viên không đổi được sau khi tạo (là định danh).
- Xóa nhân viên là xóa cứng; nếu cần lưu lịch sử, dùng trạng thái "Đã nghỉ" thay vì xóa.
