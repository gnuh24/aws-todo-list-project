
---

# 📌 **Danh sách chức năng hệ thống – v0.1.0.0 (Beta – 09/12/2025)**

## I. 🔐 **Authentication & User Profile**

### 1. Đăng nhập / Đăng ký

* **Login (User)**

  * Đăng nhập truyền thống (email + password)
  * Đăng nhập bằng Google
* **Register** (tạo tài khoản mới)
* **Forget Password** (quên mật khẩu – gửi email reset)

### 2. Quản lý thông tin cá nhân

* Cập nhật mật khẩu
* Cập nhật email
* Chỉnh sửa thông tin cá nhân

  * display_name
  * avatar
  * nhận/not nhận thông báo

---

## II. 📁 **Quản lý Project & Task Collaboration**

### 1. Quản lý Project

* Tạo project
* Mời user vào project
* Phân quyền:

  * **Owner** – toàn quyền
  * **Member** – tạo/sửa/xoá/hoàn thành task
* Sửa thông tin project (tên, mô tả)
* Xóa / Archive project
* Xem danh sách project tham gia

### 2. Quản lý Section trong Project

* Tạo section
* Đổi tên / chỉnh sửa section
* Xóa section

### 3. Quản lý Task

* Tạo task theo section hoặc task “tự do”
* Chỉnh sửa task:

  * tiêu đề
  * mô tả
  * deadline
  * start time
* **AI gợi ý label** cho task
* Xóa task
* Đặt mức độ ưu tiên:

  * Critical / High / Medium / Low

#### a. Bình luận (Comment)

* Thêm comment vào task
* Sửa / xóa comment (chỉ creator)
* Hiển thị lịch sử comment theo timeline

---

## III. 📆 **Lịch (Calendar)**

* Xem toàn bộ task trên calendar (ngày / tuần / tháng)
* Xem các deadline sắp tới
* Đồng bộ với project/task
* Tạo sự kiện hoặc reminder thủ công
* (Tuỳ chọn mở rộng) Đồng bộ Google Calendar / Outlook

---

## VII. 🔔 **Thông báo (Notification)**

**Các đối tượng có thể xuất hiện trong thông báo:**
`@Admin`, `@User`, `@Role`, `@Project`, `@Task`

### 1. Thêm vào project

* Khi một user được thêm vào project
* **Người nhận:** User vừa được thêm
* **Thông điệp:**

  * Người được thêm: “Bạn vừa được @Admin thêm vào @Project với quyền @Role”
  * Thành viên còn lại: “@User vừa được @Admin thêm vào @Project”

### 2. Cập nhật quyền

* Khi Admin đổi quyền của user
* **Người nhận:** User được đổi quyền
* **Ví dụ:** “@User vừa được @Admin cập nhật quyền thành @Role”

### 3. Giao task

* Khi user được giao task mới
* **Người nhận:** Assignee + người tạo task
* **Ví dụ:** “@Admin vừa giao cho bạn 1 task mới”

### 4. Task được cập nhật

* Khi task bị chỉnh sửa (deadline, status, mô tả…)
* **Người nhận:** Assignee + Creator
* **Ví dụ:** “@Admin vừa cập nhật mô tả của task @Task mà bạn được giao”

### 5. Task hoàn thành

* Khi task được đánh dấu “Completed”
* **Người nhận:** Assignee + Creator
* **Ví dụ:** “Task @Task vừa được @User hoàn thành”

### 6. Task sắp tới hạn

* Hệ thống tự gửi trước deadline
* **Người nhận:** Assignee + Creator
* **Ví dụ:** “Task @Task còn 4 giờ nữa đến hạn (10:00 – 11/11/2025)”

### 7. Task quá hạn

* Khi task bị trễ deadline
* **Người nhận:** Assignee + Creator
* **Ví dụ:** “Task @Task đã bị trễ deadline”

### 8. Comment mới trong task

* Khi có comment mới
* **Người nhận:** Assignee + Creator + Người đã từng comment
* **Ví dụ:** “@User vừa thêm comment mới vào task @Task”

### 9. Dự án bị xóa

* Khi project bị xóa khỏi hệ thống
* **Người nhận:** toàn bộ thành viên
* **Ví dụ:** “Dự án @Project vừa bị @Admin xóa”

---

