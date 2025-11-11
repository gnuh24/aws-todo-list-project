
---

# **Deploy Frontend ReactJS lên AWS S3**

## **Cách 1: Deploy thủ công (Tạo S3 bằng AWS UI rồi tải code lên và config)**

### **1. Build React app**

Trong thư mục project React:

```bash
npm install      # cài dependencies
npm run build    # tạo thư mục build/
```

* Thư mục `build/` chứa toàn bộ **HTML, JS, CSS** production.
* Đây là folder bạn sẽ upload lên S3.

---

### **2. Tạo S3 Bucket**

1. Truy cập **AWS Console → S3 → Create bucket**.
2. Cấu hình:

   * **Bucket name:** ví dụ `aws-todolist-project`.
   * **Region:** chọn gần bạn.
   * **Block all public access:** **bỏ chọn** (FE cần public).
3. Tạo bucket.

---

### **3. Upload React build lên S3**

1. Vào bucket → **Upload** → chọn toàn bộ nội dung **trong thư mục `build/`**.
2. Hoặc tạo folder `frontend/` nếu muốn, nhưng cần nhớ path.
3. Nhấn **Upload** và đợi AWS upload xong.

> Lưu ý: Không upload thư mục `build` mà **upload nội dung trong build**, tức là file `index.html` + thư mục `static/`.

---

### **4. Cấu hình Static Website Hosting**

1. Vào **Properties → Static website hosting**.
2. Chọn **Use this bucket to host a website**.
3. Cấu hình:

   * **Index document:** `index.html`
   * **Error document:** `index.html`

     > Bắt buộc cho React Router SPA.
4. Lưu lại → sẽ có **Endpoint URL** ví dụ:

```
http://aws-todolist-project.s3-website-us-east-1.amazonaws.com
```

* Truy cập URL này, app React sẽ chạy.
* Nếu người dùng gõ URL sai → vẫn load `index.html` → React Router render đúng.

---

<!-- ### **5. Kiểm tra app**

* Mở **Endpoint URL** trên trình duyệt.
* Kiểm tra các route React (Home, /login, /register, /app/today…) có hoạt động không.
* Nếu dùng API local, cần expose backend bằng **ngrok** để FE gọi được.

---

### **6. Lưu ý cho backend API**

* React build là static → tất cả API call phải dùng **URL public**.
* Dev/test: dùng **ngrok** để expose backend local:

```bash
ngrok http 8080   # giả sử backend chạy cổng 8080
```

* Cập nhật `.env.production`:

```env
REACT_APP_API_URL=https://abcd1234.ngrok.io
```

* Build lại và upload lên S3 nếu cần test. -->

<!-- ---

### **7. Tùy chọn: HTTPS + CDN**

* Dùng **CloudFront** làm CDN + HTTPS:

  * Origin: chọn S3 bucket.
  * Default root object: `index.html`.
  * Redirect HTTP → HTTPS.
* Khi đó URL public sẽ là CloudFront, nhanh hơn, bảo mật hơn. -->

---

💡 **Tóm tắt**

1. Build React → tạo `build/`.
2. Tạo S3 bucket → upload nội dung `build/`.
3. Cấu hình **Static Website Hosting** (index + error = `index.html`).
4. Kiểm tra app chạy đúng, route SPA vẫn hoạt động.
5. (Optional) CloudFront + HTTPS.
6. Nếu backend local → dùng ngrok.

---

Mình có thể viết tiếp **Cách 2: Deploy bằng CLI / AWS CLI** để bạn deploy nhanh không cần AWS UI.

Bạn có muốn mình làm luôn không?
