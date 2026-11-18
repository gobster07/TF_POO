import Operacional.*;
import Robos.*;
import Enum.*;
import Area.*;
import Save.Salvamento;
import java.util.*;
import java.io.*;
public class MapRobosTest {
 static int n;
 static void check(boolean ok){if(!ok)throw new AssertionError("Teste "+(n+1));n++;}
 static Drone drone(String id){return new Drone(id,"Drone",100,StatusRobo.DISPONIVEL,100,false);}
 static void rejeita(Runnable acao){boolean erro=false;try{acao.run();}catch(IllegalArgumentException e){erro=true;}check(erro);}
 public static void main(String[] args)throws Exception {
 CentralResgate c=new Salvamento().carregarCentral();check(c.getRobos().size()==10&&c.getMissoes().size()==10&&c.getAreas().size()==10);
 for(var e:c.getRobos().entrySet())check(e.getKey().equals(e.getValue().getId()));
 c.getRobos().clear();c.getMissoes().clear();c.getMissoesPendentes().clear();c.getAreas().clear();
 Drone d=drone("D1");c.cadastrarRobo(d);check(c.getRobos().get("D1")==d);check(c.getRobos().get("X")==null);
 rejeita(()->c.cadastrarRobo(drone("D1")));check(c.getRobos().get("D1")==d&&c.getRobos().size()==1);
 for(String id:new String[]{null,"","  "})rejeita(()->c.cadastrarRobo(drone(id)));
 rejeita(()->c.cadastrarRobo(null));
 AreaOperacao a=new AreaOperacao();a.setHorario(Horario.DIA);a.setTipoTerreno(TipoTerreno.URBANO);c.getAreas().add(a);
 Missao m=new Missao();m.setId("M1");m.setArea(a);m.setTipo(TipoMissao.RECONHECIMENTO);m.setStatus(StatusMissao.PENDENTE);c.getMissoes().add(m);c.getMissoesPendentes().add(m);
 c.executarMissao(new Scanner("M1\nD1\n"));check(m.getRoboResponsavel()==d&&m.getStatus()==StatusMissao.EM_ANDAMENTO&&c.getMissoesPendentes().isEmpty());
 new Salvamento().gravarCentral(c);CentralResgate salva=new Salvamento().carregarCentral();check(salva.getRobos().size()==1&&salva.getRobos().get("D1").getNome().equals("Drone"));check(salva.getMissoes().get(0).getRoboResponsavel()==salva.getRobos().get("D1"));check(salva.getMissoes().get(0).getArea()==salva.getAreas().get(0));
 Map<String,Robo> novos=new LinkedHashMap<>();novos.put("chave",drone("D2"));c.setRobos(novos);check(c.getRobos().containsKey("D2")&&!c.getRobos().containsKey("chave"));
 rejeita(()->c.setRobos(null));
 Map<String,Robo> duplicados=new LinkedHashMap<>();duplicados.put("a",drone("D3"));duplicados.put("b",drone("D3"));rejeita(()->c.setRobos(duplicados));check(c.getRobos().containsKey("D2"));
 c.getRobos().get("D2").setBateria(0);c.getRobos().get("D2").setStatus(StatusRobo.SEM_BATERIA);c.recarregarRobos();check(c.getRobos().get("D2").getBateria()==100);
 System.out.println(n+" verificacoes de Map e compatibilidade passaram.");
 }
}
