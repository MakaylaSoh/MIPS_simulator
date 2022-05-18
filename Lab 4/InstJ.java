/*
* Makayla Soh
* File name: InstJ.java
* Description: MIPS j-type instruction execution, for instructions jal and j!
*/

import java.util.*;
    
public class InstJ extends Instructions
{
    //constructor
    InstJ(HashMap<String, Integer> labels, List<String> parts)
    {
        //variables
        super(labels, parts);
        super.a1 = parts.get(1); //rs
    }

    //execute instructions
    public void execute(LinkedHashMap<String, Integer> reg, int mem[])
    {
        if((super.op).equals("jal"))
        {
            int nextPC = reg.get("pc");
            reg.replace("$ra", nextPC);
        }

        int add = (super.labels).get(a1);
        reg.replace("pc", add);

        return;
    }
}