package Save;

import Operacional.CentralResgate;
import Area.AreaOperacao;
import Operacional.Missao;
import Robos.Robo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Salvamento {
    private static final Path CAMINHO_CENTRAL = Path.of("src", "Saves", "central.dat");
    private static final Path CAMINHO_AREAS = Path.of("src", "saves", "areas.dat");
    private static final Path CAMINHO_ROBOT = Path.of("src", "saves", "robots.dat");
    private static final Path CAMINHO_DRONE = Path.of("src", "saves", "drones.dat");
    private static final Path CAMINHO_MISSOES = Path.of("src", "saves", "missoes.dat");

    public void gravarCentral(CentralResgate central) throws IOException {
        Files.createDirectories(CAMINHO_CENTRAL.getParent());

        try (ObjectOutputStream saida = new ObjectOutputStream(
                new FileOutputStream(CAMINHO_CENTRAL.toFile()))) {
            saida.writeObject(central);
        }
    }

    public CentralResgate carregarCentral() throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrada = new ObjectInputStream(
                new FileInputStream(CAMINHO_CENTRAL.toFile()))) {
            return (CentralResgate) entrada.readObject();
        }
    }
    public void gravarRelatorio(String nomeArquivo, String conteudo) throws IOException {
        Path caminho = Path.of("src", "Saves", nomeArquivo);

        Files.createDirectories(caminho.getParent());
        Files.writeString(caminho, conteudo);
    }


    @SuppressWarnings("unchecked")
    public List<Robo> carregarRobos() throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(CAMINHO_ROBOT.toFile()))) {
            return (List<Robo>) entrada.readObject();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Missao> carregarMissoes() throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(CAMINHO_MISSOES.toFile()))) {
            return (List<Missao>) entrada.readObject();
        }
    }
}
