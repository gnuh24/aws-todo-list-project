import pandas as pd
import joblib
import random
import re
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.svm import LinearSVC
from sklearn.calibration import CalibratedClassifierCV
from sklearn.pipeline import Pipeline
from sklearn.metrics import classification_report

# =============================
# 1. CẤU HÌNH
# =============================
DATA_FILE = './data_v4.csv' 
LABEL_MODEL_FILE = 'model_label.joblib'
PRIORITY_MODEL_FILE = 'model_priority.joblib'

# Hệ số nhân bản: Tăng dữ liệu lên gấp 100 lần để model học sâu hơn
AUGMENT_FACTOR = 100 

# Từ điển đồng nghĩa (Synonyms)
synonyms = {
    "Fix": ["Resolve", "Debug", "Repair", "Patch", "Troubleshoot", "Check"],
    "Add": ["Implement", "Integrate", "Setup", "Create", "Include", "Code", "Dev"],
    "Create": ["Build", "Setup", "Generate", "Construct", "Design", "Code"],
    "Implement": ["Add", "Develop", "Execute", "Apply", "Integrate", "Code"],
    "Improve": ["Optimize", "Enhance", "Refine", "Boost", "Upgrade"],
    "Config": ["Configure", "Setup", "Initialize", "Set"],
}

# Từ khóa khẩn cấp để tiêm vào dữ liệu
URGENT_PREFIXES = ["URGENT:", "CRITICAL:", "EMERGENCY:", "HOTFIX:", "FATAL:", "BLOCKER:"]

# =============================
# 2. HÀM CHUẨN HÓA DỮ LIỆU
# =============================
def standardize_labels(df):
    def map_label(label):
        label = str(label).strip()
        if label in ['Cloud', 'AWS', 'Deployment', 'Infrastructure']: return 'DevOps'
        if label in ['Mobile', 'UI/UX', 'Design', 'CSS']: return 'Frontend'
        if label in ['Refactor', 'General', 'Documentation', 'AI', 'AI Integration', 'Feature', 'API']: return 'Backend'
        return label
    
    print("🧹 Đang quy hoạch lại nhãn Label...")
    df['label'] = df['label'].apply(map_label)
    return df

# =============================
# 3. DATA AUGMENTATION THÔNG MINH
# =============================
def augment_text_synonym(text):
    """Thay thế từ đồng nghĩa"""
    if not isinstance(text, str): return ""
    words = text.split(' ')
    if not words: return text
    
    first_word = words[0]
    if first_word in synonyms and random.random() > 0.3:
        new_word = random.choice(synonyms[first_word])
        return text.replace(first_word, new_word, 1)
    return text

def augment_dataframe(df_input):
    augmented_rows = []
    print(f"   ↳ Đang nhân bản {len(df_input)} dòng lên {AUGMENT_FACTOR} lần...")
    
    for _ in range(AUGMENT_FACTOR):
        for _, row in df_input.iterrows():
            title = row['title']
            desc = row['description']
            prio = row['priority']
            label = row['label']

            # 1. Augment cơ bản (Thay từ đồng nghĩa)
            new_title = augment_text_synonym(title)
            
            # 2. KỸ THUẬT TIÊM PRIORITY (QUAN TRỌNG)
            # Với 20% xác suất, biến một task bất kỳ thành CRITICAL
            # Bất kể nó là CSS hay Documentation
            if random.random() < 0.2: 
                prefix = random.choice(URGENT_PREFIXES)
                new_title = f"{prefix} {new_title}"
                prio = "Critical" # Ép nhãn thành Critical
            
            # 3. Gộp Text
            full_text = new_title + " " + desc
            
            augmented_rows.append({
                'text': full_text,
                'label': label,
                'priority': prio
            })
            
    return pd.DataFrame(augmented_rows)

def create_nlp_pipeline():
    return Pipeline([
        ('tfidf', TfidfVectorizer(max_features=20000, ngram_range=(1, 3))),
        ('clf', CalibratedClassifierCV(LinearSVC(class_weight='balanced', dual=False), cv=3))
    ])

# =============================
# 4. MAIN FLOW
# =============================
if __name__ == "__main__":
    print(f"⏳ Đang đọc file gốc: {DATA_FILE}")
    try:
        df = pd.read_csv(DATA_FILE, encoding='utf-8-sig')
    except FileNotFoundError:
        print("❌ Lỗi file!")
        exit()

    # Preprocess
    df['title'] = df['title'].fillna('')
    df['description'] = df['description'].fillna('')
    df['priority'] = df['priority'].str.capitalize()
    
    # Sửa lỗi typo 'low' thành 'Low' nếu còn sót
    df['priority'] = df['priority'].replace('low', 'Low')

    df = standardize_labels(df)
    
    # Lọc bỏ nhãn rác
    counts = df['label'].value_counts()
    rare = counts[counts < 2].index
    if len(rare) > 0: df = df[~df['label'].isin(rare)]

    # Chia tập Train/Test
    # Lưu ý: Stratify theo Priority để đảm bảo tập test có đủ Critical
    train_df, test_df = train_test_split(df, test_size=0.2, random_state=42, stratify=df['priority'])

    print(f"✂️  Đã chia tập dữ liệu: Train ({len(train_df)}) - Test ({len(test_df)})")

    # Nhân bản tập Train (Áp dụng chiến thuật tiêm Priority)
    print("🔄 Bắt đầu nhân bản và tiêm dữ liệu...")
    train_df_aug = augment_dataframe(train_df)
    
    test_df['text'] = test_df['title'] + " " + test_df['description']

    # --- TRAIN LABEL ---
    print("\n🔧 TRAINING MODEL LABEL...")
    label_pipeline = create_nlp_pipeline()
    label_pipeline.fit(train_df_aug['text'], train_df_aug['label'])
    print("📊 REPORT LABEL (Test Set):")
    print(classification_report(test_df['label'], label_pipeline.predict(test_df['text']), zero_division=0))
    joblib.dump(label_pipeline, LABEL_MODEL_FILE)

    # --- TRAIN PRIORITY ---
    print("\n🔧 TRAINING MODEL PRIORITY...")
    priority_pipeline = create_nlp_pipeline()
    priority_pipeline.fit(train_df_aug['text'], train_df_aug['priority'])
    print("📊 REPORT PRIORITY (Test Set):")
    print(classification_report(test_df['priority'], priority_pipeline.predict(test_df['text']), zero_division=0))
    joblib.dump(priority_pipeline, PRIORITY_MODEL_FILE)

    print(f"\n✅ Done! Hãy chạy lại test_full_suite_v2.py")