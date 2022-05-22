/*
* Makayla Soh
* File name: InstR.java
* Description: MIPS r-type instruction execution
*/

import java.util.*;

public class InstR extends Instructions
{  
    //constructor
    InstR(HashMap<String, Integer> labels, List<String> parts)
    {
        //variables
        super(labels, parts);

        if((super.op).equals("jr"))
        {
            super.a1 = parts.get(1); //rs
        }
        else if((super.op).equals("sll")) 
        {
            super.a1 = parts.get(2); //rt
            super.a2 = parts.get(1); //rd
            super.a3 = parts.get(3); //shamt
        }
        else
        {
            super.a1 = parts.get(2); //rs
            super.a2 = parts.get(3); //rt
            super.a3 = parts.get(1); //rd
        }
    }

    //execute instructions
    public void execute(LinkedHashMap<String, Integer> reg, int mem[])
    {
        int reg1 = 0, reg2 = 0;

        switch(super.op)
        {
            case "and":
                if(a3.equals("$0")) return;
                reg1 = reg.get(a1);
                reg2 = reg.get(a2);
                reg.replace(a3, reg1 & reg2);
                break;

            case "or":
                if(a3.equals("$0")) return;
                reg1 = reg.get(a1);
                reg2 = reg.get(a2);
                reg.replace(a3, reg1 | reg2);
                break;

            case "add":
                if(a3.equals("$0")) return;
                reg1 = reg.get(a1);
                reg2 = reg.get(a2);
                reg.replace(a3, reg1 + reg2);
                break;

            case "sll":
                reg1 = reg.get(a1);
                int shamt = Integer.valueOf(a3);
                for(int i = 0; i < shamt; i++)
                    reg1 *= 2;
                reg.replace(a2, reg1);
                break;

            case "slt":
                if(a3.equals("$0")) return;
                reg1 = reg.get(a1);
                reg2 = reg.get(a2);
                if(reg1 < reg2)
                    reg.replace(a3, 1);
                else
                    reg.replace(a3, 0);
                break;
            
            case "sub":
                if(a3.equals("$0")) return;
                reg1 = reg.get(a1);
                reg2 = reg.get(a2);
                reg.replace(a3, reg1 - reg2);
                break;
            
            case "jr":
                reg1 = reg.get(a1);
                reg.replace("pc", reg1);
                break;
            
            default:
                break;
        }

        return;
    }
}