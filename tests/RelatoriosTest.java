import Operacional.*;
import Robos.*;
import Area.*;
import Enum.*;
import Main.Menu;
import java.io.*;
import java.nio.file.*;
import java.util.*;
public class RelatoriosTest {
 static int n;
 static void check(boolean b){if(!b)throw new AssertionError("Teste "+(n+1));n++;}
 static String captura(Runnable r){PrintStream anterior=System.out;ByteArrayOutputStream out=new ByteArrayOutputStream();try{System.setOut(new PrintStream(out));r.run();}finally{System.setOut(anterior);}return out.toString();}
 static Missao missao(String id,StatusMissao s){Missao m=new Missao();m.setId(id);m.setDescricao("Descricao "+id);m.setTipo(TipoMissao.RECONHECIMENTO);TestFixtures.status(m,s);return m;}
 public static void main(String[] args)throws Exception {
 CentralResgate c=new CentralResgate();TestFixtures.areas(c).clear();TestFixtures.robos(c).clear();TestFixtures.missoes(c).clear();TestFixtures.pendentes(c).clear();
 c.gerarRelatorioGeral();Path geral=Path.of("src/Saves/relatorio_central.txt");String texto=Files.readString(geral);check(texto.contains("Total de robôs: 0")&&texto.contains("Total de missões: 0"));
 for(int i=0;i<2;i++){Drone d=new Drone();d.setId("D"+i);c.cadastrarRobo(d);}
 for(StatusMissao s:StatusMissao.values())TestFixtures.missoes(c).add(missao(s.name(),s));
 TestFixtures.pendentes(c).add(c.getMissoes().get(2)); // Fila divergente: a contagem deve usar o status real.
 Relatorio relatorio=new Relatorio(c);check(relatorio.getTotalRobos()==2&&relatorio.getTotalMissoes()==4&&relatorio.getMissoesConcluidas()==1&&relatorio.getMissoesPendentes()==1);
 c.gerarRelatorioGeral();texto=Files.readString(geral);check(texto.contains("Total de robôs: 2")&&texto.contains("Total de missões: 4")&&texto.contains("Missões concluídas: 1")&&texto.contains("Missões pendentes: 1"));
 Missao concluida=c.getMissoes().stream().filter(m->m.getStatus()==StatusMissao.CONCLUIDA).findFirst().orElseThrow();AreaOperacao a=new AreaOperacao();a.setNome("Centro");TestFixtures.definir(concluida,"area",a);TestFixtures.responsavel(concluida,c.getRobos().get("D0"));concluida.getRoboResponsavel().setNome("drone01");TestFixtures.definir(concluida,"resultado","Busca finalizada");
 c.gerarRelatorioMissao(new Scanner("CONCLUIDA\n"));Path individual=Path.of("src/Saves/relatorio_missao_CONCLUIDA.txt");texto=Files.readString(individual);check(texto.contains("Área: Centro")&&texto.contains("drone01")&&texto.contains("Busca finalizada"));
 c.gerarRelatorioMissoes();Path todas=Path.of("src/Saves/relatorios_missoes.txt");texto=Files.readString(todas);for(StatusMissao s:StatusMissao.values())check(texto.contains("ID: "+s));check(texto.contains("Não informada")&&texto.contains("Não atribuído"));
 String anterior=Files.readString(individual);check(captura(()->c.gerarRelatorioMissao(new Scanner("INEXISTENTE\n"))).contains("Missao nao encontrada"));check(Files.readString(individual).equals(anterior));
 TestFixtures.status(c.getMissoes().get(0),StatusMissao.CONCLUIDA);c.gerarRelatorioGeral();check(Files.readString(geral).contains("Missões concluídas: 2"));
 Menu.centralResgate=c;String saida=captura(()->new Menu().MenuStart(new Scanner("17\n18\nCONCLUIDA\n19\n0\n")));check(saida.contains("Relatorio salvo")&&saida.contains("Relatorio da missao salvo"));
 TestFixtures.definir(concluida,"id","../fora");new Relatorio().salvarEmArquivo(concluida);check(Files.exists(Path.of("src/Saves/relatorio_missao____fora.txt")));
 TestFixtures.missoes(c).clear();check(captura(c::gerarRelatorioMissoes).contains("Nenhuma missao cadastrada"));check(captura(()->c.gerarRelatorioMissao(new Scanner(""))).contains("Nenhuma missao cadastrada"));
 Files.delete(geral);Files.createDirectory(geral);check(captura(c::gerarRelatorioGeral).contains("Erro ao salvar relatorio"));
 System.out.println(n+" verificacoes de relatorios passaram.");
 }
}
