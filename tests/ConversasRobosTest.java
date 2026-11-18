import Area.AreaOperacao;
import Enum.*;
import Operacional.Missao;
import Robos.*;
import Mensagens.ConversasRobos;
import java.nio.file.*;
import java.util.*;
public class ConversasRobosTest {
 static int n;
 static void check(boolean b){if(!b)throw new AssertionError("Teste "+(n+1));n++;}
 static Missao missao(String id){AreaOperacao a=new AreaOperacao();a.setHorario(Horario.DIA);a.setTipoTerreno(TipoTerreno.URBANO);Missao m=new Missao();m.setId(id);m.setArea(a);m.setTipo(TipoMissao.RECONHECIMENTO);m.setStatus(StatusMissao.PENDENTE);return m;}
 public static void main(String[] args)throws Exception{
 Path arquivo=Path.of("src/Mensagens/mensagens.txt");
 String[] esperadas={"Confirme o recebimento das instrucoes.","Informe a situacao da operacao.","Confirme o funcionamento dos sistemas.","Confirme o recebimento das instrucoes."};
 for(int i=0;i<4;i++){
 Robo r=i%2==0?new Drone("D"+i,"drone",100,StatusRobo.DISPONIVEL,100,false):new RoboTerrestre("R"+i,"terrestre",100,StatusRobo.DISPONIVEL,Roda.NORMAL,10,true);
 Missao m=missao("M"+i);m.executar(r);
 List<String> linhas=Files.readAllLines(arquivo);check(linhas.size()==(i+1)*6);
 int inicio=i*6+2;check(linhas.get(inicio).contains(esperadas[i]));
 for(int j=0;j<4;j++){String linha=linhas.get(inicio+j);check(linha.contains("Missao M"+i+": "));check(linha.contains(j%2==0?"Central -> ":" -> Central: "));}
 m.cancelar();
 // Desconsidera apenas os avisos de cancelamento nas verificacoes da rodada seguinte.
 List<String> atual=Files.readAllLines(arquivo);Files.write(arquivo,atual.subList(0,atual.size()-2));
 }
 long tamanho=Files.size(arquivo);boolean erro=false;try{ConversasRobos.conversar(missao("PENDENTE"));}catch(IllegalStateException e){erro=true;}check(erro&&Files.size(arquivo)==tamanho);
 Missao falha=missao("FALHA");falha.getArea().setHorario(Horario.NOITE);falha.executar(new Drone());check(Files.size(arquivo)==tamanho);
 System.out.println(n+" verificacoes das conversas passaram.");
 }
}
