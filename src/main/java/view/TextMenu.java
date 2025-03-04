package view;

import java.util.*;

public class TextMenu {
    private Map<String, Command> commands;
    public TextMenu() {
        commands = new LinkedHashMap<String, Command>();
    }

    public void addCommand(Command command) {
        commands.put(command.getKey(), command);
    }

    private void printMenu() {
        for (Command command : commands.values()) {
            System.out.println(command.getKey() + ": " + command.getDescription());
        }
    }

    public void show() {
        Scanner scanner = new Scanner(System.in);
        List<String> inputs = new ArrayList<String>();
        while (true) {
            printMenu();
            System.out.print("Your choice: ");
            String key = scanner.nextLine();
            if (!commands.containsKey(key)) {
                System.out.println("Invalid command");
            }
            else {
                if (inputs.contains(key)) {
                    System.out.println("Command already executed!");
                }
                else {
                    Command command = commands.get(key);
                    command.execute();
                    if (key.equals("1") || key.equals("2") || key.equals("3") || key.equals("4") || key.equals("5") || key.equals("6") || key.equals("7") || key.equals("8")) {
                        inputs.add(key);
                    }
                }
            }
        }
    }
}
