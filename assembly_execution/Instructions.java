/*
* Makayla Soh
* File name: Instructions.java
* Description: >_<
*/

import java.util.*;

abstract class Instructions {

    String op;

    Instructions(List<String> parts)
    {
        //variable op
        for(int i = 0; i < parts.size(); i++)
        {
            if(parts.get(i).equals("$zero"))
                parts.set(i, "$0");
        }
        this.op = parts.get(0);
    }

    abstract void execute(LinkedHashMap<String, Integer> reg, HashMap<String, Integer> labels, int mem[]);
    
}