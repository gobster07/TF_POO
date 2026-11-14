import java.io.Serializable;
import java.util.Scanner;

public class Drone extends Robo implements Comunicavel, Serializable {
    private float altitudeMaxima;

    private boolean thermal;

    public Drone(String id, String nome, float bateria, StatusRobo status, float altitudeMaxima,boolean thermal) {
        super(id, nome, bateria, status);
        this.altitudeMaxima = altitudeMaxima;
        this.thermal = thermal;
    }

    public Drone(){
        super();
        altitudeMaxima = 0;
        thermal = false;
    }

    public void carregar() {
        super.setBateria(100);
    }

    public float getAltitudeMaxima() {
        return altitudeMaxima;
    }

    public void setAltitudeMaxima(float altitudeMaxima) {
        this.altitudeMaxima = altitudeMaxima;
    }

    @Override
    public void cadastraRobo(Scanner in){
        try {
            super.cadastraRobo(in);
            System.out.println("Digite a altitude maxima: ");
            this.altitudeMaxima = in.nextFloat();
            System.out.println("O Drone possui camera termal?");
            System.out.println("1 - Sim");
            System.out.println("2 - Nao");
            this.thermal = in.nextBoolean();
        } catch (Exception e){
            System.out.println("Erro ao cadastrar robo");
        }
    }

    @Override
    public void executaMissao() {
    }

    @Override
    public void enviarMensagem(String mensagem) {
    }

    @Override
    public void receberMensagem(String mensagem) {
    }
}
