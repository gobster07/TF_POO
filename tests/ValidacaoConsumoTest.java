import Area.AreaOperacao;
import Enum.*;
import Operacional.Missao;
import Robos.*;
public class ValidacaoConsumoTest {
 static int n;
 static void check(boolean b){if(!b)throw new AssertionError("Teste "+(n+1));n++;}
 static void rejeita(Runnable acao){boolean erro=false;try{acao.run();}catch(IllegalArgumentException e){erro=true;}check(erro);}
 public static void main(String[] args){
 Missao m=new Missao();m.setArea(new AreaOperacao());m.setConsumoFixoBateria(10);
 Drone d=new Drone();d.setBateria(50);d.setConsumoPorHora(10);d.setConsumoCameraPorHora(2);
 RoboTerrestre r=new RoboTerrestre();r.setConsumoPorHora(10);
 for(double invalido:new double[]{-1,Double.NaN,Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY}){
 rejeita(()->d.descontarBateria(invalido));check(d.getBateria()==50);
 rejeita(()->d.setConsumoPorHora(invalido));check(d.getConsumoPorHora()==10);
 rejeita(()->d.setConsumoCameraPorHora(invalido));check(d.getConsumoCameraPorHora()==2);
 rejeita(()->m.setConsumoFixoBateria(invalido));check(m.getConsumoFixoBateria()==10);
 rejeita(()->d.calculoBateria(m,invalido));rejeita(()->r.calculoBateria(m,invalido));
 }
 d.setConsumoPorHora(0);d.setConsumoCameraPorHora(0);m.setConsumoFixoBateria(0);
 check(d.calculoBateria(m,0)==0);check(r.calculoBateria(m,0)==0);
 d.descontarBateria(0);check(d.getBateria()==50);
 d.descontarBateria(Double.MAX_VALUE);check(d.getBateria()==0 && d.getStatus()==StatusRobo.SEM_BATERIA);
 System.out.println(n+" verificacoes de valores invalidos passaram.");
 }
}
