# 🛠️ FIX BUG RULES & PROTOCOLS
> "Đừng đoán (Don't Assume). Hãy Log để nhìn thấy sự thật."

## 0. Quy tắc Vàng: "Log First, Fix Later" (Quan trọng nhất)
- **Description:**
    - Lỗi xảy ra nhưng không rõ nguyên nhân, hoặc sửa code rồi mà vẫn chạy sai logic.
    - Thường do giả định sai về dữ liệu đầu vào (Ví dụ: Tưởng biến `path` là `/api/chat` nhưng thực tế Gateway đã cắt thành `/chat`).
- **Possible solution to make:**
    - **In Log ngay tại cửa ngõ:** Tại dòng đầu tiên của Filter, Controller, hoặc Interceptor, BẮT BUỘC phải in ra các tham số quan trọng (`URL`, `Path`, `Headers`, `Token`).
        - *Ví dụ:* `System.out.println("🔍 INPUT PATH: " + request.getRequestURI());`
    - **Log tại các điểm rẽ nhánh (`if/else`):** Đừng chỉ log trong `if`, hãy log cả trong `else` để biết tại sao code không chạy vào logic mong muốn.
    - **Không nuốt lỗi (Silent Fail):** Trong block `try-catch`, tuyệt đối không để trống. Phải in `e.printStackTrace()` hoặc `log.error` để biết code chết ở đâu.

## 1. Lỗi HTTP 401 Unauthorized / 403 Forbidden
- **Description:** Request bị chặn, Server trả về lỗi không có quyền truy cập. Thường xảy ra ở Gateway hoặc Security Filter chain.
- **Possible solution to make:**
    - **Check Log Gateway (Áp dụng Rule #0):** In ra đường dẫn thực tế (`path`) ngay tại Filter. Kiểm tra xem URL có bị `StripPrefix` cắt mất không?
    - **Check Header:** Request có gửi kèm `Authorization: Bearer <token>` không? (Lưu ý: WebSocket Handshake không gửi được header này).
    - **Check Public Paths:** Đảm bảo đường dẫn đã được khai báo trong danh sách `permitAll` (hoặc `isPublicPath`) ở **CẢ** Gateway và Service đích.
    - **Test Cô Lập:** Gọi trực tiếp vào Service con (bỏ qua Gateway). Nếu Service con chạy được -> Lỗi tại Gateway config.

## 2. Lỗi CORS (Cross-Origin Resource Sharing)
- **Description:** Trình duyệt báo đỏ lòm: `Access to XMLHttpRequest... blocked by CORS policy`. Thường gặp khi Frontend và Backend khác port/domain.
- **Possible solution to make:**
    - **Check Origin Config:** - Nếu có gửi Cookie/Credentials: Dùng `setAllowedOriginPatterns("*")`. **Tuyệt đối không** dùng `setAllowedOrigins("*")`.
        - Đảm bảo `setAllowCredentials(true)`.
    - **Check Gateway YAML:** Xóa hoặc comment phần `globalcors` trong file `application.yml` nếu đã config bằng Java class (tránh xung đột).
    - **Disable CSRF:** Đảm bảo `.csrf(disable)` nếu test API Login (POST).

## 3. Lỗi WebSocket Connection Failed
- **Description:** SockJS báo `Whoops! Lost connection` hoặc `400 Bad Request` ngay khi vừa kết nối.
- **Possible solution to make:**
    - **Check Protocol:** Đổi `ws://` thành `http://` trong config Route của Gateway (để hỗ trợ cơ chế fallback HTTP của SockJS).
    - **Check Security:** Endpoint `/ws/**` (Handshake) bắt buộc phải `permitAll`. Token chỉ được check ở bước `STOMP CONNECT` (Interceptor), không check ở HTTP Handshake.
    - **Check Interceptor:** Đảm bảo Interceptor không chặn nhầm request handshake (dùng `shouldNotFilter` hoặc check `StompCommand.CONNECT`).

## 4. Lỗi Runtime "ClassNotFound" hoặc "NoClassDefFound"
- **Description:** Code biên dịch (Compile) không lỗi, nhưng khi chạy (Runtime) thì văng Exception đỏ lòm trong Console (VD: lỗi liên quan đến `jjwt`, `jackson`, `bean creation`).
- **Possible solution to make:**
    - **Check pom.xml:** Kiểm tra xem đã có đủ dependency chưa. Đặc biệt với các thư viện dạng interface như JWT, cần đủ bộ 3: `api`, `impl`, `jackson`.
    - **Check Scope:** Đảm bảo scope là `runtime` hoặc `compile` phù hợp.
    - **Maven Reload:** Luôn bấm "Reload Maven" và Restart App sau khi sửa pom.xml.

## 5. Lỗi Logic "Token Invalid" / "Signature Exception"
- **Description:** Gửi Token đúng nhưng Server báo sai chữ ký hoặc không giải mã được.
- **Possible solution to make:**
    - **Sync Secret Key:** Mở 2 file `application.yml` của Auth Service và Service đích (Chat/User...). Copy-paste lại dòng `jwt.secret` để đảm bảo chúng **giống hệt nhau từng ký tự**.
    - **Check Base64:** Kiểm tra xem code giải mã có yêu cầu secret key phải Base64 encode hay không.

## 6. Lỗi "Code sửa rồi mà vẫn chạy logic cũ" (Cache Ghost)
- **Description:** Đã sửa code, đã save, nhưng log vẫn in ra cái cũ hoặc lỗi cũ vẫn còn.
- **Possible solution to make:**
    - **IntelliJ:** Mở tab Maven -> Lifecycle -> Double click `clean` -> Double click `compile`.
    - **Docker:** Chạy `docker-compose up -d --build` (Bắt buộc có cờ `--build` để rebuild lại file jar/image).
    - **Re-run:** Stop hẳn ứng dụng (nút đỏ) rồi mới bấm Run lại (nút xanh).
## 7. Lỗi WebSocket "Gửi đi mất hút" (Silent Lost Message)
- **Description:** Server báo gửi thành công (Log: `Sent to WebSocket`), không có lỗi Exception, nhưng Client bên kia không nhận được gì.
- **Root Cause:** Thường do cấu hình Broker thiếu prefix đích.
  - Ví dụ: Code gửi vào `/queue/messages` nhưng `WebSocketConfig` chỉ enable `/topic`.
- **Solution:**
  - Kiểm tra `registry.enableSimpleBroker(...)`.
  - Đảm bảo có đủ các prefix mà bạn dùng để gửi tin (thường là `/topic` cho public, `/queue` cho private).
  - Kiểm tra Client subscribe đúng prefix chưa (đặc biệt là `/user/queue/...` vs `/user/ID/queue/...`).