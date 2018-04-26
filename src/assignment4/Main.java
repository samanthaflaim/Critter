package assignment4;
/* CRITTERS Main.java
 * EE422C Project 4 submission by
 * Samantha Flaim
 * smf2728
 * 15460
 * Slip days used: <0>
 * Spring 2018
 */

import java.lang.reflect.InvocationTargetException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;
import java.lang.reflect.Method;
import java.io.*;

/*
 * Usage: java <pkgname>.Main <input file> test
 * input file is optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */
public class Main {

    static Scanner kb;	// scanner connected to keyboard input, or input file
    private static String inputFile;	// input file, used instead of keyboard input if specified
    static ByteArrayOutputStream testOutputString;	// if test specified, holds all console output
    private static String myPackage;	// package of Critter file.  Critter cannot be in default pkg.
    private static boolean DEBUG = false; // Use it or not, as you wish!
    static PrintStream old = System.out;	// if you want to restore output to console


    // Gets the package name.  The usage assumes that Critter and its subclasses are all in the same package.
    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    /**
     * Main method.
     * @param args args can be empty.  If not empty, provide two parameters -- the first is a file name, 
     * and the second is test (for test output, where all output to be directed to a String), or nothing.
     */
    public static void main(String[] args) { 
        if (args.length != 0) {
            try {
                inputFile = args[0];
                kb = new Scanner(new File(inputFile));			
            } catch (FileNotFoundException e) {
                System.out.println("USAGE: java Main OR java Main <input file> <test output>");
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println("USAGE: java Main OR java Main <input file>  <test output>");
            }
            if (args.length >= 2) {
                if (args[1].equals("test")) { // if the word "test" is the second argument to java
                    // Create a stream to hold the output
                    testOutputString = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(testOutputString);
                    // Save the old System.out.
                    old = System.out;
                    // Tell Java to use the special stream; all console output will be redirected here from now
                    System.setOut(ps);
                }
            }
        } else { // if no arguments to main
            kb = new Scanner(System.in); // use keyboard and console
        }

        /* Do not alter the code above for your submission. */
        /* Write your code below. */

        String inLine = kb.nextLine(); //gets the entire line inputted before enter pressed
        String[] input = inLine.split(" "); //creates an array of Strings; each element is a word from inLine
        String class_name =  ""; //to be used when class name is an expected input
        int count = 1; //will be used to do certain number of step/seed/make

        /*
         * This while loop will continue to scan for input until "quit" is inputted
         * Input will be received once the ENTER key is pressed
         */
        while (input[0].equals("quit") != true) {
            if (input[0].equals("show")) {
                try {
                    //if there is junk text after "show"
                    if (input[1].length() > 0) {
                        System.out.println("error processing: " + inLine);
                    }
                }
                //if there is no such junk text
                catch (IndexOutOfBoundsException e) {
                    Critter.displayWorld();
                }
            }

            else if (input[0].equals("step")) {
                try {
                    //if there's junk text after "step"
                    if (input[2].length() > 0) {
                        System.out.println("error processing: " + inLine);
                    }
                }
                //if there's no such junk text
                catch (IndexOutOfBoundsException e){
                    //do step count times
                    try {
                        count = Integer.parseInt(input[1]);
                        for (int i = 0; i < count; i++) {
                            Critter.worldTimeStep();
                        }
                    }
                    catch (IllegalArgumentException e2) {
                        System.out.println("error processing: " + inLine);
                    }
                    //if no number was added, step will be done only once
                    catch (IndexOutOfBoundsException e2) {
                        Critter.worldTimeStep();
                    }
                }
            }

            else if (input[0].equals("seed")) {
                try {
                    //if there's junk after "seed"
                    if (input[2].length() > 0) {
                        System.out.println("error processing: " + inLine);
                    }
                }
                //if there's no such junk
                catch (IndexOutOfBoundsException e){
                    try {
                        long seed = Integer.parseInt(input[1]);
                        Critter.setSeed(seed);
                    }
                    catch (IllegalArgumentException | IndexOutOfBoundsException e2) {
                        System.out.println("error processing: " + inLine);
                    }
                }
            }

            else if (input[0].equals("make")) {
                try {
                    class_name = input[1];
                    //if there's junk after "make"
                    if (input[3].length() > 0) {
                        System.out.println("error processing: " + inLine);
                    }
                }
                //if there's no such junk
                catch (IndexOutOfBoundsException e) {
                    try {
                        count = Integer.parseInt(input[2]);
                        for (int i = 0; i < count; i++) {
                            try {
                                Critter.makeCritter(class_name);
                            }
                            catch (InvalidCritterException | NoClassDefFoundError e2){
                                System.out.println("error processing: " + inLine);
                                break;
                            }
                        }
                    }
                    catch (IllegalArgumentException e2) {
                        System.out.println("error processing: " + inLine);
                    }
                    catch (IndexOutOfBoundsException e2) {
                        count = 1;
                    }
                }
            }

            else if (input[0].equals("stats")) {
                try {
                    class_name = input[1];
                    //if there's junk after "stats"
                    if (input[2].length() > 0) {
                        System.out.println("error processing: " + inLine);
                    }
                }
                //if there's no such junk
                catch (IndexOutOfBoundsException e) {
                    try {
                        Class<?> stats_class = Class.forName(myPackage + "." + class_name);
                        Method method = stats_class.getMethod("runStats", List.class);
                        method.invoke(null, Critter.getInstances(class_name));
                    }
                    catch (ClassNotFoundException | NoSuchMethodException |
                            InvocationTargetException | IllegalAccessException e2) {
                        System.out.println("error processing: " + input);
                    }
                }
            }

            //no proper command inputted
            else {
                System.out.println("invalid command: " + inLine);
            }

            while (!kb.hasNext()); //wait for another input
            inLine = kb.nextLine(); //save the entire line
            input = inLine.split(" "); //save the entire line w each element is a word
        }
        
        /* Write your code above */
        System.out.flush();

    }
}
