import java.io.IOException;

import static io.Strings.*;
import static io.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        while(true) {
            String input = readJSONObject(System.in);
            processInput(input);
        }
    }
}

