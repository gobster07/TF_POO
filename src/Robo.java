public abstract class Robo implements Comunicavel {
    private String id;
    private String nome;
    private float bateria;
    private StatusRobo status;
    private int missoesRealizadas;

    public Robo(String id, String nome, float bateria, StatusRobo status) {
        this.id = id;
        this.nome = nome;
        this.bateria = bateria;
        this.status = status;
        this.missoesRealizadas = 0;
    }

    public Robo(){
        this.id = null;
        this.nome = null;
        this.bateria = 0;
        this.status = StatusRobo.DISPONIVEL;
        this.missoesRealizadas = 0;
    }

    public abstract void executaMissao();

    @Override
    public void enviarMensagem(String mensagem) {
        System.out.println("Robo " + id + " (" + nome + ") enviou para a central: " + mensagem);
    }

    @Override
    public void receberMensagem(String mensagem) {
        System.out.println("Robo " + id + " (" + nome + ") recebeu da central: " + mensagem);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public float getBateria() {
        return bateria;
    }

    public void setBateria(float bateria) {
        this.bateria = bateria;
    }

    public StatusRobo getStatus() {
        return status;
    }

    public void setStatus(StatusRobo status) {
        this.status = status;
    }

    public int getMissoesRealizadas() {
        return missoesRealizadas;
    }

    public void setMissoesRealizadas(int missoesRealizadas) {
        this.missoesRealizadas = missoesRealizadas;
    }
}
