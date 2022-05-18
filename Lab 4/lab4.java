/*
* Makayla Soh
* File name: lab4.java
* Description: >_<
*/

import java.io.*;
import java.util.*;

public class lab4
{
    static HashMap<String, Integer> labels = new HashMap<String, Integer>();
    static ArrayList<Instructions> instruct = new ArrayList<Instructions>();
    static String state[] = { "0", "empty", "empty", "empty", "empty"};
    static int mem[] = new int[8192];

    public static void main(String args[]) throws FileNotFoundException
    {
        //hashmaps for register values
        LinkedHashMap<String, Integer> reg = new LinkedHashMap<String, Integer>(28);
        reg.put("pc", 0);
        reg.put("$0", 0);
        reg.put("$v0", 0);
        reg.put("$v1", 0);
        reg.put("$a0", 0);
        reg.put("$a1", 0);
        reg.put("$a2", 0);
        reg.put("$a3", 0);
        reg.put("$t0", 0);
        reg.put("$t1", 0);
        reg.put("$t2", 0);
        reg.put("$t3", 0);
        reg.put("$t4", 0);
        reg.put("$t5", 0);
        reg.put("$t6", 0);
        reg.put("$t7", 0);
        reg.put("$s0", 0);
        reg.put("$s1", 0);
        reg.put("$s2", 0);
        reg.put("$s3", 0);
        reg.put("$s4", 0);
        reg.put("$s5", 0);
        reg.put("$s6", 0);
        reg.put("$s7", 0);
        reg.put("$t8", 0);
        reg.put("$t9", 0);
        reg.put("$sp", 0);
        reg.put("$ra", 0);

        //get instructions (.asm file)
        getInst(args[0]);

        String input;
        int result[] = { 0, 0 };

        //read script
        if(args.length > 1)
        {
            File file = new File(args[1]);
            Scanner scan = new Scanner(file);

            while(scan.hasNextLine())
            {
                input = scan.nextLine();
                System.out.println("mips> " + input);
                result = action(input, reg, result[0], result[1]);
            }
            scan.close();
        }

        //interactive
        else
        {
            Scanner scan = new Scanner(System.in);
            do
            {
                System.out.print("mips> ");
                input = scan.nextLine();
                result = action(input, reg, result[0], result[1]);
            } while(!input.equals("q"));

            scan.close();
        }
    }


    public static void getInst(String fName) throws FileNotFoundException
    {
        //data
        String[] r = { "and", "or", "add", "sll", "slt", "sub", "jr"};
        String[] i = { "addi", "beq", "bne", "lw", "sw" };
        String[] j = { "j", "jal" };

        //open file
        File file = new File(fName);
        Scanner sc = new Scanner(file);

        //parse into lines
        ArrayList<List<String>> instLine = new ArrayList<List<String>>();
        int add = 0;
        while(sc.hasNextLine())
        {
            String line = sc.nextLine().trim();

            //remove comments
            if(line.isEmpty() || line.startsWith("#")) 
                continue;
            if(line.contains("#"))
                line = line.substring(0, line.indexOf("#")).trim();

            //remove labels
            if(line.contains(":")) 
            {
                String labelName = line.substring(0, line.indexOf(":"));
                labels.put(labelName, add);
                line = line.substring(line.indexOf(":")+1).trim();
                if(line.length() == 0) continue;
            }
            
            //parse into baby parts
            int exist = line.indexOf("$");
            if(exist != -1 && exist != 1)
                line = line.substring(0, exist) + " " + line.substring(exist);

            List<String> parts = Arrays.asList(line.split("\\s+|, |,"));
            for(String e : parts)
                if(e.equals("")) parts.remove(e);

            instLine.add(parts);
            add++;
        }

        //get instructions
        for(List<String> element : instLine)
        {
            Instructions newInst = null;
            String op = element.get(0);

            if(Arrays.asList(r).contains(op)) //r
                newInst = new InstR(labels, element);

            else if(Arrays.asList(i).contains(op)) //i
                newInst = new InstI(labels, element);

            else if(Arrays.asList(j).contains(op)) //j
                newInst = new InstJ(labels, element);
            
            else //invalid
                System.out.println("Invalid Instruction!");
            
            //add inst to list
            if(newInst != null)
                instruct.add(newInst);
        }

        sc.close();
        return;
    }


    public static int[] action(String input, LinkedHashMap<String, Integer> reg, int cycle_count, int inst_count)
    {
        char command = input.charAt(0);

        switch (command) 
        {
            //help
            case 'h':
                System.out.println("\nh = show help\n" +
                "d = dump register state\n" +
                "p = show pipeline registers\n" +
                "s = step through a single clock cycle (i.e. simulate 1 cycle and stop)\n" +
                "s num = step through num clock cycles\n" +
                "r = run until the program ends and display timing summary\n" +
                "m num1 num2 = display data memory from location num1 to num2\n" +
                "c = clear all registers, memory, and the program counter to 0\n" +
                "q = exit the program\n");
                break;

            //register dump
            case 'd':
                int format = 0;
                System.out.print("\n");
                for(Map.Entry<String, Integer> e : reg.entrySet())
                {
                    System.out.print(e.getKey() + " = " + e.getValue() + "    ");

                    if(format%4 == 0) System.out.print("\n");
                    format++;
                }
                System.out.print("\n\n");
                break;
            
            //print current pipeline
            case 'p':
                Instructions.print_pipeline(state, reg);
                break;

            //step
            case 's':
                
                int num = 1, loc = 0;
                if(!input.equals("s")) 
                {
                    String in[] = input.split(" ");
                    num = Integer.valueOf(in[1]);
                }

                for(; loc < num; loc++)
                {
                    inst_count += Instructions.step(reg, instruct, mem, state);
                    cycle_count++;
                }
                Instructions.print_pipeline(state, reg);
                break; 

            //run through program
            case 'r':              
                while(reg.get("pc") < instruct.size())
                {
                    inst_count += Instructions.step(reg, instruct, mem, state);
                    cycle_count += 1;
                }

                for(int i = 0; i < 4; i++)
                {
                    inst_count += Instructions.step(reg, instruct, mem, state);
                    cycle_count += 1;
                }

                float cpi = (float)cycle_count/inst_count;
                String output = String.format("Program complete\nCPI = %.3f     Cycles = %d     Instructions = %d", cpi, cycle_count, inst_count);
                System.out.println(output);
                break;

            //clear
            case 'c':
                reg.replaceAll( (key, value) -> value = 0 );

                state[0] = "0";
                for(int i = 1; i < 5; i++)
                    state[i] = "empty";

                cycle_count = 0;
                inst_count = 0;

                System.out.println("    Simulator reset\n");
                
                break;

            //print memory at index
            case 'm':
                String in[] = input.split(" ");
                int index1 = Integer.valueOf(in[1]);
                int index2 = Integer.valueOf(in[2]);

                System.out.print("\n");
                for(int i = index1; i <= index2; i++)
                {
                    String out = String.format("[%d] = %d", i, mem[i]);
                    System.out.println(out);
                }
                System.out.print("\n");
                break;

            //quit
            case 'q':
                break;
            
            //wrong input
            default:
                System.out.println("Invalid Input");
                break;
        }
        
        int ret[] = {cycle_count, inst_count};
        return ret;
    }
}
