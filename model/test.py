import joblib
import os
import sys
import time
import random

# --- CẤU HÌNH ---
MODEL_LABEL_PATH = 'model_label.joblib'
MODEL_PRIORITY_PATH = 'model_priority.joblib'
AUTO_TEST_COUNT = 20  # Số lượng case tự động muốn gen

# --- TỪ ĐIỂN TỰ ĐỘNG SINH DỮ LIỆU ---
VOCAB = {
    "BACKEND": {
        "keywords": ["API", "Java", "Controller", "Service", "Endpoint", "JWT", "Spring Boot", "Login"],
        "actions": ["Implement", "Fix logic", "Refactor", "Optimize", "Update"],
        "contexts": ["returning 500 error", "is slow", "throws exception", "fails validation"]
    },
    "FRONTEND": {
        "keywords": ["CSS", "React", "Button", "Layout", "Mobile", "UI", "Navbar", "Modal"],
        "actions": ["Align", "Fix style", "Redesign", "Responsive", "Animate"],
        "contexts": ["looks broken", "is not centered", "overlaps text", "glitches on scroll"]
    },
    "DEVOPS": {
        "keywords": ["Docker", "AWS", "Pipeline", "Deploy", "S3", "Kubernetes", "CI/CD", "Server"],
        "actions": ["Config", "Setup", "Fix build", "Restart", "Scale"],
        "contexts": ["failed to build", "connection refused", "timeout", "is unreachable"]
    },
    "SECURITY": {
        "keywords": ["XSS", "SQL Injection", "Hacker", "Vulnerability", "Token", "Firewall"],
        "actions": ["Patch", "Block", "Secure", "Fix breach", "Rotate"],
        "contexts": ["exploit detected", "data leak risk", "unauthorized access", "weak password"]
    },
    "DATABASE": {
        "keywords": ["SQL", "Query", "Table", "Column", "Migration", "Backup", "Index"],
        "actions": ["Add", "Alter", "Backup", "Restore", "Optimize"],
        "contexts": ["is missing", "query too slow", "data corruption", "schema mismatch"]
    }
}

PRIORITY_VOCAB = {
    "CRITICAL": ["Crash", "System Down", "Exploit", "Emergency", "Data Loss", "Breach", "Urgent"],
    "HIGH": ["Error", "Fail", "Timeout", "Bug", "Broken"],
    "MEDIUM": ["Warning", "Slow", "Incorrect", "Glitch"],
    "LOW": ["Typo", "Color", "Icon", "Spacing", "Rename"]
}

# --- BỘ TEST CASE TĨNH (25 cases cũ) ---
TEST_SUITE = [
    {"t": "Create user API", "d": "Write controller for user registration.", "l": "BACKEND", "p": "HIGH"},
    {"t": "Fix null pointer", "d": "Java application crashes with NPE.", "l": "BACKEND", "p": "MEDIUM"},
    {"t": "System Down", "d": "Server 500 error, all services unavailable.", "l": "DEVOPS", "p": "CRITICAL"},
    {"t": "SQL Injection", "d": "Vulnerability found in login form.", "l": "SECURITY", "p": "CRITICAL"},
]

def load_model(path):
    if not os.path.exists(path):
        print(f"❌ Lỗi: Không tìm thấy file {path}")
        return None
    return joblib.load(path)

def predict(model, text):
    pred = model.predict([text])[0]
    conf = model.predict_proba([text]).max() * 100
    return pred, conf

def generate_random_case():
    """Hàm tự động sinh Test Case ngẫu nhiên"""
    # 1. Chọn Label và Priority ngẫu nhiên
    target_label = random.choice(list(VOCAB.keys()))
    target_prio = random.choice(list(PRIORITY_VOCAB.keys()))
    
    # 2. Lấy từ vựng tương ứng
    l_data = VOCAB[target_label]
    
    keyword = random.choice(l_data["keywords"])
    action = random.choice(l_data["actions"])
    context = random.choice(l_data["contexts"])
    
    # Lấy từ khóa priority (để model nhận diện được độ ưu tiên)
    prio_keyword = random.choice(PRIORITY_VOCAB[target_prio])
    
    # 3. Ghép thành câu (Title & Desc)
    # Title: "Fix Docker Crash" hoặc "Implement API"
    if random.random() > 0.5:
        title = f"{action} {keyword} {prio_keyword}"
    else:
        title = f"{prio_keyword}: {action} {keyword}"
        
    desc = f"The {keyword} {context}. Please check {prio_keyword} issue."
    
    return {"t": title, "d": desc, "l": target_label, "p": target_prio}

def print_row(t, l_res, p_res, status):
    print(f"{t:<30} | {l_res:<40} | {p_res:<40} | {status}")

def main():
    os.system('cls' if os.name == 'nt' else 'clear')
    print("\n" + "="*120)
    print(f"{'🤖 HỆ THỐNG KIỂM THỬ AI TỰ ĐỘNG (AUTO-GENERATED TEST SUITE)':^120}")
    print("="*120)

    model_l = load_model(MODEL_LABEL_PATH)
    model_p = load_model(MODEL_PRIORITY_PATH)
    
    if not model_l or not model_p: sys.exit(1)
    
    score_label = 0
    score_priority = 0
    
    # Gộp Static Suite + Generated Suite
    full_suite = TEST_SUITE.copy()
    
    print(f"🔄 Đang sinh thêm {AUTO_TEST_COUNT} test case ngẫu nhiên...")
    for _ in range(AUTO_TEST_COUNT):
        full_suite.append(generate_random_case())
        
    total = len(full_suite)
    
    # Header bảng
    print(f"{'TEST CASE':<30} | {'LABEL (Pred / Expect)':<40} | {'PRIORITY (Pred / Expect)':<40} | {'STT'}")
    print("-" * 120)

    for i, case in enumerate(full_suite):
        text = f"{case['t']} {case['d']}"
        
        # Dự đoán
        l_pred, l_conf = predict(model_l, text)
        p_pred, p_conf = predict(model_p, text)
        
        # Chuẩn hóa
        l_exp = case['l'].upper()
        p_exp = case['p'].upper()
        l_act = l_pred.upper()
        p_act = p_pred.upper()
        
        l_ok = l_act == l_exp
        p_ok = p_act == p_exp
        
        if l_ok: score_label += 1
        if p_ok: score_priority += 1
        
        # Tô màu hiển thị
        l_color = "\033[92m" if l_ok else "\033[91m" # Xanh / Đỏ
        p_color = "\033[92m" if p_ok else "\033[91m"
        
        l_disp = f"{l_color}{l_act} ({l_conf:.0f}%)\033[0m / {l_exp}"
        p_disp = f"{p_color}{p_act} ({p_conf:.0f}%)\033[0m / {p_exp}"
        
        status = "✅" if (l_ok and p_ok) else "❌"
        
        # In từng dòng (Delay nhẹ để tạo hiệu ứng chạy)
        title_short = (case['t'][:27] + '..') if len(case['t']) > 27 else case['t']
        print_row(title_short, l_disp, p_disp, status)
        time.sleep(0.02)

    print("-" * 120)
    print(f"📊 TỔNG KẾT:")
    
    acc_l = score_label/total*100
    acc_p = score_priority/total*100
    
    color_l = "\033[92m" if acc_l > 80 else "\033[93m"
    color_p = "\033[92m" if acc_p > 80 else "\033[93m"
    
    print(f"   🏷️  LABEL ACCURACY:    {color_l}{score_label}/{total} ({acc_l:.1f}%)\033[0m")
    print(f"   🔥 PRIORITY ACCURACY: {color_p}{score_priority}/{total} ({acc_p:.1f}%)\033[0m")
    print("="*120)

if __name__ == "__main__":
    main()