/*
* Makayla Soh
* File name: InstR.java
* Description: :)
*/

import java.util.*;

public class InstR extends Instructions
{
    String a1, a2, a3;
    
    //constructor
    InstR(List<String> parts)
    {
        //variables
        super(parts);

        if((super.op).equals("jr"))
        {
            this.a1 = parts.get(1); //rs
            this.a2 = "0"; //rd
            this.a3 = "0"; //shamt
        }
        else if((super.op).equals("sll")) 
        {
            this.a1 = parts.get(2); //rt
            this.a2 = parts.get(1); //rd
            this.a3 = parts.get(3); //shamt
        }
        else
        {
            this.a1 = parts.get(2); //rs
            this.a2 = parts.get(3); //rt
            this.a3 = parts.get(1); //rd
        }
    }

    //execute instructions
    public void execute(LinkedHashMap<String, Integer> reg, HashMap<String, Integer> labels, int mem[])
    {
        reg.replace("pc", reg.get("pc")+1);

        int reg1 = reg.get(a1);
        int reg2 = reg.get(a2);

        if(a3.equals("$0")) return;

        switch(super.op)
        {
            case "and":
                //rdValue = rsValue & rtValue;
                reg.replace(a3, reg1 & reg2);
                break;

            case "or":
                //rdValue = rsValue | rtValue;
                reg.replace(a3, reg1 | reg2);
                break;

            case "add":
                //rdValue = rsValue + rtValue;
                reg.replace(a3, reg1 + reg2);
                break;

            case "sll":
                //rtValue = reg.get(a1);
                int shamt = Integer.valueOf(a3);
                for(int i = 0; i < shamt; i++)
                    reg1 *= 2;
                reg.replace(a2, reg1);
                break;

            case "slt":
                if(reg1 < reg2)
                    reg.replace(a3, 1);
                else
                    reg.replace(a3, 0);
                break;
            
            case "sub":
                //rdValue = rsValue - rtValue;
                reg.replace(a3, reg1 - reg2);
                break;
            
            case "jr":
                reg.replace("pc", reg1);
                break;
            
            default:
                break;
        }

        return;
    }
}