// Main
package main;

import commands.*;
import model.Potiag;
import repository.SqliteVagonRepository;
import services.SkladService;
import utils.FileManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private final List<Command> commands = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);
    private final SkladService service;
    private final SqliteVagonRepository repository;

    public Main() {
        Potiag potiag = new Potiag("Lviv-Kyiv Express");
        repository = new SqliteVagonRepository();
        service = new SkladService(potiag, repository);

        // Завантаження складу з бази даних при старті
        System.out.println("📂 Завантаження даних з бази даних...");
        System.out.println("[INFO] Завантаження вагонів з SQLite БД.");
        service.zavantazhytyZBazy();

        commands.add(new AddVagonCommand(service));
        commands.add(new ShowSkladCommand(service));
        commands.add(new SortVagonsCommand(service));
        commands.add(new DeleteVagonCommand(service));
        commands.add(new EditVagonCommand(service));
        commands.add(new CalculatePasazhyryCommand(service));
        commands.add(new CalculateBagazhCommand(service));
        commands.add(new FindVagonCommand(service));
        commands.add(new SaveToFileCommand(service));
        commands.add(new LoadFromFileCommand(service));
        commands.add(new ExitCommand(service));
    }

    public void run() {
        System.out.println("[INFO] Запуск програми 'Управління потягом'");
        printWelcome();

        while (true) {
            printMenu();
            System.out.print("Ваш вибір: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("0")) {
                executeCommand("0");
                System.out.println("Зберігаю дані...");
                System.out.println("[INFO] Користувач ініціював завершення програми. Збереження даних.");
                try {
                    service.zberegtyUFile();
                } catch (Exception e) {
                    System.err.println("[ERROR] Помилка під час збереження даних при завершенні програми: " + e.getMessage());
                    e.printStackTrace();
                }
                repository.close();
                System.out.println("[INFO] Application finished");
                break;
            }

            executeCommand(choice);
            waitForEnter();
        }
    }

    private void printWelcome() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║   СИСТЕМА УПРАВЛІННЯ РУХОМИМ СКЛАДОМ ПОТЯГА      ║");
        System.out.println("║              Лабораторна робота № 6               ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void printMenu() {
        System.out.println("\n┌────────────────────────────────────────────────────┐");
        System.out.println("│          МЕНЮ КЕРУВАННЯ ПОТЯГОМ                    │");
        System.out.println("└────────────────────────────────────────────────────┘");
        for (Command cmd : commands) {
            System.out.println("  " + cmd.getDescription());
        }
        System.out.println("────────────────────────────────────────────────────");
    }

    private void executeCommand(String choice) {
        for (Command cmd : commands) {
            if (cmd.getDescription().startsWith(choice + ".")) {
                System.out.println("[INFO] Виконання команди: " + choice);
                try {
                    cmd.execute();
                } catch (Exception e) {
                    System.err.println("[ERROR] Помилка під час виконання команди: " + choice + " - " + e.getMessage());
                    e.printStackTrace();
                    System.out.println("Сталася помилка при виконанні команди: " + e.getMessage());
                }
                return;
            }
        }
        System.out.println("Невірний вибір!");
        System.out.println("[WARN] Невірний вибір користувача: '" + choice + "'");
    }

    private void waitForEnter() {
        System.out.println("\n[Натисніть Enter для продовження...]");
        scanner.nextLine();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}