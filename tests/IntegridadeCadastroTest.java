import Operacional.*;
import Robos.*;
import Area.*;
import Enum.*;
import Save.Salvamento;
import java.io.*;
import java.util.*;
public class IntegridadeCadastroTest {
 static int n;
 static void check(boolean b){if(!b)throw new AssertionError("Verificacao "+(n+1));n++;}
 static void rejeita(Runnable r){boolean erro=false;try{r.run();}catch(IllegalArgumentException e){erro=true;}check(erro);}
 static String dados(String id,String horario,String terreno,String risco,String altitude){return id+"\nNome teste\nLocal\n"+horario+"\n"+terreno+"\n"+risco+"\n"+altitude+"\nDescricao da area\n";}
 public static void main(String[] args)throws Exception{
 CentralResgate c=new CentralResgate();TestFixtures.missoes(c).clear();TestFixtures.pendentes(c).clear();TestFixtures.areas(c).clear();TestFixtures.robos(c).clear();
 for(String entrada:new String[]{dados("A1","INVALIDO","URBANO","BAIXO","10"),dados("A1","DIA","INVALIDO","BAIXO","10"),dados("A1","DIA","URBANO","INVALIDO","10"),dados("A1","DIA","URBANO","BAIXO","abc"),dados("A1","DIA","URBANO","BAIXO","NaN"),dados("A1","DIA","URBANO","BAIXO","Infinity"),dados("A1","DIA","URBANO","BAIXO","-1"),dados("","DIA","URBANO","BAIXO","10"),dados("  ","DIA","URBANO","BAIXO","10"),"A1\nNome\n"}){
 c.registroArea(new Scanner(entrada));check(c.getAreas().isEmpty());}
 c.registroArea(new Scanner(dados(" A1 ","DIA","URBANO","BAIXO","10,5")));check(c.getAreas().size()==1&&c.getAreas().get(0).getId().equals("A1")&&c.getAreas().get(0).getAltitude()==10.5);
 AreaOperacao existente=c.getAreas().get(0);c.registroArea(new Scanner(dados("A1","NOITE","ALAGADO","ALTO","20")));check(c.getAreas().size()==1&&c.getAreas().get(0)==existente&&existente.getHorario()==Horario.DIA);
 rejeita(()->existente.cadastroArea(new Scanner(dados("NOVO","INVALIDO","URBANO","BAIXO","10"))));check(existente.getId().equals("A1")&&existente.getNome().equals("Nome teste"));
 Drone d=new Drone();d.setId("ANTES");d.setId("D1");check(d.getId().equals("D1"));c.cadastrarRobo(d);rejeita(()->d.setId("D2"));check(c.getRobos().get("D1")==d&&d.getId().equals("D1"));d.setId("D1");check(c.getRobos().get("D1")==d);rejeita(()->d.setId(null));rejeita(()->d.setId(" "));rejeita(()->d.cadastraRobo(new Scanner("D3\n")));check(c.getRobos().get("D1")==d);
 Drone d2=new Drone();d2.setId("D2");Map<String,Robo> novos=new LinkedHashMap<>();novos.put("D2",d2);c.setRobos(novos);rejeita(()->d2.setId("D3"));check(c.getRobos().get("D2")==d2);
 ByteArrayOutputStream bytes=new ByteArrayOutputStream();try(ObjectOutputStream out=new ObjectOutputStream(bytes)){out.writeObject(c);}try(ObjectInputStream in=new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))){CentralResgate salva=(CentralResgate)in.readObject();Robo salvo=salva.getRobos().get("D2");rejeita(()->salvo.setId("OUTRO"));check(salva.getRobos().get("D2")==salvo);}
 CentralResgate antiga=new Salvamento().carregarCentral();check(antiga.getRobos().size()==10&&antiga.getMissoes().size()==10&&antiga.getAreas().size()==10);Robo antigo=antiga.getRobos().values().iterator().next();String idAntigo=antigo.getId();rejeita(()->antigo.setId("ALTERADO"));check(antiga.getRobos().get(idAntigo)==antigo);
 System.out.println(n+" verificacoes de integridade do cadastro passaram.");
 }
}
