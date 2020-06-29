package encryptdecrypt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Main {
    static abstract class processMessage {
        protected String message;
        protected int key;
        /**
         *  abstract class to encode/decode message
         * @param message String to process
         * @param key if key >=0 the message will be encoded else it will be decoded
         */
        processMessage(String message, int key) {
            this.message = message;
            this.key = key;
        }

        abstract String getMessage();
    }

    static class shift extends processMessage {
        /**
         * using the shift algorithm to encode/decode message
         * @param message String to process
         * @param key  from 'a' to 'z' the first cycle and from 'A' to 'Z' as the second cycle
         */
        shift(String message, int key) {
            super(message, key);
        }

        @Override
        String getMessage() {
            // from 'a' to 'z' the first cycle and from 'A' to 'Z' as the second cycle
            char[] output = new char[message.length()];
            for (int i = 0; i<message.length(); i++) {
                char ch = message.charAt(i);
                if (Character.isUpperCase(ch)) {
                    output[i] = (char) ((ch + key - 'A' + 26) % 26 + 'A');
                } else {
                    if (ch > 'z' || ch < 'a') {
                        output[i] = ch;
                    } else {
                        output[i] = (char) ((ch + key - 'a' + 26) % 26 + 'a');
                    }
                }
            }
            System.out.println(new String(output));
            return new String(output);
        }
    }

    static class unicode extends processMessage {

        /**
         * using the unicode algorithm to encode/decode message
         * @param message process the message
         * @param key perform a shift in unicode
         */
        unicode(String message, int key) {
            super(message, key);
        }

        @Override
        String getMessage(){
            char[] output = new char[message.length()];
            for(int i=0; i<message.length(); i++){
                int ch = message.charAt(i) + key;
                output[i] = (char) ch;
            }
            return new String(output);
        }
    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("-mode","enc");
        map.put("-data","");
        map.put("-key","0");
        map.put("-alg", "unicode");
        for (int i = 0; i < args.length; i+=2) {
            if (args[i+1].charAt(0) == '-') {
                System.out.println("Error! Argument does not have a value");
                return;
            } else {
                map.put(args[i], args[i + 1]);
            }
        }

        int key = Integer.parseInt(map.get("-key"));
        if (map.get("-mode").equals("dec")) {
            key *= -1;
        }

        String message = map.get("-data");
        if (message.isEmpty() && map.containsKey("-in")) {
            try {
                message = new String(Files.readAllBytes(Paths.get(map.get("-in"))));
            } catch (IOException e) {
                System.out.println("Error");
                e.printStackTrace();
                return;
            }
        }

        // Factory method
        processMessage tool = map.get("-alg").equals("unicode") ? new unicode(message, key): new shift(message, key);
        String output = tool.getMessage();
        System.out.println(message);
        if (map.containsKey("-out")) {
            try (PrintWriter writer = new PrintWriter(map.get("-out"))) {
                writer.print(new String(output));
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
        } else {
            System.out.println(new String(output));
        }
    }
}
