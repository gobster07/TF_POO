package Main;

import Operacional.CentralResgate;

import java.util.Scanner;

public class Menu {

    public static Scanner in = new Scanner(System.in);

    public static CentralResgate centralResgate = new CentralResgate();

    public void MenuStart(Scanner in){
        while (true) {
            System.out.println("***MENU***");
            System.out.println("Escolha uma opção");
            System.out.println("**************");
            System.out.println("1 - Ver Missões ativas");
            System.out.println("2 - Ver todas as missões");
            System.out.println("3 - Ver Robos");
            System.out.println("4 - Ver Areas");
            System.out.println("0 - Sair");
            if (!in.hasNextLine()) {
                return;
            }
            try {
                int opcao = Integer.parseInt(in.nextLine().trim());
                if (opcao == 0) {
                    return;
                }
                Menu(opcao);
            } catch (NumberFormatException e) {
                System.out.println("Digite opção valida!");
            }
        }
    }

    public void Menu(int op){
        switch (op){
            case 1:
                centralResgate.lerMissoesPendentes();
                break;
            case 2:
                centralResgate.lerMissoes();
                break;
            case 3:
                centralResgate.lerRobos();
                break;
            case 4:
                centralResgate.lerAreas();
                break;
            default:
                System.out.println("Digite opção valida!");
        }
    }
}
