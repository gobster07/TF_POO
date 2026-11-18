package Main;

import Operacional.CentralResgate;

import java.util.Scanner;
import java.util.InputMismatchException;

public class Menu {

    public static Scanner in = new Scanner(System.in);

    public static CentralResgate centralResgate = new CentralResgate();

    public void MenuStart(){
        MenuStart(in);
    }

    public void MenuStart(Scanner in){
        System.out.println("***MENU***");
        System.out.println("Escolha uma opção");
        System.out.println("**************");
        System.out.println("1 - Ver Missões pendentes");
        System.out.println("2 - Ver todas as missões");
        System.out.println("3 - Ver Robos");
        System.out.println("4 - Ver Areas");
        System.out.println("5 - Cadastrar Robo");
        System.out.println("6 - Cadastrar Area");
        System.out.println("7 - Cadastrar Missao");
        System.out.println("8 - Iniciar proxima Missao da fila");
        System.out.println("9 - Concluir Missao");
        System.out.println("10 - Cancelar Missao");
        System.out.println("11 - Recarregar Robos");
        System.out.println("12 - Conversar com Robo");
        System.out.println("13 - Registrar falha de Robo");
        System.out.println("14 - Atualizar risco de Area");
        System.out.println("15 - Ver Robos disponiveis");
        System.out.println("16 - Ver Missoes em andamento");
        System.out.println("17 - Relatorio geral da Central");
        System.out.println("18 - Relatorio de uma Missao");
        System.out.println("19 - Relatorio de todas as Missoes");
        System.out.println("20 - Realizar manutencao de Robo");
        System.out.println("0 - Salvar e sair");
        try {
            int opcao = in.nextInt();
            in.nextLine();
            Menu(opcao, in);

        } catch (InputMismatchException e) {
            in.nextLine();
            System.out.println("Digite opção valida!");
            MenuStart(in);
        } catch (NumberFormatException e) {
            System.out.println("Digite opção valida!");
            MenuStart(in);
        } catch (java.util.NoSuchElementException e) {
            System.out.println("Entrada encerrada.");
        }
    }

    public void Menu(int op, Scanner in){
        switch (op){
            case 1:
                centralResgate.lerMissoesPendentes();
                MenuStart(in);
                break;
            case 2:
                centralResgate.lerMissoes();
                MenuStart(in);
                break;
            case 3:
                centralResgate.lerRobos();
                MenuStart(in);
                break;
            case 4:
                centralResgate.lerAreas();
                MenuStart(in);
                break;
            case 5:
                centralResgate.cadastroRobo(in);
                MenuStart(in);
                break;
            case 6:
                centralResgate.registroArea(in);
                MenuStart(in);
                break;
            case 7:
                centralResgate.cadastroMissao(in);
                MenuStart(in);
                break;
            case 8:
                centralResgate.executarMissao(in);
                MenuStart(in);
                break;
            case 9:
                centralResgate.concluirMissao(in);
                MenuStart(in);
                break;
            case 10:
                centralResgate.cancelarMissao(in);
                MenuStart(in);
                break;
            case 11:
                centralResgate.recarregarRobos();
                MenuStart(in);
                break;
            case 12:
                centralResgate.conversarComRobo(in);
                MenuStart(in);
                break;
            case 13:
                centralResgate.registrarFalhaRobo(in);
                MenuStart(in);
                break;
            case 14:
                centralResgate.atualizarNivelRiscoArea(in);
                MenuStart(in);
                break;
            case 15:
                centralResgate.listarRobosDisponiveis();
                MenuStart(in);
                break;
            case 16:
                centralResgate.listarMissoesEmAndamento();
                MenuStart(in);
                break;
            case 17:
                centralResgate.gerarRelatorioGeral();
                MenuStart(in);
                break;
            case 18:
                centralResgate.gerarRelatorioMissao(in);
                MenuStart(in);
                break;
            case 19:
                centralResgate.gerarRelatorioMissoes();
                MenuStart(in);
                break;
            case 20:
                centralResgate.realizarManutencaoRobo(in);
                MenuStart(in);
                break;
            case 0:
                centralResgate.salvar();
                return;
            default:
                System.out.println("Digite opção valida!");
                MenuStart(in);
        }
    }

}
