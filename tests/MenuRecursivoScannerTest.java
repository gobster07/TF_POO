import Main.Menu;
import Operacional.*;
import Robos.*;
import Enum.*;
import java.util.Scanner;
import java.io.*;
public class MenuRecursivoScannerTest {
 static int n;
 static void check(boolean b){if(!b)throw new AssertionError("Teste "+(n+1));n++;}
 static void limpar(){Menu.centralResgate=new CentralResgate();Menu.centralResgate.getRobos().clear();Menu.centralResgate.getAreas().clear();Menu.centralResgate.getMissoes().clear();Menu.centralResgate.getMissoesPendentes().clear();}
 static void menu(String texto){new Menu().MenuStart(new Scanner(texto));}
 public static void main(String[] args)throws Exception{
 PrintStream original=System.out;System.setOut(new PrintStream(OutputStream.nullOutputStream()));
 try{
 limpar();Scanner in=new Scanner("abc\n99\n2\n3\n0\nrestante\n");new Menu().MenuStart(in);check(in.nextLine().equals("restante"));check(true);
 menu("");menu("2\n");check(true);
 limpar();menu("6\nA1\nCentro\nRua principal\nDIA\nURBANO\nBAIXO\n10,5\n5\n1\nD1\nDrone nome composto\n2,5\n100,5\n1\n1,5\n5\n2\nR1\nTerrestre nome composto\n3.5\n1\n20,5\nNORMAL\n7\nM1\nBusca no centro\nRECONHECIMENTO\n1\nA1\n0\n");
 check(Menu.centralResgate.getAreas().size()==1&&Menu.centralResgate.getAreas().get(0).getAltitude()==10.5);
 check(Menu.centralResgate.getRobos().size()==2);
 Drone d=(Drone)Menu.centralResgate.getRobos().get("D1");check(d.getId().equals("D1")&&d.getNome().equals("Drone nome composto")&&d.getConsumoPorHora()==2.5&&d.getAltitudeMaxima()==100.5&&d.getConsumoCameraPorHora()==1.5);
 RoboTerrestre r=(RoboTerrestre)Menu.centralResgate.getRobos().get("R1");check(r.getId().equals("R1")&&r.getNome().equals("Terrestre nome composto")&&r.getCapacidadeCarga()==20.5);
 check(Menu.centralResgate.getMissoes().size()==1&&Menu.centralResgate.getMissoes().get(0).getId().equals("M1"));
 CentralResgate salva=new Save.Salvamento().carregarCentral();check(salva.getRobos().size()==2&&salva.getMissoes().size()==1);
 limpar();menu("5\nx\n3\n0\n");check(Menu.centralResgate.getRobos().isEmpty());
 limpar();menu("5\n1\nD2\nTeste\ninvalido\n3\n0\n");check(Menu.centralResgate.getRobos().isEmpty());
 limpar();menu("5\n2\nR2\nTeste\n1\n2\n3\n0\n");check(Menu.centralResgate.getRobos().isEmpty());
 limpar();menu("5\n1\nD3\nTeste\n");check(Menu.centralResgate.getRobos().isEmpty());
 limpar();Scanner anterior=Menu.in;try{Menu.in=new Scanner("2\n0\n");new Menu().MenuStart();check(!Menu.in.hasNextLine());}finally{Menu.in=anterior;}
 }finally{System.setOut(original);}
 System.out.println(n+" verificacoes de recursao e Scanner passaram.");
 }
}
