/*
* Makayla Soh
* File name: Instructions.java
* Description: Abstract class that represents an instruction. Functions step through one inst and print pipeline state.
*/

import java.util.*;

abstract class Instructions 
{

    HashMap<String, Integer> labels;
    String op, a1, a2, a3;
    
    //constructor
    Instructions(HashMap<String, Integer> labels, List<String> parts)
    {
        //variables
        this.labels = labels;

        for(int i = 0; i < parts.size(); i++)
        {
            if(parts.get(i).equals("$zero"))
                parts.set(i, "$0");
        }
        this.op = parts.get(0);
        this.a1 = "0";
        this.a2 = "0";
        this.a3 = "0";
    }


    //stepping one instruction
    public static int step(LinkedHashMap<String, Integer> reg, ArrayList<Instructions> instruct, int mem[], String state[])
    {
        int exe = 1;
        if(reg.get("pc") < instruct.size())
        {
            reg.replace("pc", reg.get("pc") + 1);
            exe = update_pipeline(reg, instruct, state, mem);
        }

        else
        {
            for(int i = 4; i > 1; i--)
                state[i] = state[i-1];
            state[1] = "empty";
            return 0;
        }

        Instructions ex = instruct.get(reg.get("pc")-1);   // 0 is execute, 1 is don't execute     
        if(exe == 0 && !state[1].equals("squash"))
            ex.execute(reg, mem);
        
        if(exe == 0) return 1;

        return 0;
    }

    //print pipeline state
    public static void print_pipeline(String state[], LinkedHashMap<String, Integer> reg)
    {
        System.out.println("pc    if/id   id/exe   exe/mem   mem/wb");  
        for(int i = 0; i < 5; i++)   
        {
            String[] output = state[i].split("_");
            if(state[i] == null)
                System.out.print("empty     ");
            else
                System.out.print(String.format("%s     ", output[0]));
        } 
        System.out.print("\n");
        
        return;
    }

    //executing each instruction
    abstract void execute(LinkedHashMap<String, Integer> reg, int mem[]);


    //update pipeline state
    public static int update_pipeline(LinkedHashMap<String, Integer> reg, ArrayList<Instructions> instruct, String state[], int mem[])
    { 
        //current instruction
        Instructions current = instruct.get(reg.get("pc") - 1);
        state[0] = Integer.toString(reg.get("pc"));  

        //shift states down one
        for(int i = 4; i > 1; i--)
            state[i] = state[i-1];
        
        //first instruction
        state[1] = current.op;
        if(state[1] == null)
            return 0;

        //check branches
        int num = check_branch(reg, instruct, state, mem);
        if(num == 1 || (num == 0 && state[1].equals("squash")))
            return num; // 1 indicates DO NOT execute instruction
        
        //check lw
        if(check_lw(reg, instruct, state) == 1)
            return 1; // indicates do not execute next instruction

        //check jumps
        if((state[2].equals("j") || state[2].equals("jal") || state[2].equals("jr")))
        {
            reg.replace("pc", reg.get("pc")-1); //stay on same inst
            state[0] = Integer.toString(reg.get("pc"));
            state[1] = "squash";
            return 1; //indicates do not execute next instruction
        }

        return 0;
    }

    /*
    Function: check_branch()
    Description: returns 1 if the current instruction should not be executed and 0 if the current instruction should be executed.
    In other words, if the program should contine then 0 is entered, if the program should not continue then 1 should be entered.
    The program should not continue if there is a taken branch becuase the following instructions "clog" the pipeline.
    */
    public static int check_branch(LinkedHashMap<String, Integer> reg, ArrayList<Instructions> instruct, String state[], int mem[])
    {
        // if NOT taken branch then branch will be labeled beq_ex or bne_ex

        // taken branch at state[4] indicates execute state[4] instruction
        if(state[4].equals("beq") || state[4].equals("bne"))
        {
            instruct.get(reg.get("pc") - 4).execute(reg, mem);
            state[0] = Integer.toString(reg.get("pc"));
            state[1] = "squash";
            state[2] = "squash";
            state[3] = "squash";
            return 0;
        }

        //continue and do nothing
        if(state[3].equals("beq") || state[3].equals("bne") || state[2].equals("beq") || state[2].equals("bne"))
            return 1;

        //current inst is a branch
        if(state[1].equals("beq") || state[1].equals("bne"))
        {
            Instructions check = instruct.get(reg.get("pc") - 1);
            int val1 = reg.get(check.a1);
            int val2 = reg.get(check.a2);

            //branch taken, returns 1 to indicate not to continue executing instructions (just update pipeline)
            if( (state[1].equals("beq") && val1 == val2) || (state[1].equals("bne") && val1 != val2) )
                return 1;
            //branch not taken, returns 0, adds _ex to indicate the branch is already executed
            else
                state[1] += "_ex";
        }
        
        return 0;
    }
    
    /*
    Function: check_lw()
    Description: If the instructions "lw rt, immediate(fs)" is followed by other instructions, then we must check that
    the rt (dest reg) is not equal to the register(s) directly after. This is because if the register is used, we must
    wait until the pipeline stage that loads the value into the destination location before using that register.
    */
    public static int check_lw(LinkedHashMap<String, Integer> reg, ArrayList<Instructions> instruct, String state[])
    {
        if(!(state[3].equals("lw"))) return 0;

        Instructions prev1 = instruct.get(reg.get("pc") - 2);
        Instructions lw_inst = instruct.get(reg.get("pc") - 3);
        String lw_dest = lw_inst.a2;

        String[] check1 = { "and", "or", "add", "slt", "sub", "beq", "bne" }; // check a1 and a2
        String[] check2 = { "sll", "jal", "j", "jr", "addi" }; //check a1
        String[] check3 = { "sw", "lw" }; //check a2

        //following inst is in check1
        if(Arrays.asList(check1).contains(state[2]))
        {
            String op_rs = prev1.a1;
            String op_rt = prev1.a2;
            if(lw_dest.equals(op_rs) || lw_dest.equals(op_rt))
            {
                reg.replace("pc", reg.get("pc") - 1); //stay on same inst
                state[0] = Integer.toString(reg.get("pc"));  

                state[1] = state[2];
                state[2] = "stall";
                return 1;
            }
        }

        //following inst is in check2
        if(Arrays.asList(check2).contains(state[2]))
        {
            String op_rs = prev1.a1;

            if(lw_dest.equals(op_rs))
            {
                reg.replace("pc", reg.get("pc") - 1); //stay on same inst
                state[0] = Integer.toString(reg.get("pc"));  

                state[1] = state[2];
                state[2] = "stall";
                return 1;
            }
        }

        //following inst is in check3
        if(Arrays.asList(check3).contains(state[2]))
        {
            String arr[] = (prev1.a1).split("\\(|\\)");
            String op_rs = arr[1]; //arr[0] is offset
            String op_rt = prev1.a2;

            if( (state[2].equals("lw") && lw_dest.equals(op_rs)) || (state[2].equals("sw") && lw_dest.equals(op_rt)))
            {
                reg.replace("pc", reg.get("pc") - 1); //stay on same inst
                state[0] = Integer.toString(reg.get("pc"));  

                state[1] = state[2];
                state[2] = "stall";
                return 1;
            }
        }
        return 0;
    }
}