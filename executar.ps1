param(
    [ValidateSet('testar', 'iniciar')]
    [string]$Acao = 'testar'
)
$ErrorActionPreference = 'Stop'
$javaAnterior = $env:JAVA_HOME
$jdk = Get-ChildItem -Path (Join-Path $PSScriptRoot '.tools/jdk-*') -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
$maven = Join-Path $PSScriptRoot '.tools/apache-maven-3.9.9/bin/mvn.cmd'
if (-not (Test-Path -LiteralPath $maven)) {
    $comando = Get-Command mvn -ErrorAction SilentlyContinue
    if (-not $comando) { throw 'Instale JDK 17+ e Maven ou use Docker conforme o README.' }
    $maven = $comando.Source
}
Push-Location $PSScriptRoot
try {
    if ($jdk) { $env:JAVA_HOME = $jdk.FullName }
    if ($Acao -eq 'testar') {
        & $maven -B -ntp '-Dmaven.repo.local=.tools/repository' clean verify
    } else {
        & $maven -B -ntp '-Dmaven.repo.local=.tools/repository' spring-boot:run
    }
    $codigo = $LASTEXITCODE
} finally {
    $env:JAVA_HOME = $javaAnterior
    Pop-Location
}
exit $codigo