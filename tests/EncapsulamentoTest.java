import Area.AreaOperacao;
import Enum.*;
import Operacional.*;
import Robos.*;
import Save.Salvamento;
import java.io.*;
import java.util.*;
public class EncapsulamentoTest {
 static int n;
 static void check(boolean b){if(!b)throw new AssertionError("Teste "+(n+1));n++;}
 static void rejeita(Runnable r){boolean erro=false;try{r.run();}catch(IllegalArgumentException|IllegalStateException|UnsupportedOperationException e){erro=true;}check(erro);}
 static AreaOperacao area(String id){return new AreaOperacao(id,"Area","Centro",Horario.DIA,TipoTerreno.URBANO,NivelRisco.BAIXO,"Descricao",0);}
 static Missao missao(String id,AreaOperacao a){Missao m=new Missao();m.setId(id);m.setTipo(TipoMissao.RECONHECIMENTO);m.setArea(a);m.setConsumoFixoBateria(10);return m;}
 static CentralResgate copiar(CentralResgate c)throws Exception{ByteArrayOutputStream b=new ByteArrayOutputStream();try(ObjectOutputStream out=new ObjectOutputStream(b)){out.writeObject(c);}try(ObjectInputStream in=new ObjectInputStream(new ByteArrayInputStream(b.toByteArray()))){return (CentralResgate)in.readObject();}}
 public static void main(String[] args)throws Exception{
 CentralResgate antiga=new Salvamento().carregarCentral();check(antiga.getRobos().size()==10&&antiga.getMissoes().size()==10&&antiga.getAreas().size()==10);
 AreaOperacao antigaArea=antiga.getAreas().get(0);Missao antigaMissao=antiga.getMissoes().get(0);rejeita(()->antigaArea.setId("NOVO"));rejeita(()->antigaMissao.setId("NOVO"));
 CentralResgate c=new CentralResgate();c.setMissoes(List.of());c.setAreas(List.of());c.setRobos(Map.of());
 AreaOperacao a=area("A1");c.cadastrarArea(a);Drone d=new Drone("D1","Drone",100,StatusRobo.DISPONIVEL,100,false);c.cadastrarRobo(d);Missao m=missao("M1",a);c.registrarMissao(m);
 rejeita(()->c.getRobos().clear());rejeita(()->c.getAreas().add(area("A2")));rejeita(()->c.getMissoes().clear());Queue<Missao> fila=c.getMissoesPendentes();fila.clear();check(c.getMissoesPendentes().contains(m));
 rejeita(()->c.getRobos().put("X",d));rejeita(()->m.setId("M2"));rejeita(()->a.setId("A2"));rejeita(()->m.setStatus(StatusMissao.CONCLUIDA));rejeita(()->m.setArea(area("A2")));rejeita(()->m.setTipo(TipoMissao.TRANSPORTE_DE_SUPRIMENTOS));
 rejeita(()->c.setAreas(null));rejeita(()->c.setMissoes(null));rejeita(()->c.setMissoesPendentes(new LinkedList<>()));
 List<AreaOperacao> areas=new ArrayList<>(List.of(a));c.setAreas(areas);areas.clear();check(c.getAreas().size()==1);
 Map<String,Robo> robos=new LinkedHashMap<>();robos.put("D1",d);c.setRobos(robos);robos.clear();check(c.getRobos().get("D1")==d);
 List<Missao> missoes=new ArrayList<>(List.of(m));c.setMissoes(missoes);missoes.clear();check(c.getMissoes().contains(m));
 check(d.verificarDisponibilidade()&&d.consultarBateria()==100);rejeita(()->d.setStatus(null));rejeita(()->d.setStatus(StatusRobo.EM_MISSAO));rejeita(()->m.setStatus(null));rejeita(()->a.setHorario(null));rejeita(()->a.setNivelRisco(null));rejeita(()->a.setAltitude(Double.NaN));rejeita(()->d.setAltitudeMaxima(-1));rejeita(()->d.setMissoesRealizadas(20));
 Missao semResponsavel=missao("SEM",a);rejeita(semResponsavel::iniciar);
 c.atribuirRoboMissao("M1","D1");check(m.getRoboResponsavel()==d&&m.getStatus()==StatusMissao.PENDENTE&&d.verificarDisponibilidade());m.iniciar();check(m.getStatus()==StatusMissao.EM_ANDAMENTO&&d.getStatus()==StatusRobo.EM_MISSAO&&!d.verificarDisponibilidade());
 rejeita(()->d.setStatus(StatusRobo.DISPONIVEL));rejeita(()->m.setStatus(StatusMissao.PENDENTE));rejeita(()->m.setRoboResponsavel(new Drone()));rejeita(()->m.setConsumoFixoBateria(0));rejeita(()->d.atualizarStatusAposOperacao(m));rejeita(()->c.setMissoes(List.of()));rejeita(()->c.setRobos(Map.of()));
 Missao outra=missao("M2",a);c.registrarMissao(outra);rejeita(()->c.atribuirRoboMissao("M2","D1"));check(outra.getRoboResponsavel()==null&&c.getMissoesPendentes().size()==1);
 CentralResgate copia=copiar(c);Missao salva=copia.getMissoes().get(0);Robo salvo=copia.getRobos().get("D1");check(salva.getRoboResponsavel()==salvo&&!salvo.verificarDisponibilidade());rejeita(()->salvo.setStatus(StatusRobo.DISPONIVEL));salva.concluir();check(salvo.verificarDisponibilidade()&&salvo.getMissoesRealizadas()==1);
 m.concluir();check(m.getStatus()==StatusMissao.CONCLUIDA&&d.verificarDisponibilidade()&&d.getMissoesRealizadas()==1);rejeita(()->m.setStatus(StatusMissao.PENDENTE));rejeita(m::iniciar);
 outra.atribuirRobo(d);outra.iniciar();outra.cancelar();check(d.verificarDisponibilidade()&&outra.getStatus()==StatusMissao.CANCELADA);rejeita(()->outra.setStatus(StatusMissao.PENDENTE));
 d.setStatus(StatusRobo.EM_MANUTENCAO);rejeita(()->d.setStatus(StatusRobo.DISPONIVEL));d.realizarManutencao();check(d.verificarDisponibilidade());
 System.out.println(n+" verificacoes de encapsulamento e ciclo de missao passaram.");
 }
}
