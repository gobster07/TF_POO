package Main;

import Operacional.CentralResgate;
import Save.Salvamento;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
 

public class Main {
    public static void main(String[] args) {
        Salvamento salvamento = new Salvamento();
        CentralResgate central;

        try {
            central = salvamento.carregarCentral();
            System.out.println("Central carregada com sucesso.");
        } catch (FileNotFoundException e) {
            central = criarCentralVazia();
            System.out.println("Nenhum salvamento encontrado. Uma central nova foi criada.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Não foi possível carregar a central: " + e.getMessage());
            return;
        }

        CentralResgate centralAtual = central;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                salvamento.gravarCentral(centralAtual);
                System.out.println("Central salva com sucesso.");
            } catch (IOException e) {
                System.err.println("Não foi possível salvar a central: " + e.getMessage());
            }
        }));

        // O menu e as operações do sistema serão executados usando esta central.
    }

    private static CentralResgate criarCentralVazia() {
        CentralResgate central = new CentralResgate();
        central.setRobos(new ArrayList<>());
        central.setAreas(new ArrayList<>());
        central.setMissoes(new ArrayList<>());
        central.setMissoesPendentes(new ArrayDeque<>());
        return central;
    }
}
