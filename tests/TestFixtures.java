import java.lang.reflect.Field;
import java.util.*;
import Operacional.*;
import Area.AreaOperacao;
import Robos.Robo;
import Enum.*;
/** Constroi estados isolados, inclusive invalidos, sem reabrir a API da aplicacao. */
public class TestFixtures {
 public static Object campo(Object alvo,String nome){try{Field f=achar(alvo.getClass(),nome);f.setAccessible(true);return f.get(alvo);}catch(Exception e){throw new RuntimeException(e);}}
 public static void definir(Object alvo,String nome,Object valor){try{Field f=achar(alvo.getClass(),nome);f.setAccessible(true);f.set(alvo,valor);}catch(Exception e){throw new RuntimeException(e);}}
 static Field achar(Class<?> classe,String nome)throws Exception{while(classe!=null){try{return classe.getDeclaredField(nome);}catch(NoSuchFieldException e){classe=classe.getSuperclass();}}throw new NoSuchFieldException(nome);}
 @SuppressWarnings("unchecked") public static Map<String,Robo> robos(CentralResgate c){return (Map<String,Robo>)campo(c,"robos");}
 @SuppressWarnings("unchecked") public static List<AreaOperacao> areas(CentralResgate c){return (List<AreaOperacao>)campo(c,"areas");}
 @SuppressWarnings("unchecked") public static List<Missao> missoes(CentralResgate c){return (List<Missao>)campo(c,"missoes");}
 @SuppressWarnings("unchecked") public static Queue<Missao> pendentes(CentralResgate c){return (Queue<Missao>)campo(c,"missoesPendentes");}
 public static void status(Object alvo,Object valor){definir(alvo,"status",valor);}
 public static Robos.Drone droneAltitudeInvalida(double altura){Robos.Drone d=new Robos.Drone("D","Drone",100,StatusRobo.DISPONIVEL,100,false);definir(d,"altitudeMaxima",altura);return d;}
 public static Robos.RoboTerrestre terrestreCargaInvalida(double carga){Robos.RoboTerrestre r=new Robos.RoboTerrestre("R1","Terrestre",100,StatusRobo.DISPONIVEL,Roda.OFFROAD,1,true);definir(r,"capacidadeCarga",carga);return r;}
 public static void responsavel(Missao m,Robo r){definir(m,"roboResponsavel",r);}
}
