/*
 * Grep.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *  A class which takes files, flags and search strings as input and
 *  searches the given files according to the given flags for the search strings.
 *
 * @author      Uddesh Karda
 * @author      Aamir Jamal
 */

public class Grep {
    public  static final String RED = "\u001B[31m";
    public  static final String PURPLE = "\u001B[35m";
    public static final String RESET = "\u001B[0m";
    static String flags = "";
    static boolean printFlag = false;
    static int fileCounter = 0, flagCounter = 0;
    /**
     * The main program.
     * @param args  Command line argument (Filepath)
     */
    public static void main(String[] args) {

//        int searchPattern = 0;
//        String[] file = new String[args.length];
//        String[] flag = new String[args.length];
//        for (int index = 0; index < args.length; index++) {
//            if (args[index].endsWith(".txt") || args[index].matches("[-]")) {
//                file[fileCounter++] = args[index];
//            }
//            if (args[index].matches("-[cwlq]")) {
//                flag[flagCounter++] = args[index];
//            }
//            if (args[index].matches(".+") && !args[index].endsWith(".txt") && !args[index].startsWith("-") && !args[index].equals("-"))   {
//                searchPattern = index;
//            }
//        }

        String[] file = Arrays.stream(args)
                                .filter(arg -> arg.endsWith(".txt") || arg.matches("-"))
                                .toArray(String[]::new);
        fileCounter = file.length;
        String[] flag = Arrays.stream(args)
                                .filter(arg -> arg.matches("-[cwlq]"))
                                .toArray(String[]::new);
        flagCounter = flag.length;
        String searchPattern = Arrays.stream(args).filter(arg -> !arg.startsWith("-")).findFirst().get();

        if( flagCounter == 0){
            for(int indexFile = 0;indexFile < fileCounter; indexFile++){
                printFlag = true;
                flagW(file[indexFile], searchPattern);
            }
//            Arrays.stream(file).forEach(a -> flagW(a,searchPattern));
        }

        if( flagCounter == 1 ){
            for(int indexFile = 0;indexFile<fileCounter;indexFile++){
                if(file[indexFile] == null){
                    System.exit(1);
                }
                if(file[indexFile].equals("-")){
                    stdInput(searchPattern);
                    indexFile++;
                }
                switch (flag[0]){
                    case "-c":
                        flagC(file[indexFile],searchPattern);
                        break;
                    case "-w":
                        printFlag = true;
                        flagW(file[indexFile],searchPattern);
                        break;
                    case "-q":
                        flagQ(file[indexFile],searchPattern);
                        break;
                    case "-l":
                        flagL(file[indexFile],searchPattern);
                        break;
                }
            }
        }
        else{
//            for(int index = 0;index < flagCounter;index++) {
//                flags += flag[index];
//            }
            flags = Arrays.stream(flag).collect(Collectors.joining());

            for(int indexFile = 0;indexFile<fileCounter;indexFile++){
                if(file[indexFile].equals("-")){
                    stdInput(searchPattern);
                }
                for(int index = 0;index < flagCounter;index++){

                    if(flags.contains("-q")){
                        flagQ(file[indexFile], searchPattern);
                        System.exit(1);
                    }
                    else if(flags.contains("-l")){
                        flagL(file[indexFile], searchPattern);
                    }
                    else if(flags.contains("-w")){
                        printFlag = false;
                        flagW(file[indexFile], searchPattern);
                    }
                    else{
                        flagC(file[indexFile], searchPattern);
                    }
                }
            }
        }
        System.exit(1);
    }

    /**
     * This method is called when the flag -w is observed in the args array.
     *
     * @param fileName takes the filename entered by the user
     * @param patternToSearch takes the search string entered by the user
     */
    public static void flagW(String fileName, String patternToSearch){
        int countOfLines = 0;
        try (BufferedReader br = new BufferedReader
                (new FileReader(fileName))){
            String line;
            Pattern pattern = Pattern.compile("\\b"+ patternToSearch +"\\b");
            while ((line = br.readLine()) != null){
                Matcher matcher = pattern.matcher(line);
                if(matcher.find()){
                    countOfLines++;
                    if(printFlag){
                        if(patternToSearch.matches("[a-zA-Z]")){
                            System.out.println(PURPLE + fileName + RESET + "|" + line.substring(0, matcher.group().indexOf(patternToSearch)) +
                                    line.substring(matcher.group().indexOf(patternToSearch), matcher.group().lastIndexOf(patternToSearch))+
                                    line.substring(matcher.group().lastIndexOf(patternToSearch),line.length()));
                        }
                        else{
                            System.out.println(PURPLE + fileName + RESET + "|" + line);
                        }
                    }
                }
            }
            if(!printFlag){
                System.out.println(PURPLE + fileName + RESET + "|" + countOfLines);
            }
        }
        catch (Exception e){ }
    }

    /**
     * This method is called when the flag -c is observed in the args array.
     *
     * @param fileName takes the filename entered by the user
     * @param patternToSearch takes the search string entered by the user
     */
    public static void flagC(String fileName, String patternToSearch){
        try (BufferedReader br = new BufferedReader
                (new FileReader(fileName))){
            String line;
            int matchingLines = 0;
            Pattern pattern = Pattern.compile(patternToSearch);
            while ((line = br.readLine()) != null){
                Matcher matcher = pattern.matcher(line);
                if(matcher.find()){
                    matchingLines++;
                }
            }
            System.out.println(PURPLE + fileName + RESET + "|" + matchingLines);
        }
        catch (Exception e){ }
    }

    /**
     *
     * This method is called when the flag -q is observed in the args array.
     *
     * @param fileName takes the filename entered by the user
     * @param patternToSearch takes the search string entered by the user
     */
    public static void flagQ(String fileName, String patternToSearch){
        try (BufferedReader br = new BufferedReader
                (new FileReader(fileName))){
            String line;
            Pattern pattern = Pattern.compile(patternToSearch);
            while ((line = br.readLine()) != null){
                Matcher matcher = pattern.matcher(line);
                if(matcher.find()){
                    System.exit(1);
                }
            }
        }
        catch (Exception e){ }
    }

    /**
     * This method is called when the flag -l is observed in the args array.
     *
     * @param fileName takes the filename entered by the user
     * @param patternToSearch takes the search string entered by the user
     */
    public static void flagL(String fileName, String patternToSearch){
        try (BufferedReader br = new BufferedReader
                (new FileReader(fileName))){
            String line;
            Pattern pattern = Pattern.compile(patternToSearch);
            while ((line = br.readLine()) != null){
                Matcher matcher = pattern.matcher(line);
                if(matcher.find()){
                    System.out.println(PURPLE + fileName + RESET);
                    return;
                }
            }
        }
        catch (Exception e){ }
    }

    /**
     * This method is called if the input file is stated as a hyphen-minus.
     *
     * @param patternToSearch takes the search string entered by the user
     */
    public static void stdInput(String patternToSearch){
        try{
            Scanner reader = new Scanner(System.in);
            String input;
            int counter = 0;
            String[] stdip = new String[2000];
            while( !(input = reader.nextLine()).equals(";")){
                System.out.println(input);
                if(input.contains(patternToSearch)){
                    stdip[counter++] = input;
                }
            }

            for(int index = 0;index < flagCounter;index++){

                if(flags.contains("-q")){
                }
                else if(flags.contains("-l")){
                    System.out.println();
                }
                else if(flags.contains("-c")){
                    System.out.println(PURPLE + "(standard input): " + RESET + "" + counter);
                }
                else{
//                    for(int indexPrint = 0; indexPrint < counter; indexPrint++){
//                        System.out.println(PURPLE + "(standard input): " + RESET + "" + stdip[indexPrint]);
//                    }
                    Arrays.stream(stdip)
                            .forEach(a -> System.out.println(PURPLE + "(standard input): " + RESET + "" + a+1));
                }
            }
        }
        catch (Exception e){
        }
    }
}