/*
* Makayla Soh
* File name: lab3.java
* Description: >_<
*/

import java.io.*;
import java.util.*;

public class lab3
{
    static HashMap<String, Integer> labels = new HashMap<String, Integer>();
    static ArrayList<Object> instruct = new ArrayList<Object>();
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
        reg.put("$t1", 1);
        reg.put("$t2", 2);
        reg.put("$t3", 3);
        reg.put("$t4", 4);
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

        //get instructions
        getInst(args[0]);

        //read script
        if(args.length > 1)
        {
            File file = new File(args[1]);
            Scanner bob = new Scanner(file);
            String input;

            while(bob.hasNextLine())
            {
                input = bob.nextLine();
                System.out.println("mips> " + input);
                action(input, reg);
            }
            bob.close();
        }

        //user input
        else
        {
            Scanner bob = new Scanner(System.in);
            String input;

            do
            {
                System.out.print("mips> ");
                input = bob.nextLine();
                action(input, reg);
            } while(!input.equals("q"));

            bob.close();
        }
    }


    public static void getInst(String fName) throws FileNotFoundException
    {
        //data
        ArrayList<String> r = new ArrayList<String>();
        r.add("and");
        r.add("or");
        r.add("add");
        r.add("sll");
        r.add("slt");
        r.add("sub");
        r.add("jr");

        ArrayList<String> i = new ArrayList<String>();
        i.add("addi");
        i.add("beq");
        i.add("bne");
        i.add("lw");
        i.add("sw");

        ArrayList<String> j = new ArrayList<String>();
        j.add("j");
        j.add("jal");

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
            //r instruction
            if(r.contains(element.get(0)))
            {
                InstR newInst = new InstR(element);
                instruct.add(newInst);
            }

            //i instruction
            else if(i.contains(element.get(0)))
            {
                InstI newInst = new InstI(labels, element);
                instruct.add(newInst);
            }

            //j instruction
            else if(j.contains(element.get(0)))
            {
                InstJ newInst = new InstJ(labels, element);
                instruct.add(newInst);
            }

            //invalid instruction
            else
            {
                System.out.println("Invalid Instruction!");
            }
        }

        sc.close();
        return;
    }


    public static void action(String input, LinkedHashMap<String, Integer> reg)
    {
        char command = input.charAt(0);

        //help
        if(command == 'h')
        {
            System.out.println("\nh = show help\n" +
                               "d = dump register state\n" +
                               "s = single step through the program (i.e. execute 1 instruction and stop)\n" +
                               "s num = step through num instruction of the program\n" +
                               "r = run until the program ends\n" +
                               "m num1 num2 = display data memory from location num1 to num2\n" +
                               "c = clear all registers, memory, and the program counter to 0\n" +
                               "q = exit the program\n");

        }

        //dump
        else if(command == 'd')
        {
            System.out.print("\n");
            int format = 0;
            for(Map.Entry<String, Integer> e : reg.entrySet())
            {
                System.out.print(e.getKey() + " = " + e.getValue() + "    ");

                if(format%4 == 0) System.out.print("\n");
                format++;
            }
            System.out.print("\n\n");
        }

        //step
        else if(command == 's')
        {
            int num = 1;
            if(!input.equals("s"))
            {
                String in[] = input.split(" ");
                num = Integer.valueOf(in[1]);
            }

            if(reg.get("pc") >= instruct.size())
                num = 0;

            for(int i = 0; i < num; i++)
            {
                if(reg.get("pc") >= instruct.size())
                {
                    num = i;
                    break;
                }

                Object obj = instruct.get(reg.get("pc"));
                if(obj instanceof InstR)
                    ((InstR)obj).execute(reg);
                if(obj instanceof InstI)
                    ((InstI)obj).execute(reg, labels, mem);
                if(obj instanceof InstJ)
                    ((InstJ)obj).execute(reg, labels);
            }  

            String output = String.format("    %d instruction(s) executed", num);
            System.out.println(output);         
        }

        //run whole program
        else if(command == 'r')
        {
            while(reg.get("pc") < instruct.size())
            {
                Object obj = instruct.get(reg.get("pc"));
                if(obj instanceof InstR)
                    ((InstR)obj).execute(reg);
                if(obj instanceof InstI)
                    ((InstI)obj).execute(reg, labels, mem);
                if(obj instanceof InstJ)
                    ((InstJ)obj).execute(reg, labels);
            }
        }

        //clear
        else if(command == 'c')
        {
            reg.replaceAll( (key, value) -> value=0 );
            System.out.println("    Simulator reset\n");
            
        }

        //display data memory
        else if(command == 'm')
        {
            String in[] = input.split(" ");
            int index1 = Integer.valueOf(in[1]);
            int index2 = Integer.valueOf(in[2]);

            System.out.print("\n");
            for(int i = index1; i <= index2; i++)
            {
                String output = String.format("[%d] = %d", i, mem[i]);
                System.out.println(output);
            }
            System.out.print("\n");
            
        }

        //quit
        else if(command == 'q')
        {
            //System.out.println("Exiting the program!");
            return;
        }

        //not a valid input
        else
        {
            System.out.println("Whoops, wrong input!");
        }
        return;
    }
}
