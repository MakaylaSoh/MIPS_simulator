/*
* Makayla Soh
* File name: Instruction.java
* Description: (:
*/

import java.util.*;
    
public class InstJ
{
    String op, a1;
    HashMap<String, Integer> labels;
    
    InstJ(HashMap<String, Integer> labels, List<String> parts)
    {
        //variables
        this.labels = labels;

        for(int i = 0; i < parts.size(); i++)
        {
            if(parts.get(i).equals("$zero"))
                parts.set(i, "$0");
        }
        this.op = parts.get(0);
        this.a1 = parts.get(1); //rs
    }

    //execute instructions
    public void execute(LinkedHashMap<String, Integer> reg,  HashMap<String, Integer> labels)
    {
        reg.replace("pc", reg.get("pc")+1);

        if(op.equals("j")) //j
        {
            int add = labels.get(a1);
            reg.replace("pc", add);
        }

        else if(op.equals("jal")) //jr
        {
            int nextPC = reg.get("pc");
            reg.replace("$ra", nextPC);

            int add = labels.get(a1);
            reg.replace("pc", add);
        }

        return;
    }
}