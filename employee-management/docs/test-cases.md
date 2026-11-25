# Test case

Cột **Cách kiểm** cho biết test được chạy tự động hay thủ công.
Các ca ghi *Thủ công + MySQL* cần chạy chương trình với CSDL thật; hãy tự điền cột **Kết quả** khi bạn chạy.

| # | Chức năng | Bước thực hiện | Kết quả mong đợi | Cách kiểm | Kết quả |
|---|---|---|---|---|---|
| 1 | Thêm hợp lệ | Menu 1, nhập đủ dữ liệu đúng | Báo đã thêm; xuất hiện ở menu 2 | Thủ công + MySQL | |
| 2 | Trùng mã | Thêm mã `NV001` | Lỗi "Mã nhân viên NV001 đã tồn tại" | Thủ công + MySQL | |
| 3 | Trùng email | Thêm email `an.nguyen@company.vn` | Lỗi email đã được sử dụng | Thủ công + MySQL | |
| 4 | Họ tên toàn khoảng trắng | Nhập `   ` | Lỗi họ tên bắt buộc | JUnit `NhanVienValidatorTest` | |
| 5 | Email sai định dạng | Nhập `abc@` | Lỗi định dạng email | JUnit | |
| 6 | Lương <= 0 | Nhập `0` và `-5` | Lỗi lương phải lớn hơn 0 | JUnit | |
| 7 | Ngày vào làm tương lai | Nhập ngày mai | Lỗi ngày tương lai | JUnit | |
| 8 | Số điện thoại sai | Nhập `12345` | Lỗi số điện thoại; để trống thì hợp lệ | JUnit | |
| 9 | Giới tính/trạng thái sai | Giá trị ngoài danh sách | Báo 2 lỗi | JUnit | |
| 10 | Phòng ban không tồn tại | Nhập mã `ZZ` | Lỗi phòng ban không tồn tại | Thủ công + MySQL | |
| 11 | Ngày sai định dạng | Nhập `31/02/2020` | Báo nhập lại, không thoát chương trình | Thủ công | |
| 12 | Lương không phải số | Nhập `abc` | Báo "Lương phải là số", hỏi lại | Thủ công | |
| 13 | Xem chi tiết | Menu 3 với `NV001` | Hiện đủ 12 trường | Thủ công + MySQL | |
| 14 | Xem mã không tồn tại | Menu 3 với `NV999` | Lỗi không tìm thấy, chương trình vẫn chạy | Thủ công + MySQL | |
| 15 | Cập nhật giữ nguyên | Menu 4, nhấn Enter ở mọi trường | Dữ liệu không đổi | Thủ công + MySQL | |
| 16 | Cập nhật sang email người khác | Menu 4, đổi email trùng | Lỗi trùng email | Thủ công + MySQL | |
| 17 | Cập nhật giữ email của mình | Menu 4, chỉ đổi chức vụ | Thành công (không báo trùng với chính mình) | Thủ công + MySQL | |
| 18 | Xóa có xác nhận | Menu 5, gõ `n` | Không xóa | Thủ công + MySQL | |
| 19 | Xóa xác nhận `y` | Menu 5, gõ `y` | Đã xóa; menu 3 báo không tìm thấy | Thủ công + MySQL | |
| 20 | Tìm không dấu | Menu 6, `nguyen` | Ra `Nguyễn Văn An` | Thủ công + MySQL | |
| 21 | Tìm ký tự đặc biệt | Menu 6, `%` | Không trả về toàn bộ danh sách | Thủ công + MySQL | |
| 22 | Lọc phòng ban / trạng thái | Menu 7 `IT`; menu 8 `Đã nghỉ` | Chỉ ra đúng nhóm | Thủ công + MySQL | |
| 23 | Sắp xếp | Menu 9, 5 kiểu | Thứ tự đúng | Thủ công + MySQL | |
| 24 | Thống kê | Menu 10 | Tổng = tổng nhân viên; phòng trống hiện 0 | Thủ công + MySQL | |
| 25 | Lương trung bình theo bộ lọc | Menu 11, lọc `IT` | Bằng trung bình tay của nhóm IT | Thủ công + MySQL | |
| 26 | Lương TB danh sách rỗng | Menu 11, từ khóa không tồn tại | Báo không có nhân viên, không lỗi chia 0 | Thủ công + MySQL | |
| 27 | Xóa phòng ban có nhân viên | Menu 12 → X → `IT` | Bị từ chối, nêu số nhân viên còn lại | Thủ công + MySQL | |
| 28 | Dữ liệu còn sau khi khởi động lại | Thêm nhân viên, thoát, chạy lại | Nhân viên vẫn còn | Thủ công + MySQL | |
| 29 | MySQL chưa chạy | Tắt MySQL, chạy menu 2 | Thông báo lỗi kết nối rõ ràng, không văng stack trace | Thủ công | |

## Web prototype
Logic của `employee-service.js` và `storage.js` đã được kiểm bằng 24 ca tự động chạy trên Node
(thêm/sửa/xóa, trùng mã/email, tìm không dấu, lọc, sắp xếp, thống kê, xuất/nhập JSON, dữ liệu còn sau khi nạp lại module).
Phần giao diện (DOM, hộp thoại, responsive) cần bạn kiểm thủ công trên trình duyệt:

| # | Kiểm tra | Mong đợi |
|---|---|---|
| W1 | Mở `index.html`, thêm nhân viên, nhấn F5 | Nhân viên vẫn còn |
| W2 | Bấm Xóa rồi chọn "Giữ lại" | Không xóa |
| W3 | Gõ `<b>x</b>` vào họ tên | Hiện nguyên văn, không in đậm |
| W4 | Thu nhỏ cửa sổ dưới 560px | Bố cục một cột, bảng cuộn ngang |
| W5 | Xuất JSON → Khôi phục dữ liệu mẫu → Nhập lại file | Dữ liệu trở lại như lúc xuất |
