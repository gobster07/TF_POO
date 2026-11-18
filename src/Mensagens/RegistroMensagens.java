package Mensagens;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroMensagens {
    private static final Path ARQUIVO = Path.of("src", "Mensagens", "mensagens.txt");

    public static void registrar(String remetente, String destinatario, String mensagem) throws IOException {
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("A mensagem nao pode estar vazia.");
        }
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String registro = "[" + data + "] " + remetente + " -> " + destinatario + ": "
                + mensagem.replaceAll("\\R", " ");
        Files.createDirectories(ARQUIVO.getParent());
        Files.writeString(ARQUIVO, registro + System.lineSeparator(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        System.out.println(registro);
    }
}
