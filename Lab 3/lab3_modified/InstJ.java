/*
* Makayla Soh
* File name: Instruction.java
* Description: (:
*/

import java.util.*;
    
public class InstJ extends Instructions
{
    String op, a1;
    
    InstJ(List<String> parts)
    {
        //variables
        super(parts);
        this.a1 = parts.get(1); //rs
    }

    //execute instructions
    public void execute(LinkedHashMap<String, Integer> reg, HashMap<String, Integer> labels, int mem[])
    {
        reg.replace("pc", reg.get("pc")+1);

        if((super.op).equals("jal"))
        {
            int nextPC = reg.get("pc");
            reg.replace("$ra", nextPC);
        }

        int add = labels.get(a1);
        reg.replace("pc", add);

        return;
    }
}