import joblib
import os
from flask import Flask, request, jsonify

# --- CẤU HÌNH ---
MODEL_LABEL_PATH = 'model_label.joblib'

# Khởi tạo App Flask
app = Flask(__name__)

# Biến lưu model
model_label = None

# --- LOAD MODEL (Chạy ngay khi khởi tạo app) ---
print("⏳ Đang load model Label...")
try:
    if os.path.exists(MODEL_LABEL_PATH):
        model_label = joblib.load(MODEL_LABEL_PATH)
        print("✅ Load model thành công! Sẵn sàng gán nhãn.")
    else:
        print(f"❌ Lỗi: Không tìm thấy file {MODEL_LABEL_PATH}")
except Exception as e:
    print(f"❌ Exception: {e}")

# --- API DỰ ĐOÁN ---
@app.route("/api/model/predict", methods=["POST"])
def predict_label():
    # 1. Kiểm tra model
    if not model_label:
        return jsonify({"error": "Model chưa sẵn sàng hoặc bị lỗi."}), 503

    # 2. Lấy dữ liệu từ JSON gửi lên
    # Flask không dùng Pydantic tự động, phải lấy thủ công
    data = request.get_json()
    
    if not data or 'title' not in data:
        return jsonify({"error": "Thiếu trường 'title' trong request"}), 400

    title = data['title']
    description = data.get('description', "") # Mặc định rỗng nếu không có

    # 3. Gộp text
    full_text = f"{title} {description}"
    
    try:
        # 4. Dự đoán Label (Logic cũ)
        pred = model_label.predict([full_text])[0]
        prob = model_label.predict_proba([full_text]).max() * 100
        
        # 5. Logic gợi ý Priority (Rule-based cũ)
        suggested_priority = "Medium"
        if pred.upper() == "SECURITY":
            suggested_priority = "High"
        elif "CRITICAL" in full_text.upper() or "URGENT" in full_text.upper():
            suggested_priority = "Critical"

        # 6. Trả về kết quả
        return jsonify({
            "status": "success",
            "task_preview": full_text[:50] + "...",
            "result": {
                "label": pred,
                "confidence": f"{prob:.2f}%",
                "suggestion": {
                    "priority": suggested_priority,
                    "reason": "Based on Label & Keywords rule"
                }
            }
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500

# --- HEALTH CHECK ---
@app.route("/health", methods=["GET"])
def health():
    status = "ok" if model_label is not None else "error"
    return jsonify({"status": status, "model_loaded": model_label is not None})

if __name__ == "__main__":
    # Chạy server Flask (Mặc định Flask dev server)
    # debug=True giúp tự reload khi sửa code (giống nodemon)
    app.run(host="0.0.0.0", port=9997, debug=True)