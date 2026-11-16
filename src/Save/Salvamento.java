package Save;

import Area.AreaOperacao;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Salvamento {
    private static final Path CAMINHO_AREAS = Path.of("src", "saves", "areas.dat");
    private static final Path CAMINHO_ROBOT = Path.of("src", "saves", "robots.dat");
    private static final Path CAMINHO_DRONE = Path.of("src", "saves", "drones.dat");

    public void gravarAreas(List<AreaOperacao> areas) throws IOException {
        Files.createDirectories(CAMINHO_AREAS.getParent());
        try (ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(CAMINHO_AREAS.toFile()))) {
            saida.writeObject(new ArrayList<>(areas));
        }
    }

    @SuppressWarnings("unchecked")
    public List<AreaOperacao> carregarAreas() throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(CAMINHO_AREAS.toFile()))) {
            return (List<AreaOperacao>) entrada.readObject();
        }
    }
}
