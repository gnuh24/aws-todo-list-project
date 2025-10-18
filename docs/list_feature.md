# 📌 Danh sách chức năng hệ thống – v0.1.0.0 - beta (23/09/2025)

## I. Authentication & User Profile  
### 1. Đăng nhập / Đăng ký
- Login (User)  
    + Login truyền thống
    + Login google
    + Login facebook
- Register  
- Forget password  

### 2. Quản lý thông tin cá nhân
- Cập nhật mật khẩu  
- Cập nhật email  
- Cập nhật thông tin cá nhân (display_name, avatar)

## II. Quản lý Project & Task Collaboration  
### 1. Quản lý Project  
- Tạo project  
- Mời thêm người dùng vào project  
- Phân quyền trong project (Owner, Admin, Member, Viewer)  
    + Owner: Người đầu tiên tạo project có quyền mạnh nhất  
    + Admin: Quản trị có thể thêm, xóa, sửa quyền bất kì ai ngoại trừ Owner  
    + Member: Có thể thêm, xóa, task, hoàn thành task  
    + Viewer: Chỉ có thể xem và không thể làm gì khác  
- Sửa thông tin project (tên)  
- Xóa / archive project  
- Xem danh sách project tham gia  

### 2. Quản lý Section trong Project  
- Tạo section trong project  
- Đổi tên / chỉnh sửa section  
- Xóa section  
- Kéo thả sắp xếp thứ tự section trong project  

### 3. Quản lý Task trong Project  
- Tạo task trong project (theo section hoặc tự do)  
- Chỉnh sửa task (tiêu đề, mô tả, deadline, assignee)  
- Xóa task  
- Ghim / bỏ ghim task  
- Đặt trạng thái (To-do, In-progress, Done, Blocked)  
- Đặt độ ưu tiên (High, Medium, Low)  
- Tìm kiếm, lọc task theo trạng thái, ưu tiên, người thực hiện  

#### a. Task con (Subtask)
- Tạo subtask cho task chính  
- Chỉnh sửa subtask (tiêu đề, mô tả, deadline, assignee)  
- Xóa subtask  
- Đặt trạng thái và độ ưu tiên cho subtask  
- Hoàn thành / bỏ hoàn thành subtask  

#### b. Bình luận (Comment)
- Thêm comment vào task  
- Chỉnh sửa / xóa comment (chỉ người tạo mới được phép)  
- Đính kèm nhiều attachment (ảnh, file) vào comment  
- Xóa attachment khỏi comment  
- Hiển thị lịch sử comment theo thời gian  

---

## III. Inbox (Task Cá Nhân)  
- Tạo task cá nhân (không thuộc project)  
- Chỉnh sửa task cá nhân (tiêu đề, mô tả, deadline, nhãn)  
- Xóa task cá nhân  
- Ghim / bỏ ghim task cá nhân  
- Đặt trạng thái (To-do, In-progress, Done, Blocked)  
- Đặt độ ưu tiên (High, Medium, Low)  
- Tìm kiếm, lọc task cá nhân theo trạng thái, ưu tiên  
- Kéo thả để sắp xếp task cá nhân trong inbox  
- Đồng bộ task từ inbox sang project (convert sang task trong project)  

---


# (Các chức năng bên dưới này sẽ được phát triển sau)
## IV. Bấm giờ (Focus Timer)  
- Bắt đầu bấm giờ cho một task cụ thể  
- Tạm dừng / kết thúc bấm giờ  
- Cảnh báo khi người dùng tab ra ngoài (có tuỳ chọn bật/tắt cho các ngành đặc thù)  
- Ghi nhận số lần tab ra ngoài  
- Thống kê thời gian đã làm task + số lần tab ra ngoài sau khi hoàn thành  

---

## V. Thống kê & Báo cáo  
- Thống kê thời gian làm việc của người dùng  
- Thống kê số lượng task hoàn thành  
- Thống kê số lượng task bị trễ deadline  
- Thống kê theo từng project (task theo trạng thái, tiến độ, hiệu suất thành viên)  
- Biểu đồ hiệu suất cá nhân (so sánh theo tuần / tháng)  

---

## VI. Lịch (Calendar)  
- Xem tất cả task trên calendar (ngày / tuần / tháng)  
- Xem deadline của các task trong tương lai  
- Tích hợp project & task vào calendar  
- Tạo sự kiện / reminder thủ công trong lịch  
- Đồng bộ với Google Calendar / Outlook (tùy chọn mở rộng)  

---

## VII. Thông báo (Notification)  

@Admin: Người tạo
@User: Người nhận
@Role: Quyền
@Project: Project
@Task


- Được thành vào project với quyền gì đó.
    + Mô tả: Khi 1 user nào đó được thêm vào project
    + Đối tượng nhận thông báo: Người được thêm vào.
    + VD: 
        1. Người được thêm vào:  "Bạn vừa được @Admin thêm vào @Project với quyền @Quyền"
        2. Các người dùng còn lại: "@User vừa được @Admin thêm vào @Project"

- Được cập nhật quyền
    + Mô tả: Khi 1 user được Admin cập nhật quyền
    + Đối tượng nhận thông báo: Người được cập nhật quyền
    + VD: @User vừa được @Admin cập nhật quyền thành @Quyền

- Được giao task gì đó (@Admin vừa giao cho bạn 1 task mới)
    + Mô tả: Khi 1 user được admin giao task
    + Đối tượng nhận thông báo: Người được giao task, người tạo task
    + VD: @User vừa được @Admin cập nhật quyền thành @Quyền


- Task được update (Task mà bạn được giao vừa được cập nhật bởi @Admin)
    + Mô tả: Khi Task được giao cho người dùng bị thay đổi nội dung (deadline, status) bởi người khác.	
    + Đối tượng: Người được giao Task (Assignee), Người tạo Task (Creator).	
    + Ví dụ: @Admin vừa cập nhật Mô tả của Task: @Tên_Task mà bạn được giao.

- Task hoàn thành 
    + Mô tả: Khi Task được đánh dấu là hoàn thành (Completed) bởi bất kỳ User nào có quyền.	
    + Đối tượng: Người được giao Task (Assignee), Người tạo Task (Creator).	
    + Vd: Task @Tên_Task vừa được @User_Thực_hiện đánh dấu Hoàn thành.


- Task sắp tới hẹn
    + Mô tả: Hệ thống tự động gửi thông báo khi Task còn N ngày/giờ nữa là đến Deadline.	
    + Đối tượng: Người được giao Task (Assignee).	
    + Ví dụ: Task @Tên_Task của bạn sẽ sắp tới hạn (còn 4 giờ) vào lúc 10:00, 11/11/2025.
  
- Task trễ deadline
    + Mô tả: Hệ thống tự động gửi thông báo khi Task đã quá hạn (sau Deadline).	
    + Đối tượng: Người được giao Task (Assignee), Người tạo Task (Creator).	
    + Ví dụ: Task @Tên_Task đã bị trễ deadline.
  
- Các comment trong task
    + Mô tả: Khi có một bình luận mới được thêm vào Task.	
    + Đối tượng: Người được giao Task (Assignee), Người tạo Task (Creator), Những người đã từng comment trong Task (để tiện theo dõi luồng trao đổi).	
    + VÍ dụ: @User_Comment vừa thêm một comment mới vào Task @Tên_Task.
     
- Dự án bị xóa
    + Mô tả: Khi Project bị xóa khỏi hệ thống.	
    + Đối tượng: Toàn bộ User là thành viên của Project (trước khi xóa).	
    + VÍ dụ: Dự án @Project cũ vừa bị @Admin xóa.


