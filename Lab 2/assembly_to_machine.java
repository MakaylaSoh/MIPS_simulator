/*
* Makayla Soh
* File name: lab2.java
* Description: Reads in a program in assembly langauge. Uses the MIPS instruction set architecture to convert the assembly language into machine langauge (binary).
*   Parses and converts based off of instruction types (I, J, R). Output is a string to the console representing the machine langauge.
*/

import java.io.*;
import java.util.*;

import javax.lang.model.util.ElementScanner14;

public class lab2
{

    // Function: getInst()
    // Description: Reads an asm file and removes comments and stores labels in a hashmap (key: label name, value: address/line).
    //              First stores each line in an ArrayList. Then breaks each line into individual 
    //              instructions and stores parts as an array in an ArrayList
    public static void getInst(String fName, HashMap<String, Integer> labels, ArrayList<String[]> instParts) throws FileNotFoundException
    {
        //opening file
        File file = new File(fName);
        Scanner sc = new Scanner(file);

        ArrayList<String> instLine = new ArrayList<String>();
        int add = 0;

        while(sc.hasNextLine())
        {
            String line = sc.nextLine().trim();

            //remove comments
            if(line.isEmpty() || line.startsWith("#")) 
                continue;
            if(line.contains("#"))
                line = line.substring(0, line.indexOf("#")).trim();

            //keep track and remove labels
            if(line.contains(":")) 
            {
                String labelName = line.substring(0, line.indexOf(":"));
                labels.put(labelName, add);
                line = line.substring(line.indexOf(":")+1).trim();
                if(line.length() == 0) continue;
            }
            
            //add instruction, keep track of address
            int exist = line.indexOf("$"); 
            if(exist != -1 && exist != 1)
                line = line.substring(0, exist) + " " + line.substring(exist);
            instLine.add(line); 
 
            add++;
        }
        
        //splits parts
        for(String i : instLine)
        {
            String temp[] = i.split("\\s+|, |,");
            ArrayList<String> t = new ArrayList<String>();
            for(String e : temp)
                if(!e.equals("")) t.add(e);
        
            String final_parts[] = new String[t.size()];
            final_parts = t.toArray(final_parts);

            instParts.add(final_parts);
        }

        sc.close();

        return;
    }


    // Function: getBinInst()
    // Description: Takes in an array that holds the instructions, each instruction is an array itself of the different instrution parts.
    //   Also takes in the lables (with their addresses) and the array where the binary instructions will be stored. Depending on the type of 
    //   instruction, translates the instruction to the machine language. Uses hashmaps to get the associated binary "codes".
    public static void getBinInst(ArrayList<String[]> instParts, HashMap<String, Integer> labels, ArrayList<String> binInst)
    {
        //hashmaps for registers and instructions
        HashMap<String, String> reg = new HashMap<String, String>(27);
        reg.put("$zero", "00000 ");
        reg.put("$0", "00000 ");
        reg.put("$v0", "00010 ");
        reg.put("$v1", "00011 ");
        reg.put("$a0", "00100 ");
        reg.put("$a1", "00101 ");
        reg.put("$a2", "00110 ");
        reg.put("$a3", "00111 ");
        reg.put("$t0", "01000 ");
        reg.put("$t1", "01001 ");
        reg.put("$t2", "01010 ");
        reg.put("$t3", "01011 ");
        reg.put("$t4", "01100 ");
        reg.put("$t5", "01101 ");
        reg.put("$t6", "01110 ");
        reg.put("$t7", "01111 ");
        reg.put("$s0", "10000 ");
        reg.put("$s1", "10001 ");
        reg.put("$s2", "10010 ");
        reg.put("$s3", "10011 ");
        reg.put("$s4", "10100 ");
        reg.put("$s5", "10101 ");
        reg.put("$s6", "10110 ");
        reg.put("$s7", "10111 ");
        reg.put("$t8", "11000 ");
        reg.put("$t9", "11001 ");
        reg.put("$sp", "11101 ");
        reg.put("$ra", "11111 ");

        HashMap<String, String> r = new HashMap<String, String>(7);
        r.put("and", "100100 ");
        r.put("or", "100101 ");
        r.put("add", "100000 ");
        r.put("sll", "000000 ");
        r.put("slt", "101010 ");
        r.put("sub", "100010 ");
        r.put("jr", "001000 ");

        HashMap<String, String> i = new HashMap<String, String>(7);
        i.put("addi", "001000 ");
        i.put("beq", "000100 ");
        i.put("bne", "000101 ");
        i.put("lw", "100011 ");
        i.put("sw", "101011 ");

        HashMap<String, String> j = new HashMap<String, String>();
        j.put("j", "000010 ");
        j.put("jal", "000011 ");

        int index = 0;
        for(String inst[] : instParts)
        {
            //Instruction R (ex: add $2, $3, $4)
            if(r.containsKey(inst[0]))
            {
                String element = "000000 ";

                if(inst[0].equals("jr"))
                {
                    element += reg.get(inst[1]) + "00000 00000 00000 ";
                }
                else if(reg.containsKey(inst[3]))
                    element += reg.get(inst[2]) + reg.get(inst[3]) + reg.get(inst[1]) + "00000 ";
                else
                    element += "00000 " + reg.get(inst[2]) + reg.get(inst[1]) + getBinNum(inst[3], 5);

                element += r.get(inst[0]);
                System.out.println(element);
                binInst.add(element.trim());
            }

            //Instruction I (ex: addi $2, $3, 12)
            else if(i.containsKey(inst[0]))
            {
                String element = i.get(inst[0]);
                if(inst[0].equals("beq") || inst[0].equals("bne"))
                {
                    //current locations is ind
                    int toLabel = labels.get(inst[3]) - index - 1;
                    String labelAdd = Integer.toString(toLabel);
                    element += reg.get(inst[1]) + reg.get(inst[2]) + getBinNum(labelAdd, 16);
                }
                else if(inst[0].equals("lw") || inst[0].equals("sw")) //ex: sw $3, 4($2)
                {
                    String arr[] = inst[2].split("\\(|\\)");
                    element += reg.get(arr[1]) + reg.get(inst[1]) + getBinNum(arr[0], 16);
                }
                else
                {
                    element += reg.get(inst[2]) + reg.get(inst[1]) + getBinNum(inst[3], 16);
                }
                System.out.println(element);
                binInst.add(element.trim());
            }

            //Instruction J
            else if(j.containsKey(inst[0]))
            {
                String labelAdd = Integer.toString(labels.get(inst[1]));
                String element = j.get(inst[0]) + getBinNum(labelAdd, 26);
                System.out.println(element);
                binInst.add(element.trim());
            }

            else
            {
                String ret = String.format("Invalid instruction: %s", inst[0]);
                binInst.add(ret);
                break;
            }
            index++;
        }
        
        return;
    }



    // Function: getBinNum()
    // Description: Takes in a binary number as a string and the size the binary number needs to be extended to.
    //   This just ensures that the binary number is representing the correct number of bytes. The values of 
    //   the input number does not change.
    public static String getBinNum(String num, int extend)
    {
        int temp = Integer.valueOf(num);
        String binNum = Integer.toBinaryString(temp);
        while(binNum.length() < extend)
        {
            if(temp < 0)
                binNum = "1" + binNum;
            else
                binNum = "0" + binNum;
        }
        while(binNum.length() > extend)
            binNum = binNum.substring(1);
        
        return binNum + " ";
    }



    // Function: main()
    // Description: Passes the input argument (assembly languge file) into getInst to parse the input into the instruction parts.
    //   Then calls getBinInst to translate the instructions into machine language, and then prints out the result.
    public static void main(String args[]) throws FileNotFoundException
    {
        ArrayList<String[]> instParts = new ArrayList<String[]>();
        HashMap<String, Integer> labels = new HashMap<String, Integer>();
        ArrayList<String> binInst = new ArrayList<String>();

        //get instructions
        getInst(args[0], labels, instParts);

        //turn to binary
        getBinInst(instParts, labels, binInst);

        //print elements in binInst
        for(String i : binInst)
            System.out.println(i);
    }
}
