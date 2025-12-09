
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

💡 **Tóm tắt**

1. Build React → tạo `build/`.
2. Tạo S3 bucket → upload nội dung `build/`.
3. Cấu hình **Static Website Hosting** (index + error = `index.html`).
4. Kiểm tra app chạy đúng, route SPA vẫn hoạt động.
5. (Optional) CloudFront + HTTPS.
6. Nếu backend local → dùng ngrok.

---

Tuyệt 😎 bạn dùng **Arch Linux** thì cài AWS CLI và build React sẽ nhanh hơn nhiều — mình sẽ chỉnh lại phần hướng dẫn cho **chuẩn với Arch** luôn nhé.

---


Tuyệt vời 😎
Dưới đây là **phiên bản hoàn chỉnh của hướng dẫn Deploy Frontend ReactJS lên AWS S3 bằng AWS CLI**,
được chia nhánh rõ ràng cho **Linux (Arch, Ubuntu, Fedora, …)** và **Windows**.
Các bước từ build → upload → public → website hosting đều hợp nhất và chạy được trên cả hai nền tảng.

---

## **Cách 2: Deploy bằng AWS CLI (Dành cho Linux và Windows)**

---

### **1️⃣ Cài AWS CLI**

#### 🐧 **Linux (Arch, Ubuntu, Fedora, …)**

* **Arch Linux:**

  ```bash
  sudo pacman -S aws-cli
  ```

* **Ubuntu / Debian:**

  ```bash
  sudo apt update
  sudo apt install awscli -y
  ```

* **Fedora / RHEL:**

  ```bash
  sudo dnf install awscli -y
  ```

Kiểm tra:

```bash
aws --version
```

Ví dụ:

```
aws-cli/2.17.25 Python/3.12.5 Linux/6.11.2-arch-x86_64
```

---

#### 🪟 **Windows**

1. Tải AWS CLI v2 installer:
   👉 [https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html#windows](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html#windows)
2. Chạy file `.msi` và cài đặt theo hướng dẫn.
3. Sau khi xong, mở **PowerShell** và kiểm tra:

   ```powershell
   aws --version
   ```

---

### **2️⃣ Cấu hình thông tin AWS**

Dùng chung cho mọi hệ điều hành 👇

```bash
aws configure
```

Nhập:

| Trường                | Ví dụ                | Giải thích                 |
| --------------------- | -------------------- | -------------------------- |
| AWS Access Key ID     | `AKIAXXXXXXXX`       | Lấy từ IAM của AWS         |
| AWS Secret Access Key | `xxxxxxxxxxxxxxxxxx` | Cặp key tương ứng          |
| Default region name   | `ap-southeast-1`     | (Singapore – gần Việt Nam) |
| Default output format | `json`               | hoặc `text`, tuỳ thích     |

> ⚙️ Sau khi nhập xong, AWS sẽ lưu thông tin tại:
>
> ```
> ~/.aws/credentials
> ~/.aws/config
> ```
>
> (Windows: `C:\Users\<TênUser>\.aws\`)

---

### **3️⃣ Build React app**

Trong thư mục FE (ví dụ `todolist-frontend/`):

```bash
npm install
npm run build
```

Sau khi xong, sẽ có thư mục `build/` chứa:

```
index.html
static/
asset-manifest.json
favicon.ico
manifest.json
```

---

### **4️⃣ Tạo S3 Bucket**

Tên bucket phải **duy nhất toàn cầu** (đặt khác đi nếu lỗi trùng tên):

```bash
aws s3 mb s3://aws-todolist-project --region ap-southeast-1
```

---

### **5️⃣ Bật quyền public cho bucket**

S3 mặc định **chặn public access**, nên ta cần mở quyền đọc file tĩnh:

```bash
aws s3api put-bucket-policy \
  --bucket aws-todolist-project \
  --policy '{
    "Version": "2012-10-17",
    "Statement": [
      {
        "Sid": "PublicReadGetObject",
        "Effect": "Allow",
        "Principal": "*",
        "Action": "s3:GetObject",
        "Resource": "arn:aws:s3:::aws-todolist-project/*"
      }
    ]
  }'
```

---

### **6️⃣ Upload code React lên S3**

```bash
aws s3 sync build/ s3://aws-todolist-project --delete
```

Giải thích:

* `sync`: đồng bộ nội dung `build/`
* `--delete`: xóa file cũ trên S3 nếu không còn trong build mới

---

### **7️⃣ Bật chế độ Static Website Hosting**

```bash
aws s3 website s3://aws-todolist-project/ \
  --index-document index.html \
  --error-document index.html
```

> ⚠️ Bắt buộc để `error-document` = `index.html` nhằm React Router load đúng route.

---

### **8️⃣ Lấy URL website**

Sau khi bật hosting, AWS trả về URL dạng:

```
http://aws-todolist-project.s3-website-ap-southeast-1.amazonaws.com
```

Mở link này trên trình duyệt để test React App.

---

### **9️⃣ Cập nhật khi có build mới**

```bash
npm run build
aws s3 sync build/ s3://aws-todolist-project --delete
```

---

## 💡 **Tóm tắt nhanh**

| Bước | Lệnh / Hành động                                                                                    | Mô tả                 |
| ---- | --------------------------------------------------------------------------------------------------- | --------------------- |
| 1    | `sudo pacman -S aws-cli` / `aws --version`                                                          | Cài AWS CLI           |
| 2    | `aws configure`                                                                                     | Thiết lập key, region |
| 3    | `npm run build`                                                                                     | Build React FE        |
| 4    | `aws s3 mb s3://aws-todolist-project`                                                               | Tạo bucket            |
| 5    | `aws s3api put-bucket-policy …`                                                                     | Cho phép public       |
| 6    | `aws s3 sync build/ s3://aws-todolist-project --delete`                                             | Upload code           |
| 7    | `aws s3 website s3://aws-todolist-project/ --index-document index.html --error-document index.html` | Bật hosting           |
| 8    | Truy cập URL                                                                                        | Xem kết quả           |

---


