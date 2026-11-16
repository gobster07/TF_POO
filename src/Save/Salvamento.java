package Save;

import Area.AreaOperacao;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Salvamento {
    private static final Path CAMINHO_AREAS = Path.of("src", "saves", "areas.dat");

    public void gravarAreas(List<AreaOperacao> areas) throws IOException {
        Files.createDirectories(CAMINHO_AREAS.getParent());
        try (ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(CAMINHO_AREAS.toFile()))) {
            saida.writeObject(new ArrayList<>(areas));
        }
    }

    public List<AreaOperacao> carregarAreas() throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(CAMINHO_AREAS.toFile()))) {
            Object objeto = entrada.readObject();
            if (!(objeto instanceof List<?>)) {
                throw new IOException("O arquivo nao contem uma lista de areas.");
            }

            List<AreaOperacao> areas = new ArrayList<>();
            for (Object item : (List<?>) objeto) {
                if (!(item instanceof AreaOperacao)) {
                    throw new IOException("O arquivo contem um item que nao e uma area.");
                }
                areas.add((AreaOperacao) item);
            }
            return areas;
        }
    }
}
