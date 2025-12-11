# ======================================================
# 🚀 AUTO DEPLOY SCRIPT: api-gateway (AWS ECS/Fargate)
# ======================================================

# Cấu hình Encoding để hiển thị tiếng Việt có dấu
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# ====== 1. CẤU HÌNH CHUNG ======
$AWS_ACCOUNT_ID = "031133710884"
$AWS_REGION = "ap-southeast-1"
$CLUSTER_NAME = "SGUTodolist-Cluster"

# ====== 2. THÔNG TIN DỊCH VỤ (API GATEWAY) ======
# 👇 ĐÃ CẬP NHẬT TẠI ĐÂY
$SERVICE_NAME = "api-gateway"
$TASK_DEFINITION_NAME = "api-gateway-td"
$LOG_GROUP_NAME = "/ecs/api-gateway-td"

# ====== 3. CẤU HÌNH ECR ======
# Lưu ý: Đảm bảo bạn đã tạo Repository tên 'api-gateway' trên ECR
$ECR_REPO_URI = "$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$SERVICE_NAME"
$IMAGE_TAG = "latest"

# Lấy thư mục hiện tại (nơi chứa file script này)
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition

Write-Host "======================================================"
Write-Host "🚀 BẮT ĐẦU AUTO DEPLOY: $SERVICE_NAME"
Write-Host "📂 Thư mục build: $scriptDir"
Write-Host "======================================================"

# ======================================================
# BƯỚC 1: BUILD & PUSH DOCKER IMAGE
# ======================================================
Write-Host "📦 1/3. Đóng gói & Đẩy Docker Image..." -ForegroundColor Cyan

# 1.1 Login ECR
Write-Host "   -> Đang đăng nhập vào ECR..."
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

# 1.2 Build Image (Sử dụng Dockerfile tại thư mục hiện tại)
Write-Host "   -> Đang build Docker Image..."
docker build -t $SERVICE_NAME $scriptDir

# 1.3 Tag Image
Write-Host "   -> Đang gắn thẻ (Tag) Image..."
docker tag "${SERVICE_NAME}:latest" "${ECR_REPO_URI}:${IMAGE_TAG}"

# 1.4 Push Image
Write-Host "   -> Đang đẩy lên ECR..."
docker push "${ECR_REPO_URI}:${IMAGE_TAG}"

Write-Host "✅ Docker image đã được đẩy thành công: ${ECR_REPO_URI}:${IMAGE_TAG}" -ForegroundColor Green

# ======================================================
# BƯỚC 2: CẬP NHẬT ECS SERVICE
# ======================================================
Write-Host "`n🚀 2/3. Cập nhật ECS Service (Force Deployment)..." -ForegroundColor Cyan

# Lệnh này ép ECS pull image mới nhất về và thay thế task cũ
$updateResult = aws ecs update-service `
    --cluster $CLUSTER_NAME `
    --service $SERVICE_NAME `
    --force-new-deployment `
    --no-cli-pager `
    --region $AWS_REGION

Write-Host "✅ Đã gửi lệnh Deploy lên ECS. Hệ thống đang khởi động Task mới..." -ForegroundColor Green

# ======================================================
# BƯỚC 3: KIỂM TRA TRẠNG THÁI (MONITORING)
# ======================================================
Write-Host "`n❤️ 3/3. Kiểm tra sức khỏe Service..." -ForegroundColor Cyan
Write-Host "⏳ Đang chờ 30 giây để Task mới khởi động..."
Start-Sleep -Seconds 30

# 3.1 Kiểm tra Task đang chạy
Write-Host "`n🔍 Trạng thái Task ECS:"
$tasks = aws ecs list-tasks --cluster $CLUSTER_NAME --service-name $SERVICE_NAME --region $AWS_REGION | ConvertFrom-Json
if ($tasks.taskArns.Count -gt 0) {
    $tasks.taskArns | ForEach-Object { Write-Host "   - Task đang chạy (ARN): $_" -ForegroundColor Yellow }
} else {
    Write-Host "❌ Chưa có Task nào đang chạy (Có thể đang pending hoặc bị crash)" -ForegroundColor Red
}

# 3.2 Kiểm tra Logs (Xem có lỗi khởi động không)
Write-Host "`n📄 10 dòng Logs mới nhất từ CloudWatch ($LOG_GROUP_NAME):"
try {
    $logStreams = aws logs describe-log-streams --log-group-name $LOG_GROUP_NAME --order-by LastEventTime --descending --limit 1 --region $AWS_REGION | ConvertFrom-Json
    if ($logStreams.logStreams.Count -gt 0) {
        $latestStream = $logStreams.logStreams[0].logStreamName
        Write-Host "   Log Stream: $latestStream" -ForegroundColor Gray
        $logs = aws logs get-log-events --log-group-name $LOG_GROUP_NAME --log-stream-name $latestStream --limit 10 --region $AWS_REGION | ConvertFrom-Json
        $logs.events | ForEach-Object { Write-Host "   [$($_.timestamp)] $($_.message)" }
    } else {
        Write-Host "⚠️ Không tìm thấy Log Stream. Task có thể chưa kịp ghi log." -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Lỗi khi đọc logs. Kiểm tra lại tên Log Group." -ForegroundColor Red
}

# 3.3 Kiểm tra Target Group Health (Quan trọng nhất)
Write-Host "`n🎯 Kiểm tra Target Group (ALB Health Check):"
try {
    # Tự động tìm Target Group ARN gắn với Service này
    $serviceDetails = aws ecs describe-services --cluster $CLUSTER_NAME --services $SERVICE_NAME --region $AWS_REGION | ConvertFrom-Json
    
    if ($serviceDetails.services[0].loadBalancers.Count -gt 0) {
        $tgArn = $serviceDetails.services[0].loadBalancers[0].targetGroupArn
        
        # Lấy trạng thái sức khỏe
        $tgHealth = aws elbv2 describe-target-health --target-group-arn $tgArn --region $AWS_REGION | ConvertFrom-Json
        
        if ($tgHealth.TargetHealthDescriptions.Count -gt 0) {
            $tgHealth.TargetHealthDescriptions | ForEach-Object { 
                $state = $_.TargetHealth.State
                $color = if ($state -eq "healthy") { "Green" } else { "Red" }
                $targetId = $_.Target.Id
                Write-Host "   Target: $targetId | Trạng thái: $state" -ForegroundColor $color
                
                if ($_.TargetHealth.Reason) {
                    Write-Host "      Lý do: $($_.TargetHealth.Reason) - $($_.TargetHealth.Description)" -ForegroundColor Red
                }
            }
        } else {
            Write-Host "⚠️ Target Group chưa có IP nào đăng ký (Task chưa lên)." -ForegroundColor Yellow
        }
    } else {
        Write-Host "⚠️ Service này không được gắn với Load Balancer." -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Lỗi khi kiểm tra Target Group." -ForegroundColor Red
}

Write-Host "`n======================================================"
Write-Host "✅ DEPLOY HOÀN TẤT!"
Write-Host "======================================================"