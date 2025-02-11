package org.example.fiveletters.solving.cli.service;

import java.io.PrintStream;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class InputOutputService {

    private static final Set<Character> AVAILABLE_LETTER_STATUSES = Set.of('E', 'O', 'N');
    private static final Set<Character> AVAILABLE_LETTERS = Set.of(
        'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п',
        'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю','я'
    );

    private final Scanner in = new Scanner(System.in);
    private final PrintStream out = System.out;

    public Optional<String> readWordOptionally() {
        out.println("Input next word if want (or skip for optimal search)");
        String input = in.nextLine().toLowerCase();

        while (!isWordInputCorrect(input)) {
            System.out.println("Incorrect input, try again");
            input = in.nextLine().toLowerCase();
        }

        return Optional.of(input)
            .filter(v -> !v.isEmpty());
    }

    private boolean isWordInputCorrect(String input) {
        if (!input.isEmpty() && input.length() != 5) {
            return false;
        }

        for (int i = 0; i < input.length(); i++) {
            if (!AVAILABLE_LETTERS.contains(input.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public String readLettersStatus() {
        out.println("Input letter statuses (E - Exact position, O - Other position, N - Not present)");
        String input = in.nextLine().toUpperCase();

        while (!isLettersStatusCorrect(input)) {
            System.out.println("Incorrect input, try again");
            input = in.nextLine().toUpperCase();
        }

        return input;
    }

    private boolean isLettersStatusCorrect(String input) {
        if (input.length() != 5) {
            return false;
        }

        for (int i = 0; i < input.length(); i++) {
            if (!AVAILABLE_LETTER_STATUSES.contains(input.charAt(i))) {
                return false;
            }
        }

        return true;
    }
}
