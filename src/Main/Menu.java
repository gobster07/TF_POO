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
            System.out.println("5 - Cadastrar Robo");
            System.out.println("6 - Cadastrar Area");
            System.out.println("7 - Cadastrar Missao");
            System.out.println("0 - Sair");
            if (!in.hasNextLine()) {
                return;
            }
            try {
                int opcao = Integer.parseInt(in.nextLine().trim());
                if (opcao == 0) {
                    return;
                }
                Menu(opcao, in);
            } catch (NumberFormatException e) {
                System.out.println("Digite opção valida!");
            }
        }
    }

    public void Menu(int op){
        Menu(op, in);
    }

    public void Menu(int op, Scanner in){
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
            case 5:
                centralResgate.cadastroRobo(in);
                break;
            case 6:
                centralResgate.registroArea(in);
                break;
            case 7:
                centralResgate.cadastroMissao(in);
                break;
            default:
                System.out.println("Digite opção valida!");
        }
    }
}
