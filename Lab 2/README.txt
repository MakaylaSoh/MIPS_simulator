Purpose: Read in assembly language (MIPS) and transform it into machine language (bits)!

Input: a program in assembly langauge (MIPS)
 - reads labels
 - reads these R instructions: AND, OR, ADD, SLL, LST, SUB, JR
 - reads these I instructions: ADDI, BEQ, BEN, LW, SW
 - reads these J instructions: J, JAL
 - does not work with $gp or $fp registers, works with all other registers
 - ignores comments

Output: the binary, machine language of the progarm

Implementation description: 
 - Uses a scanner to scan in the file and loop through the lines in the file with .hasNextLine() and .nextLine()
 - Uses a hashmap to hold the instructions and they're corresponding "code" (binary representation)
 - Uses a hashmap to hold the registers and they're corresponding "code" (binary representation)
 - Keep track of address using the line number referenced as "add" in getInst

The whole input is first parsed into the individual instructions and registers. This is done by reading and parsing
each line of the file using getInst() then interpreting the parsed text in getBinInst().

In getInst(), for each line the program first looks for:
1. Comments: If the line starts with "#" it's a comment and the proram moves onto the next line. If there is a "#"
in the middle of the lines, then the substring after the "#" can be ignored.
2. Labels: If the line contains ":" then put the label name and the address (add) is put into a labels HashMap that keeps
track of all the labels. The program then looks at the part of the line after the label. If that part is empty, the
program goes on to the next line. Else the program looks at the instructions for the rest of the line.
3. Instructions: formats each instruction so it's exactly <instruction> <registers>. For example, if the input is 
"add$2,$3,$4" this get formated into "add $2,$3,$4", so there's a space between the instruction and registers. These 
instructions are kept in this <instruction> <register> format as a whole string and saved in a ArrayList instLine.
In other words instLine holds each line of instruction.

Then in getInst(), each element in instLine is split into the correct parts - the instruction and each register is identified.
This is done using .split and splitting the instruction (like this part "add $2,$3,$4") by white space and by commas into 
an array. This array is then added to an ArrayList instParts that holds arrays of parsed instruction lines - instParts is an 
array of arrays.

In getBinInst(), for each instruction in instParts (aka loop through each instruction in instParts) the program first identifies
if the instruction is R, I, or J type. This is done by checking if the first element (instruction name) is in the 
hashmap for R, I, or J instructions. Remeber, this is the format of each instruction in the array:
[ [ <instruction> <rd reg> <rs reg> <rt reg> ] ...]
1. R instructions: organized with an opcode, followed by rs (reg source one), rt (reg source two), rd (reg destination), 
shamt (shift amount), and a funct (identifies inst function). R instructions opcode is always "000000". rs will be the register in 
index 2, rt will be the index in index 3, and the destination is index 1 (the first register provided!). There is a special case
with jr. The funct is added at the end and is the binary code for the actual instruction (i.e. add, sub, mult, etc.).
2. I instructions: organized as opcode, rs (reg source one), rt (reg source two), and an immediate. The opcode here identifies
and is based off of the instruction (ust be looked up in i instruction hashmap).
 -  If the instructions are beq or bne, then the program must find the correct label and the correct binary rep for the label. 
   The rs reg is the first provided register at index 1 and rt is at index 2. Then the label is the immediate.
 - If the instructions are lw and sw, then the destination is put in the rt location. This register is the first provide register
   at index 1. The later part of the instruction will have something like <offset>(<register>). This is parsed and the 
   register goes in to the rs location, and the offset goes into the immediate location.
 - All other instructions (such as addi and subi) follow similarly to R instructions. rs reg gets the second index, rt reg gets the 
   first index (rt holds the destination), and then the immediate is the last index.
3. J instructions: The label must be found first. Then the opcode is looked up based on the instruction. The address is then
   just the location the program is jumping to.

Each of these binary instructions are then stored in an ArrayList binInst as strings. 
