import Area.AreaOperacao;
import Enum.Horario;
import Enum.NivelRisco;
import Enum.StatusRobo;
import Enum.TipoMissao;
import Enum.TipoTerreno;
import Operacional.CentralResgate;
import Operacional.Missao;
import Robos.Drone;
import Save.Salvamento;

/** Gera os dados de teste na pasta isolada definida pelo executor. */
public class PrepararSalvamento {
    public static void main(String[] args) throws Exception {
        CentralResgate central = new CentralResgate();
        for (int i = 1; i <= 10; i++) {
            AreaOperacao area = new AreaOperacao("A" + i, "Area " + i, "Centro",
                    Horario.DIA, TipoTerreno.URBANO, NivelRisco.BAIXO, "Area de teste", 0);
            central.cadastrarArea(area);
            central.cadastrarRobo(new Drone("D" + i, "Drone " + i, 100,
                    StatusRobo.DISPONIVEL, 100, false));
            Missao missao = new Missao();
            missao.setId("M" + i);
            missao.setDescricao("Missao de teste " + i);
            missao.setTipo(TipoMissao.RECONHECIMENTO);
            missao.setArea(area);
            missao.setConsumoFixoBateria(10);
            central.registrarMissao(missao);
            central.atribuirRoboMissao(missao.getId(), "D" + i);
        }
        new Salvamento().gravarCentral(central);
    }
}
