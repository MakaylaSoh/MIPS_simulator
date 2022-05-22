/*
* Makayla Soh
* File name: InstI.java
* Description: :D
*/

import java.util.*;

public class InstI
{
    String op, a1, a2, a3;
    HashMap<String, Integer> labels;
    
    InstI(HashMap<String, Integer> labels, List<String> parts)
    {
        //variables
        this.labels = labels;

        for(int i = 0; i < parts.size(); i++)
        {
            if(parts.get(i).equals("$zero"))
                parts.set(i, "$0");
        }

        this.op = parts.get(0);

        this.a1 = parts.get(2); //rs
        this.a2 = parts.get(1); //rt
        if(parts.size() > 3)
            this.a3 = parts.get(3); //imm
    }


    //execute instructions
    public void execute(LinkedHashMap<String, Integer> reg, HashMap<String, Integer> labels, int mem[])
    {
        reg.replace("pc", reg.get("pc")+1);

        if(op.equals("addi")) //addi
        {
            if(a2.equals("$0"))
                return;
            int rsValue = reg.get(a1);
            int result = rsValue + Integer.valueOf(a3);
            reg.replace(a2, result);
        }

        else if(op.equals("beq")) //beq
        {
            int rsValue = reg.get(a1);
            int rtValue = reg.get(a2);

            int add = labels.get(a3) - reg.get("pc");
            int current = reg.get("pc");

            if(rsValue == rtValue)
                reg.replace("pc", current + add);
        }

        else if(op.equals("bne")) //bne
        {
            int rsValue = reg.get(a1);
            int rtValue = reg.get(a2);

            int add = labels.get(a3) - reg.get("pc");
            int current = reg.get("pc");

            if(rsValue != rtValue)
                reg.replace("pc", current + add);
        }

        else if(op.equals("lw")) //lw
        { 
            String arr[] = a1.split("\\(|\\)");
            int offset = Integer.valueOf(arr[0]);
            int add = reg.get(arr[1]) + offset;

            reg.replace(a2, mem[add]);
        }

        else if(op.equals("sw")) //sw
        {
            String arr[] = a1.split("\\(|\\)");
            int offset = Integer.valueOf(arr[0]);
            int add = reg.get(arr[1]) + offset;

            mem[add] = reg.get(a2);
        }

        return;
    }
}