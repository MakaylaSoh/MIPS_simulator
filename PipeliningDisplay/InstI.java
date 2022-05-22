/*
* Makayla Soh
* File name: InstI.java
* Description: MIPS i-type instruction execution
*/

import java.util.*;

public class InstI extends Instructions
{
    //constructor    
    InstI(HashMap<String, Integer> labels, List<String> parts)
    {
        //variables
        super(labels, parts);

        super.a1 = parts.get(2); //rs
        super.a2 = parts.get(1); //rt
        if(parts.size() > 3)
            super.a3 = parts.get(3); //imm
    }


    //execute instructions
    public void execute(LinkedHashMap<String, Integer> reg, int mem[])
    {
        int reg1 = 0, reg2 = 0;
        if(reg.get(a1) != null)
        {
            reg1 = reg.get(a1);
            reg2 = reg.get(a2);
        }

        String arr[] = a1.split("\\(|\\)");

        int add, current, offset;
        switch(super.op)
        {
            case "addi":
                if(a2.equals("$0")) 
                    break;
                int result = reg1 + Integer.valueOf(a3);
                reg.replace(a2, result);
                break;

            case "beq":
                add = (super.labels).get(a3) - reg.get("pc");
                current = reg.get("pc");

                if(reg1 == reg2)
                    reg.replace("pc", current + add);
                break;

            case "bne":
                add = (super.labels).get(a3) - reg.get("pc");
                current = reg.get("pc");

                if(reg1 != reg2)
                    reg.replace("pc", current + add);
                break;
            
            case "lw":
                offset = Integer.valueOf(arr[0]);
                add = reg.get(arr[1]) + offset;

                reg.replace(a2, mem[add]);        
                break;
                
            case "sw":
                offset = Integer.valueOf(arr[0]);
                add = reg.get(arr[1]) + offset;

                mem[add] = reg.get(a2);
                break;
            
            default:
                break;

        }

        return;
    }
}