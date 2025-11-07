# Simple MongoDB init script runner
$initScriptPath = Join-Path (Get-Location).Path "mongodb-init.js"
Write-Host "Init script path: $initScriptPath"

# Check if script exists
if (-not (Test-Path $initScriptPath)) {
    Write-Host "Error: Script not found!"
    exit 1
}

# MongoDB paths to check
$mongoPaths = @(
    "D:\Program Files\MongoDB\Server\8.2\bin",
    "C:\Program Files\MongoDB\Server\8.2\bin",
    "D:\Program Files\MongoDB\Server\7.0\bin",
    "C:\Program Files\MongoDB\Server\7.0\bin",
    "D:\Program Files\MongoDB\Server\6.0\bin",
    "C:\Program Files\MongoDB\Server\6.0\bin"
)

# Try mongosh first
foreach ($path in $mongoPaths) {
    $mongoshExe = Join-Path $path "mongosh.exe"
    if (Test-Path $mongoshExe) {
        Write-Host "Found mongosh: $mongoshExe"
        Write-Host "Running script with mongosh..."
        & "$mongoshExe" "$initScriptPath"
        exit 0
    }
}

# Try mongo if mongosh not found
foreach ($path in $mongoPaths) {
    $mongoExe = Join-Path $path "mongo.exe"
    if (Test-Path $mongoExe) {
        Write-Host "Found mongo: $mongoExe"
        Write-Host "Running script with mongo..."
        & "$mongoExe" "$initScriptPath"
        exit 0
    }
}

# If no client found
Write-Host "No MongoDB client found. Please install mongosh and run:"
Write-Host "mongosh $initScriptPath"
Write-Host "Or use Spring Boot app to initialize data automatically."
exit 1