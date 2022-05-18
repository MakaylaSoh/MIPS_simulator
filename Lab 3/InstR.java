/*
* Makayla Soh
* File name: InstR.java
* Description: :)
*/

import java.util.*;

public class InstR
{
    String a1, a2, a3, func;
    
    //constructor
    InstR(List<String> parts)
    {
        //variables
        this.func = parts.get(0);

        for(int i = 0; i < parts.size(); i++)
        {
            if(parts.get(i).equals("$zero"))
                parts.set(i, "$0");
        }

        if(func.equals("jr"))
        {
            this.a1 = parts.get(1); //rs
            this.a2 = "0"; //rd
            this.a3 = "0"; //shamt
        }
        else if(func.equals("sll")) 
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
    public void execute(LinkedHashMap<String, Integer> reg)
    {
        reg.replace("pc", reg.get("pc")+1);
        if(a3.equals("$0"))
            return;
        
        if(func.equals("and")) //and
        {
            int rsValue = reg.get(a1);
            int rtValue = reg.get(a2);
            int rdValue = rsValue & rtValue;
            reg.replace(a3, rdValue);
        }

        else if(func.equals("or")) //or
        {
            int rsValue = reg.get(a1);
            int rtValue = reg.get(a2);
            int rdValue = rsValue | rtValue;
            reg.replace(a3, rdValue);
        }

        else if(func.equals("add")) //add
        {
            int rsValue = reg.get(a1);
            int rtValue = reg.get(a2);
            int rdValue = rsValue + rtValue;
            reg.replace(a3, rdValue);
        }

        else if(func.equals("sll")) //sll
        {
            int rtValue = reg.get(a1);
            int shamt = Integer.valueOf(a3);
            for(int i = 0; i < shamt; i++)
                rtValue *= 2;
            reg.replace(a2, rtValue);
        }

        else if(func.equals("slt")) //slt
        {
            int rsValue = reg.get(a1);
            int rtValue = reg.get(a2);
            if(rsValue < rtValue)
                reg.replace(a3, 1);
            else
                reg.replace(a3, 0);
        }
        
        else if(func.equals("sub")) //sub
        {
            int rsValue = reg.get(a1);
            int rtValue = reg.get(a2);
            int rdValue = rsValue - rtValue;
            reg.replace(a3, rdValue);
        }

        else if(func.equals("jr")) //jr
        {
            int rsValue = reg.get(a1);
            reg.replace("pc", rsValue);
        }

        return;
    }
}