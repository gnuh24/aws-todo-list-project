# ======================================================
# 🚀 AUTO DEPLOY SCRIPT: auth-service (AWS ECS/Fargate)
# ======================================================

# ====== Cấu hình ======
$AWS_ACCOUNT_ID = "031133710884"
$AWS_REGION = "ap-southeast-1"
$CLUSTER_NAME = "SGUTodolist-Cluster"

# THÔNG TIN DỊCH VỤ CỤ THỂ
$SERVICE_NAME = "auth-service"
$TASK_DEFINITION_NAME = "auth-service-td" # Tên Task Definition Family
$LOG_GROUP_NAME = "/ecs/$TASK_DEFINITION_NAME" # Tên Log Group trong CloudWatch

# BIẾN THIẾT YẾU
$ECR_REPO_URI = "$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$SERVICE_NAME"
$IMAGE_TAG = "latest"

# ENVIRONMENT VARIABLES cho production
$ENV_VARS = @{
    MYSQL_URL = "jdbc:mysql://<YOUR-RDS-ENDPOINT>:3306/dbname"
    MYSQL_USER = "<DB_USERNAME>"
    MYSQL_PASSWORD = "<DB_PASSWORD>"
    REDIS_HOST = "<REDIS_HOST>"
    REDIS_PORT = "<REDIS_PORT>"
    REDIS_PASSWORD = "<REDIS_PASSWORD>"
    KAFKA_SERVERS = "<KAFKA_BOOTSTRAP_SERVERS>"
}

# Lấy thư mục script
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition

Write-Host "======================================================"
Write-Host "🚀 BẮT ĐẦU AUTO DEPLOY: $SERVICE_NAME"
Write-Host "======================================================"

# ======================================================
# 1/ Build Docker image
# ======================================================
Write-Host "1/3. Build Docker Image (Multi-stage, không cần Maven local)"

# Login vào ECR
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

# Build Docker image
docker build -t $SERVICE_NAME $scriptDir

# Tag image theo ECR URI (chú ý dùng ${})
docker tag "${SERVICE_NAME}:latest" "${ECR_REPO_URI}:${IMAGE_TAG}"

# Push image lên ECR
docker push "${ECR_REPO_URI}:${IMAGE_TAG}"

Write-Host "✅ Docker image đã được push: ${ECR_REPO_URI}:${IMAGE_TAG}"

# ======================================================
# 2/ Force new ECS deployment
# ======================================================
Write-Host "2/3. Force new ECS deployment"

# Cập nhật ECS service với image mới
$updateResult = aws ecs update-service `
    --cluster $CLUSTER_NAME `
    --service $SERVICE_NAME `
    --force-new-deployment `
    --no-cli-pager `
    --region $AWS_REGION

Write-Host "✅ ECS deployment đã được thực hiện"

# ======================================================
# 3/ Kiểm tra ECS Service & Logs
# ======================================================
Write-Host "3/3. Kiểm tra ECS Service: $SERVICE_NAME"

# Lấy task đang chạy
$tasks = aws ecs list-tasks --cluster $CLUSTER_NAME --service-name $SERVICE_NAME --region $AWS_REGION | ConvertFrom-Json
if ($tasks.taskArns.Count -gt 0) {
    Write-Host "✅ Task đang chạy:"
    $tasks.taskArns | ForEach-Object { Write-Host $_ }
} else {
    Write-Host "❌ Chưa có task nào chạy"
}

# Kiểm tra CloudWatch logs (10 dòng mới nhất)
try {
    Write-Host "📄 Kiểm tra logs CloudWatch ($LOG_GROUP_NAME): "
    $logStreams = aws logs describe-log-streams --log-group-name $LOG_GROUP_NAME --order-by LastEventTime --descending --limit 1 --region $AWS_REGION | ConvertFrom-Json
    if ($logStreams.logStreams.Count -gt 0) {
        $latestStream = $logStreams.logStreams[0].logStreamName
        $logs = aws logs get-log-events --log-group-name $LOG_GROUP_NAME --log-stream-name $latestStream --limit 10 --region $AWS_REGION | ConvertFrom-Json
        $logs.events | ForEach-Object { Write-Host $_.message }
    } else {
        Write-Host "❌ Không tìm thấy log stream!"
    }
} catch {
    Write-Host "❌ Không tìm thấy log group: $LOG_GROUP_NAME. Vui lòng kiểm tra lại tên."
}

# Kiểm tra Target Group (ALB)
Write-Host "🎯 Kiểm tra Target Group (ALB):"
try {
    $tgArn = (aws ecs describe-services --cluster $CLUSTER_NAME --services $SERVICE_NAME --region $AWS_REGION | ConvertFrom-Json).services[0].loadBalancers[0].targetGroupArn
    $tgHealth = aws elbv2 describe-target-health --target-group-arn $tgArn --region $AWS_REGION | ConvertFrom-Json
    $tgHealth.TargetHealthDescriptions | ForEach-Object { Write-Host "$($_.Target.Id)`t$($_.TargetHealth.State)" }
} catch {
    Write-Host "⚠️ Không tìm thấy thông tin Target Group. Service có thể đang ở trạng thái Provisioning."
}

Write-Host ""
Write-Host "======================================================"
Write-Host "✅ Kiểm tra hoàn tất"
Write-Host "======================================================"


# ================= Cách chạy =================
# 1. cd todolist-backend/auth-service
# 2. Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
# 3. .\deploy.ps1
