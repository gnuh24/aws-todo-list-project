# ======================================================
# 🚀 SCRIPT DEPLOY FRONTEND V2.1 (ROBUST FAIL-FAST)
# ======================================================

# ====== 1. CẤU HÌNH DỰ ÁN (HARDCODED) ======
$AWS_REGION = "ap-southeast-1"
$S3_BUCKET = "sgutodolist-frontend-sg"           # Tên Bucket S3
$CF_DISTRIBUTION_ID = "E1H1BEAD7RHIDY"           # ID CloudFront
$BUILD_DIR = "./build"                          # Thư mục chứa code đã build

# Khởi tạo danh sách lỗi
$FailedSteps = @()

Write-Host "======================================================"
Write-Host "🚀 BẮT ĐẦU TRIỂN KHAI FRONTEND TỰ ĐỘNG"
Write-Host "   -> Bucket: $S3_BUCKET"
Write-Host "   -> CloudFront ID: $CF_DISTRIBUTION_ID"
Write-Host "======================================================"

# ======================================================
# BƯỚC 1: KIỂM TRA, CÀI ĐẶT THƯ VIỆN & BUILD CODE
# ======================================================
Write-Host "🛠️ 1/3. Kiểm tra & Build Frontend Code..." -ForegroundColor Cyan

# 1.1 Cài đặt Dependencies
Write-Host "   -> Đang cài đặt/cập nhật Dependencies (npm install)..."
npm install
if ($LASTEXITCODE -ne 0) {
    $FailedSteps += "NPM Install Failed"
    Write-Host "❌ Lỗi NPM Install. Dừng triển khai." -ForegroundColor Red
    exit 1
}

# 1.2 Build Code
Write-Host "   -> Đang tiến hành build Production (npm run build)..."
npm run build
if ($LASTEXITCODE -ne 0) {
    $FailedSteps += "NPM Build Failed"
    Write-Host "❌ Lỗi NPM Build. Dừng triển khai." -ForegroundColor Red
    exit 1
}
Write-Host "✅ Build Code hoàn tất. Kết quả trong thư mục '$BUILD_DIR'." -ForegroundColor Green


# ======================================================
# BƯỚC 2: UPLOAD CODE LÊN S3 (ĐÃ LOẠI BỎ --ACL)
# ======================================================
Write-Host "`n📦 2/3. Đồng bộ hóa code lên S3..." -ForegroundColor Cyan

try {
    # Lệnh S3 Sync (Không dùng cờ --acl public-read vì Bucket Policy đã lo)
    # Tải lên các file tĩnh (cache dài 1 năm)
    aws s3 sync $BUILD_DIR "s3://$S3_BUCKET" `
        --delete `
        --cache-control "max-age=31536000" `
        --exclude "*.html" `
        --region $AWS_REGION
    
    # Tải lên các file HTML (cache ngắn 5 phút)
    aws s3 sync $BUILD_DIR "s3://$S3_BUCKET" `
        --exclude "*" --include "*.html" `
        --cache-control "max-age=300" `
        --region $AWS_REGION

    Write-Host "✅ Đồng bộ S3 hoàn tất: s3://$S3_BUCKET" -ForegroundColor Green

} catch {
    # Dùng catch để bắt các lỗi AWS CLI (như Network, quyền truy cập)
    Write-Host "❌ LỖI S3 SYNC: Không thể upload code." -ForegroundColor Red
    $FailedSteps += "S3 Sync Failed (Vui lòng kiểm tra AWS CLI/Quyền s3:PutObject)."
    exit 1
}


# ======================================================
# BƯỚC 3: TẠO INVALIDATION TRÊN CLOUDFRONT
# ======================================================
Write-Host "`n🗑️ 3/3. Yêu cầu xóa cache CloudFront..." -ForegroundColor Cyan

try {
    $InvalidationPaths = "/*"
    
    $invalidationResult = aws cloudfront create-invalidation `
        --distribution-id $CF_DISTRIBUTION_ID `
        --paths $InvalidationPaths `
        --query 'Invalidation.Id' `
        --region $AWS_REGION

    $invalidationId = $invalidationResult | Out-String | Select-Object -First 1

    Write-Host "✅ Yêu cầu Invalidation đã được gửi." -ForegroundColor Green
    Write-Host "   -> ID Invalidation: $invalidationId" -ForegroundColor Yellow
    Write-Host "   -> Quá trình xóa cache sẽ mất 5-15 phút." -ForegroundColor Yellow

} catch {
    Write-Host "❌ LỖI INVALIDATION: Không thể gửi yêu cầu xóa cache." -ForegroundColor Red
    $FailedSteps += "CloudFront Invalidation Failed (Kiểm tra ID hoặc quyền CloudFront)."
    exit 1
}


# ======================================================
# TỔNG KẾT
# ======================================================
Write-Host "`n======================================================"
if ($FailedSteps.Count -eq 0) {
    Write-Host "🎉 TRIỂN KHAI FRONTEND HOÀN TẤT THÀNH CÔNG!" -ForegroundColor Green
} else {
    Write-Host "❌ TRIỂN KHAI GẶP VẤN ĐỀ. VUI LÒNG KIỂM TRA LẠI CÁC BƯỚC FAILED." -ForegroundColor Red
    Write-Host "--- DANH SÁCH BƯỚC FAILED ---" -ForegroundColor Red
    $FailedSteps | ForEach-Object { Write-Host "   - $_" -ForegroundColor Red }
}
Write-Host "======================================================"