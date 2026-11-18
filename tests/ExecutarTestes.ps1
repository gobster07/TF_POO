$ErrorActionPreference = 'Stop'
$raizProjeto = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$execucao = Join-Path $raizProjeto ('tmp/testes-completos-' + [guid]::NewGuid())
$classes = Join-Path $execucao 'classes'
New-Item -ItemType Directory -Force $classes | Out-Null
$fontes = @(Get-ChildItem (Join-Path $raizProjeto 'src'),$PSScriptRoot -Filter '*.java' -Recurse | ForEach-Object FullName)
& javac -encoding UTF-8 -d $classes $fontes
if ($LASTEXITCODE -ne 0) { throw 'A compilacao falhou.' }
$protegidos = @(Get-ChildItem (Join-Path $raizProjeto 'src/Saves') -File -Recurse | Get-FileHash)
$historico = Join-Path $raizProjeto 'src/Mensagens/mensagens.txt'
if (Test-Path -LiteralPath $historico) { $protegidos += Get-FileHash -LiteralPath $historico }
$resultados = @()
foreach ($arquivoTeste in (Get-ChildItem $PSScriptRoot -Filter '*Test.java' | Sort-Object Name)) {
    $suite = $arquivoTeste.BaseName
    $pastaTeste = Join-Path $execucao $suite
    New-Item -ItemType Directory -Force $pastaTeste | Out-Null
    if ($suite -in @('MapRobosTest', 'IntegridadeCadastroTest', 'EncapsulamentoTest')) {
        $savesTeste = Join-Path $pastaTeste 'src/Saves'
        New-Item -ItemType Directory -Force $savesTeste | Out-Null
        Copy-Item -LiteralPath (Join-Path $raizProjeto 'src/Saves/central.dat') -Destination (Join-Path $savesTeste 'central.dat')
    }
    Push-Location $pastaTeste
    try {
        $saida = @(& java -cp $classes $suite 2>&1)
        $codigo = $LASTEXITCODE
    } finally { Pop-Location }
    $saida | Set-Content -Encoding utf8 (Join-Path $execucao ($suite + '.txt'))
    $ultimaLinha = [string]($saida | Select-Object -Last 1)
    $passaram = 0
    $falharam = 0
    if ($ultimaLinha -match '(\d+) passaram; (\d+) falharam') {
        $passaram = [int]$Matches[1]
        $falharam = [int]$Matches[2]
    } elseif ($ultimaLinha -match '^(\d+) .*passaram') {
        $passaram = [int]$Matches[1]
    } elseif ($codigo -ne 0) { $falharam = 1 }
    $resultados += [pscustomobject]@{ Suite=$suite; Codigo=$codigo; Passaram=$passaram; Falharam=$falharam; Resultado=$ultimaLinha }
    Write-Output ($suite + ': ' + $ultimaLinha)
    $saida | Select-String 'FALHOU:' | ForEach-Object { Write-Output $_.Line }
}
foreach ($arquivoProtegido in $protegidos) {
    if ((Get-FileHash -LiteralPath $arquivoProtegido.Path).Hash -ne $arquivoProtegido.Hash) {
        throw ('Arquivo real alterado: ' + $arquivoProtegido.Path)
    }
}
$resultados | ConvertTo-Json -Depth 3 | Set-Content -Encoding utf8 (Join-Path $execucao 'resultados.json')
Write-Output ('TOTAL: ' + ($resultados.Passaram | Measure-Object -Sum).Sum + ' passaram; ' + ($resultados.Falharam | Measure-Object -Sum).Sum + ' falharam.')
Write-Output ('Relatorios: ' + $execucao)
if (@($resultados | Where-Object Codigo -ne 0).Count -gt 0) { exit 1 }
