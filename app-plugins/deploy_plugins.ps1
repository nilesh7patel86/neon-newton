$ErrorActionPreference = "Stop"

# 1. Build Plugins
Write-Host "Building plugins..." -ForegroundColor Cyan
Push-Location ..
mvn clean package -pl app-plugins -am -DskipTests
$buildResult = $LASTEXITCODE
Pop-Location

if ($buildResult -ne 0) {
    Write-Error "Build failed!"
}

# 2. Clear Target Directory
$targetDir = "..\plugins"
if (Test-Path $targetDir) {
    Write-Host "Cleaning $targetDir..." -ForegroundColor Yellow
    Remove-Item -Path "$targetDir\*" -Recurse -Force
} else {
    New-Item -ItemType Directory -Path $targetDir | Out-Null
}

# 3. Copy Artifacts
Write-Host "Copying artifacts..." -ForegroundColor Green
$count = 0

# Find all jars in target folders of subdirectories, excluding the parent pom's target if any
Get-ChildItem -Path . -Directory | ForEach-Object {
    $pluginDir = $_.FullName
    $jarPath = "$pluginDir\target\*.jar"
    $zipPath = "$pluginDir\target\*.zip"
    
    # Copy JARs (excluding shaded/original- prefixed ones if need be, but usually default is fine)
    Get-ChildItem -Path $jarPath -ErrorAction SilentlyContinue | Where-Object { $_.Name -notmatch "^original-" -and $_.Name -notmatch "-shaded" } | ForEach-Object {
        Copy-Item -Path $_.FullName -Destination $targetDir
        Write-Host "  Deployed: $($_.Name)"
        $count++
    }
    
    # Copy ZIPs
    Get-ChildItem -Path $zipPath -ErrorAction SilentlyContinue | ForEach-Object {
        Copy-Item -Path $_.FullName -Destination $targetDir
        Write-Host "  Deployed: $($_.Name)"
        $count++
    }
}

Write-Host "Successfully deployed $count plugins to $targetDir" -ForegroundColor Cyan
