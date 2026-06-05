Set-Location $PSScriptRoot

if (-not $env:JAVA_HOME) {
    $env:JAVA_HOME = "C:\Users\USER\.jdks\temurin-17.0.18"
}

Write-Host "Using Java: $env:JAVA_HOME"
Write-Host "Starting Online Toy Store on http://localhost:8080/"
& "$PSScriptRoot\mvnw.cmd" jetty:run
