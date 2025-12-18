
---

# 📌 **Danh sách chức năng hệ thống – v1.0 (Beta – 09/12/2025)**

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

---

## VII. Thông báo (Notification)

@Admin: Người tạo

@User: Người nhận

@Role: Quyền

@Project: Project

@Task


- Được thành vào project với quyền gì đó. ( ví dụ 2 sai vì chỉ thông báo đến user được thêm vào)

  + Mô tả: Khi 1 user nào đó được thêm vào project

  + Đối tượng nhận thông báo: Người được thêm vào.

  + VD:

    1. Người được thêm vào: "Bạn vừa được @Admin thêm vào @Project với quyền @Quyền"

    2. Các người dùng còn lại: "@User vừa được @Admin thêm vào @Project"


- Được cập nhật quyền (gửi đến toàn bộ member trong project)

  + Mô tả: Khi 1 user được Admin cập nhật quyền

  + Đối tượng nhận thông báo: Người được cập nhật quyền

  + VD: @User vừa được @Admin cập nhật quyền thành @Quyền



- Được giao task gì đó (@Admin vừa giao cho bạn 1 task mới) (sai ví dụ)

  + Mô tả: Khi 1 user được admin giao task

  + Đối tượng nhận thông báo: Người được giao task, người tạo task

  + VD: @User vừa được @Admin cập nhật quyền thành @Quyền


- Task được update (Task mà bạn được giao vừa được cập nhật bởi @Admin) (Cập nhật ví dụ)

  + Mô tả: Khi Task được giao cho người dùng bị thay đổi nội dung (deadline, status) bởi người khác.

  + Đối tượng: Người được giao Task (Assignee), Người tạo Task (Creator).

  + Ví dụ: @Tên_Task trong dự án @Project vừa được chỉnh sửa.


- Task hoàn thành (Đúng)

  + Mô tả: Khi Task được đánh dấu là hoàn thành (Completed) bởi bất kỳ User nào có quyền.

  + Đối tượng: Người được giao Task (Assignee), Người tạo Task (Creator).

  + Vd: Task @Tên_Task vừa được @User_Thực_hiện đánh dấu Hoàn thành.


- Task sắp tới hẹn (Cập nhật ví dụ)

  + Mô tả: Hệ thống tự động gửi thông báo khi Task còn N ngày/giờ nữa là đến Deadline.

  + Đối tượng: Người được giao Task (Assignee), Người tạo Task (Creator).

  + Ví dụ: Nhiệm vụ @Tên_Task trong dự án @Project sẽ sắp tới hạn vào lúc 10:00, 11/11/2025.


- Task trễ deadline (Cập nhật ví dụ)

  + Mô tả: Hệ thống tự động gửi thông báo khi Task đã quá hạn (sau Deadline).

  + Đối tượng: Người được giao Task (Assignee), Người tạo Task (Creator).

  + Ví dụ: Nhiệm vụ @Tên_Task trong dự án @Project đã hết hạn thực hiện vào lúc 10:00, 11/11/2025.


- Các comment trong task (Không có gửi cho người được giao do code thiếu)

  + Mô tả: Khi có một bình luận mới được thêm vào Task.

  + Đối tượng: Người được giao Task (Assignee), Người tạo Task (Creator), Những người đã từng comment trong Task (để tiện theo dõi luồng trao đổi).

  + VÍ dụ: @User_Comment vừa thêm một comment mới vào Task @Tên_Task.


- Dự án bị xóa (đúng)

  + Mô tả: Khi Project bị xóa khỏi hệ thống.

  + Đối tượng: Toàn bộ User là thành viên của Project (trước khi xóa). + VÍ dụ: Dự án @Project cũ vừa bị @Admin xóa
---

