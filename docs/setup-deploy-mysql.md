# ☁️ Hướng Dẫn Deploy MySQL Database trên AWS RDS

> Triển khai cơ sở dữ liệu MySQL dùng **Amazon RDS**, phục vụ cho dự án **AWS TODOLIST**.  
> Video hướng dẫn chi tiết tại: [🎥 YouTube - Deploy MySQL on AWS RDS (Acgdf58Rm6M)](https://www.youtube.com/watch?v=Acgdf58Rm6M)

---

## 🧭 1. Mở Amazon RDS Console
Truy cập:
👉 [https://console.aws.amazon.com/rds](https://console.aws.amazon.com/rds)

Chọn **“Create database”**

---

## ⚙️ 2. Cấu Hình Cơ Bản

| Mục | Giá trị đề xuất |
|------|----------------|
| **Engine type** | MySQL |
| **Version** | MySQL 8.0 (mới nhất, ổn định) |
| **Templates** | Free Tier |
| **DB instance identifier** | `aws-todolist-db` |
| **Master username** | `admin` |
| **Master password** | Ví dụ: `AwsTodolist@123` |
| **DB instance size** | `db.t3.micro` |
| **Allocated storage** | 20 GiB (SSD) |

---

## 🔒 3. Cấu Hình Mạng & Bảo Mật

**Connectivity**
- **VPC**: `Default VPC`
- **Public access**: ✅ *Yes (tạm thời cho test hoặc dev)*
- **VPC security group**:  
  Đây là phần cực quan trọng — nếu không đúng, **WorkBench hoặc backend sẽ không gọi được RDS**.  

### 🔹 Cấu hình Security Group
1. Tạo mới **Security Group** cho RDS.  
2. Thêm **Inbound Rule**:
   - Type: `MySQL/Aurora`
   - Port: `3306`
   - Source:
     - Nếu backend chạy trên **EC2 trong cùng VPC** → chọn **Custom** và điền **Security Group của EC2**  
     - Nếu muốn kết nối từ local (máy dev) → chọn **My IP**  
     - Chỉ test nhanh: `0.0.0.0/0` (KHÔNG dùng production)  

> 🔑 Lưu ý: Nếu không thêm source đúng, WorkBench sẽ **timeout** khi connect RDS.

---

## 🧱 4. Cấu Hình Database
- **Initial database name**: `aws_todolist_database`
- **Backup retention period**: `1 day`
- **Monitoring**: Off (nếu dùng Free Tier)

Nhấn **Create database**.

---

## 🕒 5. Chờ Khởi Tạo Hoàn Tất
Sau vài phút, trạng thái instance chuyển thành ✅ **Available**

Lưu lại:
- **Endpoint** (VD):  
  `aws-todolist-db.xxxxxx.ap-southeast-1.rds.amazonaws.com`
- **Port**: `3306`

---

## 🧪 6. Kết Nối Thử (CLI hoặc Workbench)

### **Linux / macOS**
```bash
mysql -h <endpoint> -u admin -p
