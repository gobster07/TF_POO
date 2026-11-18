import Area.AreaOperacao;
import Enum.*;
import Operacional.Missao;
import Robos.*;

public class RoboTerrestreTest {
    static int total;
    static Missao missao() {
        AreaOperacao area=new AreaOperacao();area.setHorario(Horario.DIA);area.setTipoTerreno(TipoTerreno.URBANO);
        Missao m=new Missao();m.setArea(area);m.setTipo(TipoMissao.RECONHECIMENTO);TestFixtures.status(m,StatusMissao.PENDENTE);return m;
    }
    static RoboTerrestre robo(double carga) {return TestFixtures.terrestreCargaInvalida(carga);}
    static void verifica(RoboTerrestre r,Missao m,boolean permitido) {
        StatusMissao estado=m==null?null:m.getStatus();Robo responsavel=m==null?null:m.getRoboResponsavel();
        StatusRobo status=r.getStatus();int bateria=r.getBateria();boolean rejeitou=false;
        try {r.executaMissao(m);}catch(IllegalArgumentException e){rejeitou=true;}
        if(rejeitou==permitido || bateria!=r.getBateria() || status!=r.getStatus()
                || (m!=null && (m.getStatus()!=estado || m.getRoboResponsavel()!=responsavel))) throw new AssertionError("Cenario "+(total+1));
        total++;
    }
    public static void main(String[] args) {
        for(Horario h:Horario.values()) for(TipoTerreno t:TipoTerreno.values()) for(Roda roda:Roda.values())
            for(boolean flutua:new boolean[]{false,true}) for(TipoMissao tipo:TipoMissao.values()) {
                Missao m=missao();m.getArea().setHorario(h);m.getArea().setTipoTerreno(t);m.setTipo(tipo);
                RoboTerrestre r=new RoboTerrestre("R","Robo",100,StatusRobo.DISPONIVEL,roda,10,flutua);
                verifica(r,m,h==Horario.DIA && (t!=TipoTerreno.ALAGADO||flutua) && (t!=TipoTerreno.IRREGULAR||roda==Roda.OFFROAD));
            }
        verifica(robo(10),null,false);
        Missao m=missao();TestFixtures.definir(m,"area",null);verifica(robo(10),m,false);
        m=missao();TestFixtures.definir(m.getArea(),"horario",null);verifica(robo(10),m,false);
        m=missao();TestFixtures.definir(m.getArea(),"tipoTerreno",null);verifica(robo(10),m,false);
        m=missao();TestFixtures.definir(m,"tipo",null);verifica(robo(10),m,false);
        for(StatusMissao s:StatusMissao.values())if(s!=StatusMissao.PENDENTE){m=missao();TestFixtures.status(m,s);verifica(robo(10),m,false);}
        for(StatusRobo s:StatusRobo.values())if(s!=StatusRobo.DISPONIVEL){RoboTerrestre r=robo(10);TestFixtures.status(r,s);verifica(r,missao(),false);}
        RoboTerrestre r=robo(10);r.setBateria(0);verifica(r,missao(),false);
        m=missao();TestFixtures.responsavel(m,robo(10));verifica(robo(10),m,false);
        r=robo(10);m=missao();TestFixtures.responsavel(m,r);verifica(r,m,true);
        verifica(new RoboTerrestre("R","R",100,StatusRobo.DISPONIVEL,null,10,true),missao(),false);
        for(double carga:new double[]{0,-1,Double.NaN,Double.POSITIVE_INFINITY}){m=missao();m.setTipo(TipoMissao.TRANSPORTE_DE_SUPRIMENTOS);verifica(robo(carga),m,false);}
        r=robo(10);m=missao();m.executar(r);
        if(m.getStatus()!=StatusMissao.EM_ANDAMENTO || m.getRoboResponsavel()!=r || r.getStatus()!=StatusRobo.EM_MISSAO)throw new AssertionError("Integracao");total++;
        r=robo(10);m=missao();m.getArea().setTipoTerreno(TipoTerreno.IRREGULAR);
        RoboTerrestre semRodaAdequada=new RoboTerrestre("R","R",100,StatusRobo.DISPONIVEL,Roda.NORMAL,10,true);m.executar(semRodaAdequada);
        if(m.getStatus()!=StatusMissao.PENDENTE || m.getRoboResponsavel()!=null || semRodaAdequada.getStatus()!=StatusRobo.DISPONIVEL)throw new AssertionError("Falha alterou estado");total++;
        System.out.println(total+" cenarios do terrestre passaram.");
    }
}
