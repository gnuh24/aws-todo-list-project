# ======================================
#   Dockerfile Production for Flask AI
# ======================================

# 1) Base image nhẹ
FROM python:3.10-slim

# 2) Không buffer log
ENV PYTHONUNBUFFERED=1

# 3) Thư mục làm việc
WORKDIR /app

# 4) Copy requirements và cài đặt
COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

# 5) Copy toàn bộ source code + model vào container
COPY . .

# 6) Expose port app chạy
EXPOSE 9997

# 7) RUN PRODUCTION SERVER USING GUNICORN
# "app:app" = file app.py, biến app = Flask()
CMD ["gunicorn", "--workers=4", "--threads=2", "--bind=0.0.0.0:9997", "server:app"]