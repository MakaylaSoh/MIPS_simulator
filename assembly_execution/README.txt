Executes Assembly Languge, MIPS simulator

Purpose: To read in an assembly langauge MIPS file and execute the instructions. Be able to run the whole thing and have 
   and interactive portion. The interactive portion allows for stepping, showing registers, display memory (just set 
   as a static array), clearing registers, and quiting.

Input: an assembly language MIPS file. In the interactive state, when the program is ran the user can enter 
   any of the following commands:

h = show help
d = dump register state
s = single step through the program (i.e. execute 1 instruction and stop)
s num = step through num instructions of the program
r = run until the program ends
m num1 num2 = display data memory from location num1 to num2
c = clear all registers, memory, and the program counter to 0
q = exit the program

Output: the registers and memory are updated appropriately. Must keep track of pc.

Implementation Description:
 - Abstract class Instructions that creates a common abstract function execute for all instruction types. Also grabs the op
   (or funct for R inst) for all instructions.
 - InstI, InstJ, InstR classes extend Instructions and is created to hold the necessary information for each instruction. 
   This includes finding the correct registers. Each class also has there own execute function that executes based on 
   the specific instruction in the class.
 - The registers are created in main() because they are updated throughout the program. The registers are a hashmap
   of the register names and the values in the registers, which are updated. Unlike program two, registers are not
   linked to the binary code.
 - There instructions are just an arrayList the identify an instruction as R, I, or J. There is no binary representation!

The first step is mostly the same as in lab 2 - getting the instructions by parsing the file. But there is more added on. 

In getInst(), after parsing each line into an array of arrays (where each inner array is the instruction parts), 
each instruction must be parsed each instruction is turned into an instance of InstR, InstI, or InstJ. Now the program
has a list of Instructions, and each instruction can be executed! The list of instruct and labels are global and
can be accessed when trying to execute the program.

Interactive:
Using a scanner, the user input is scanned in. The input is processed by the function action() which takes the user input and 
the registers (current register state). The program continues to take user input and run action() until the user enters 'q' for quit.
Function action() runs all the instructions in the menu:
h = show help
d = dump register state
s = single step through the program (i.e. execute 1 instruction and stop)
s num = step through num instructions of the program
r = run until the program ends
m num1 num2 = display data memory from location num1 to num2
c = clear all registers, memory, and the program counter to 0
q = exit the program
 - help just prints out the above menu
 - dump prints out the registers (the static ones in main() passed into action())
 - s runs a single instruction or multiple instructions. Each instruction is executed by finding the instruction at the 
   program counter. The program counter just holds the index that must be found in the instruct arraylist. Then this instruction
   can be executed because it's an Instruction.
 - r will do what s does but for the entire program - aka for all the instructions. In other words, while the program counter 
   is less than the number of instructions.
 - c sets all the register values to 0 and the pc to 0
 - m finds the two input numbers and prints out the array values in the fake memeory at those locations. The "fake memory" is just
   a static array that holds values that the program can update.

In each instruction class, the execute function has a switch statement that identifies the specific instruction. The registers, labels
and memory must be passed into each execute call so that the correct register values can be accessed and updated.
