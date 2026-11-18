# Desgaste e manutencao preventiva

O desgaste e persistido com cada robo no arquivo da central. Robos de arquivos antigos comecam com desgaste zero.

## Regras

- Escala de 0 a 100; a partir de 80, o robo precisa de manutencao e nao pode iniciar outra missao.
- Cada operacao iniciada acrescenta 10 pontos ao ser concluida, cancelada em andamento ou interrompida por falha.
- Risco MEDIO acrescenta 5 pontos; ALTO acrescenta 10.
- Drone: camera termal acrescenta 5 pontos; altitude da area a partir de 200 m acrescenta mais 5.
- Terrestre: terreno IRREGULAR acrescenta 10 pontos; ALAGADO acrescenta 5.
- Cancelamento de missao ainda pendente nao provoca desgaste.
- Recarga nao reduz desgaste. Robos em manutencao nao sao recarregados pela recarga coletiva.
- A opcao 20 do menu permite selecionar o robo pelo ID para realizar manutencao.
- A manutencao zera o desgaste e preserva a bateria: o robo fica DISPONIVEL se tiver energia ou SEM_BATERIA se estiver zerado.
- Um robo em missao nao pode receber manutencao.
- Na reatribuicao por falha, cada robo acumula apenas o desgaste da sua propria participacao.

## Demonstracao

Concluir oito missoes de risco BAIXO com um drone sem camera termal, em altitude inferior a 200 m, leva o desgaste de 0 a 80. A oitava missao e concluida normalmente, mas o robo entra em EM_MANUTENCAO. Uma nova missao e bloqueada ate a manutencao pela opcao 20. A listagem de robos mostra o desgaste atual.
