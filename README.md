MIPS Simulator

Purpose: Create a program that reads in and manipulates a program in assembly language based on the MIPS instruction set architecture.

AssemblyTranslator

Translates assembly language into machine langauge. This is done by first parsing each program into the labels, comments, instruction types, registers. Each instruction has a specific type - I, R, or J instruction type. The instruction type is decifered through hashmaps. Each instruction type has a hashmap holding the instruction name (i.e. add, addi, sub, lw, jal, etc.) and the associated binary value for the instruction. Each register also has a binary code that is held in a register. Each instruction can be represented as a 32 bit number. The organization of each instrution depends on the instrution type. Look at the MIPS reference to see the different instrution organization. See AseemblyTranslator directory for more details.

AssemblyExecution

This is builing off the assembly translator. The parsing of the assembly language is essentially the same. The program is parsed into labels, comments, instruction types, and registers. Instead of converting the program into machine language, the program creates an instance of each instruction type. This is done using an Instructions interface and I, R, and J instruction classes. Since each instruction is organized differently, the I, R, and J classes have hte source and destination registers in different locations within the instruciton statement. This is handled separately by each instruction class. See AssemblyExecution for more details.

PipeliningDisplay

Again, building off the previous two programs, the pipelining display executes the assembly langauge while also keeping track of pipeline information. An instruction may impact the pipeline if there are instructions skipped, or if the program reads and writes to the same register. Instructions that are skipped are squashed. This occurs with instructions like branching and jumping. Conflicts with register usage may happen with loading data into a register (lw). Conflicts with registers must result in a stall. This pipelining is based on a simple 4 stage pipeline where each instruction takes one cycle. The cycles and the instructions executed are tracked, and the CPI can be calculated. See PipeliningDisplay for more details.
