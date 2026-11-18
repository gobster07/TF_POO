package Mensagens;

import Enum.StatusMissao;
import Enum.StatusRobo;
import Operacional.Missao;
import Robos.Robo;

public class ConversasRobos {
    private static int proximaConversa = 0;

    private static final String[][] CONVERSAS = {
        {
            "Confirme o recebimento das instrucoes.",
            "Instrucoes recebidas. Estou atuando na area designada.",
            "Mantenha a central informada sobre o progresso.",
            "Entendido. Informarei qualquer alteracao durante a operacao."
        },
        {
            "Informe a situacao da operacao.",
            "Operacao em andamento. Monitorando as condicoes da area.",
            "Priorize a seguranca durante o deslocamento.",
            "Confirmado. Vou respeitar os limites do equipamento."
        },
        {
            "Confirme o funcionamento dos sistemas.",
            "Sistemas operacionais. Comunicacao com a central estabelecida.",
            "Continue a missao e avise se precisar de apoio.",
            "Entendido. Solicitarei apoio caso seja necessario."
        }
    };

    public static void conversar(Missao missao) {
        if (missao == null || missao.getStatus() != StatusMissao.EM_ANDAMENTO || missao.getRoboResponsavel() == null || missao.getRoboResponsavel().getStatus() != StatusRobo.EM_MISSAO) {
            throw new IllegalStateException("A conversa exige uma missao e um robo em operacao.");
        }
        Robo robo = missao.getRoboResponsavel();
        String[] conversa = CONVERSAS[proximaConversa];
        proximaConversa = (proximaConversa + 1) % CONVERSAS.length;
        String prefixo = "Missao " + missao.getId() + ": ";
        for (int i = 0; i < conversa.length; i++) {
            if (i % 2 == 0) {
                robo.receberMensagem(prefixo + conversa[i]);
            } else {
                robo.enviarMensagem(prefixo + conversa[i]);
            }
        }
    }
}
