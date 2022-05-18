Lab 4 in CPE 315 (Computer Architecture)

Building off lab 2 and 3

Purpose: Read in assembly language and print out the pc and pipeline stages (4 stage pipeline). Take into account stalls and squashes.
   There is an interactive part where the following commands can be entered:

Input: A file in assembly langauge.
h = show help
d = dump register state
p = show pipeline registers
s = step through a single clock cycle (i.e. simulate 1 cycle and stop)
s num = step through num clock cycles
r = run until the program ends and display timing summary
m num1 num2 = display data memory from location num1 to num2
c = clear all registers, memory, and the program counter to 0
q = exit the program

Output: The program counter and the current instruction in each stage of the pipeline. 

Implementation description:
In our pipeline, we have 4 stages. If we are jumping, then the next instruction must be 

- InstR, InstJ, InstI stay the same, since the execution of each instruction does not change
- Instruction abstract class is added onto from lab 3. A function step is included that keeps track of the state of the pipeline and
  executes the actual instruction if necessary. Step uses update_pipeline() and to check if the instructinos need to
  branch, load word, or jump. Depending on the situation and the registers involved, a squash r stall might be necessary.
  print_pipeline() is also included just to print the pipeline to the console.
- There is a state[] array that holds the 4 states of the pipleine. state[] is static and reflects what
  is in the pipeline.
Printing the pipeline is the new thing! The instructions executed and the cycles gone through are also tracked:
- The inst_count is the number of instructions (every step) that has been executed. An instruction is not necessarily executed each 
  cycle.
- The cycle_count is the number of cycles that have gone through the pipeline.
- CPI is the cycles per instruction so that's cycle_count/inst_count

Step
When going through one instruction, first check that the pc is "in range" - aka still less than the number of instructions.
If so, then add one to the pc. Then we update the pipeline. The update takes the registers, the instruct (all the instructions),
the state, and the memory. After updating the pipline, get the correct instruction that should be executed. In this program, 
this is the instruction at the updated pc - 1 (aka the previous instruction, that's the instruction that is now in id/exe). Based
on the updated_pipline, if the instruction should be executed, execute the instruction and return 1. else return 0. The 1 and 0 is
used to count the inst_count (aka if the instruction was executed).

update_pipeline
Get the current instruction executing at pc-1 and name it current. Get the new instrcution at pc and place that in state[0] 
- technically this is replacing the current state[0], but this is okay because we have that current instruction
(the first step, pc-1). Shift all the states down one - the last state gets bumped out. Update the the executing state, state[1]
(the one that was "replaced") using current. put the current.op (instruction label) into state[1]. Then check the branches, 
the lw, and the jump to see if state[0] or any of the other states, need to be squashed/stalled. These are done using check_branch
and check_lw.