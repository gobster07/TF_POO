import Area.AreaOperacao;
import Enum.*;
import Operacional.*;
import Robos.*;
import java.time.LocalDateTime;
import java.lang.reflect.Field;

public class BateriaTest {
    static int passaram, falharam;
    static void check(boolean ok) { if (!ok) throw new AssertionError("Resultado incorreto"); }
    static void teste(String nome, Runnable acao) {
        try { acao.run(); passaram++; System.out.println("PASSOU: " + nome); }
        catch(Throwable e) { falharam++; System.out.println("FALHOU: " + nome + " -> " + e); }
    }
    static Drone drone() { Drone d=new Drone("D","Drone",100,StatusRobo.DISPONIVEL,10000,false); d.setConsumoPorHora(10); return d; }
    static Missao missao(double altura) {
        AreaOperacao a=new AreaOperacao(); a.setHorario(Horario.DIA); a.setTipoTerreno(TipoTerreno.URBANO); a.setAltitude(altura);
        Missao m=new Missao(); m.setArea(a); m.setStatus(StatusMissao.PENDENTE); m.setTipo(TipoMissao.RECONHECIMENTO); m.setConsumoFixoBateria(10); return m;
    }
    static void inicio(Missao m, LocalDateTime valor) { try {Field f=Missao.class.getDeclaredField("dataInicio");f.setAccessible(true);f.set(m,valor);}catch(Exception e){throw new RuntimeException(e);} }
    static void camera(Drone d, double valor) { try {Field f=Drone.class.getDeclaredField("consumoCameraPorHora");f.setAccessible(true);f.set(d,valor);}catch(Exception e){throw new RuntimeException(e);} }
    static void rejeita(Runnable acao) {boolean erro=false;try{acao.run();}catch(IllegalArgumentException e){erro=true;}check(erro);}
    public static void main(String[] args) {
        teste("Formula terrestre",()->{RoboTerrestre r=new RoboTerrestre();r.setConsumoPorHora(10);check(r.calculoBateria(missao(0),2)==30);});
        for(double altura:new double[]{0,199,200,399,400}) teste("Drone altitude "+altura,()->check(drone().calculoBateria(missao(altura),1)==10+10*Math.pow(2,(int)(altura/200))));
        teste("Camera termal adiciona consumo",()->{Drone d=new Drone("D","D",100,StatusRobo.DISPONIVEL,1000,true);d.setConsumoPorHora(10);camera(d,2);check(d.calculoBateria(missao(200),2)==58);});
        teste("Desconto fracionario arredonda para cima",()->{Drone d=drone();d.descontarBateria(2.1);check(d.getBateria()==97);});
        teste("Desconto exato esgota bateria",()->{Drone d=drone();d.descontarBateria(100);check(d.getBateria()==0&&d.getStatus()==StatusRobo.SEM_BATERIA);});
        teste("Desconto acima da bateria limita a zero",()->{Drone d=drone();d.descontarBateria(110);check(d.getBateria()==0);});
        teste("Inicio rejeita custo fixo insuficiente",()->{Drone d=drone();d.setBateria(9);Missao m=missao(0);m.executar(d);check(m.getStatus()==StatusMissao.PENDENTE&&d.getStatus()==StatusRobo.DISPONIVEL);});
        teste("Encerrar apos uma hora desconta 20",()->{Drone d=drone();Missao m=missao(0);m.executar(d);inicio(m,LocalDateTime.now().minusMinutes(60).minusSeconds(5));m.concluir();check(d.getBateria()==80&&m.getStatus()==StatusMissao.CONCLUIDA&&d.getStatus()==StatusRobo.DISPONIVEL);});
        teste("Encerrar sem bateria cancela",()->{Drone d=drone();d.setBateria(15);Missao m=missao(0);m.executar(d);inicio(m,LocalDateTime.now().minusMinutes(60).minusSeconds(5));m.concluir();check(d.getBateria()==0&&d.getStatus()==StatusRobo.SEM_BATERIA&&m.getStatus()==StatusMissao.CANCELADA);});
        teste("Encerrar duas vezes nao desconta novamente",()->{Drone d=drone();Missao m=missao(0);m.executar(d);m.concluir();int b=d.getBateria();boolean erro=false;try{m.concluir();}catch(IllegalStateException e){erro=true;}check(erro&&d.getBateria()==b);});
        teste("Consumo negativo nao pode aumentar bateria",()->{Drone d=drone();d.setBateria(50);try{d.descontarBateria(-10);}catch(IllegalArgumentException e){}check(d.getBateria()==50);});
        teste("Desconto NaN rejeitado",()->rejeita(()->drone().descontarBateria(Double.NaN)));
        teste("Desconto infinito rejeitado",()->rejeita(()->drone().descontarBateria(Double.POSITIVE_INFINITY)));
        teste("Taxa negativa rejeitada",()->rejeita(()->drone().setConsumoPorHora(-1)));
        teste("Taxa NaN rejeitada",()->rejeita(()->drone().setConsumoPorHora(Double.NaN)));
        teste("Custo fixo negativo rejeitado",()->rejeita(()->missao(0).setConsumoFixoBateria(-1)));
        teste("Horas negativas rejeitadas",()->rejeita(()->drone().calculoBateria(missao(0),-1)));
        teste("Camera negativa rejeitada no calculo",()->{Drone d=new Drone("D","D",100,StatusRobo.DISPONIVEL,1000,true);camera(d,-10);rejeita(()->d.calculoBateria(missao(0),1));});
        teste("Altitude 6200 nao gera consumo negativo",()->check(drone().calculoBateria(missao(6200),1)>0));
        teste("Encerramento registra resultado e contador",()->{Drone d=drone();Missao m=missao(0);m.executar(d);m.concluir();check(d.getMissoesRealizadas()==1&&m.getResultado()!=null&&!m.getResultado().isBlank());});
        teste("Trinta segundos geram consumo variavel",()->{Drone d=drone();d.setConsumoPorHora(120);Missao m=missao(0);m.executar(d);inicio(m,LocalDateTime.now().minusSeconds(30));m.concluir();check(d.getBateria()<90);});
        System.out.println("TOTAL: "+passaram+" passaram; "+falharam+" falharam.");if(falharam>0)System.exit(1);
    }
}
