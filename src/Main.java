import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static io.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String input = readJSONObject(System.in);
        processInput(input);
    }
}
