# Executar os testes

Na raiz do projeto, com o JDK no PATH:

```powershell
powershell -ExecutionPolicy Bypass -File tests/ExecutarTestes.ps1
```

O script compila a aplicacao e os testes, executa cada suite em uma pasta isolada em tmp, salva os logs e um resumo JSON e verifica que os arquivos reais de dados e mensagens nao mudaram.

Tres suites usam uma copia do central.dat de demonstracao, com 10 robos, 10 missoes e 10 areas. O arquivo real nao e sobrescrito.

TestFixtures e exclusivo dos testes: prepara estados deliberadamente invalidos ou historicos por reflexao para testar validacoes e relatorios. A aplicacao nao depende dele. EncapsulamentoTest verifica as restricoes dos setters e das colecoes usando a API publica.

A suite BateriaTest verifica o multiplicador de altitude limitado a 2^30 (a partir de 6000 m) e o consumo por minutos completos. Missoes abaixo de um minuto cobram apenas o consumo fixo, conforme a regra escolhida. O script retorna codigo 1 se qualquer teste falhar.
