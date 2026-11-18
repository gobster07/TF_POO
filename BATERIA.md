# Regras de consumo da bateria

- O custo fixo depende do risco da area: BAIXO = 10, MEDIO = 25, ALTO = 50 pontos de bateria.
- O tempo e contabilizado em minutos completos: horas = minutos / 60. Segundos incompletos nao entram no calculo.
- Missoes com menos de um minuto pagam apenas o custo fixo.
- Terrestre: custo fixo + consumo por hora * horas.
- Drone: custo fixo + (consumo por hora + consumo da camera termal, quando equipada) * horas * multiplicador de altitude.
- O multiplicador dobra a cada 200 metros: 1 abaixo de 200 m, 2 a partir de 200 m, 4 a partir de 400 m, etc.
- Aos 6000 m, o multiplicador chega a 2^30 (1073741824), a ultima potencia de dois positiva que cabe em int. A partir dessa altitude ele fica limitado a esse valor, inclusive em 6200 m, evitando o estouro do inteiro.
- Esse teto limita o fator de consumo, sem alterar a verificacao de alcance maximo do drone.
- O desconto final e arredondado para cima; a bateria permanece entre 0 e 100.
