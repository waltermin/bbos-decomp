package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.analysis.Instruction;
import net.rim.tools.compiler.analysis.InstructionBranch;
import net.rim.tools.compiler.analysis.InstructionBytes;
import net.rim.tools.compiler.analysis.InstructionInts;
import net.rim.tools.compiler.analysis.InstructionLong;
import net.rim.tools.compiler.analysis.InstructionNameAndType;
import net.rim.tools.compiler.analysis.InstructionStackEntry;
import net.rim.tools.compiler.analysis.InstructionString;
import net.rim.tools.compiler.analysis.InstructionStringArray;
import net.rim.tools.compiler.analysis.InstructionTarget;
import net.rim.tools.compiler.analysis.InstructionType;
import net.rim.tools.compiler.analysis.InstructionWalker;
import net.rim.tools.compiler.exec.MyArrays;
import net.rim.tools.compiler.types.ClassType;
import net.rim.tools.compiler.types.NameAndType;
import net.rim.tools.compiler.types.Type;

public final class ByteCodeInstructions {
   private boolean _merged;
   private Instruction _instruction;
   private int[] _opcodes;
   private int[] _ops;
   private int _numInstructions;
   private Instruction[] _instructions;
   private InstructionTarget[] _targets;
   private ByteCodeExceptionHandler[] _exceptionHandlers;
   private int _numStackEntries;
   private int _length;

   public ByteCodeInstructions() {
      this(0);
   }

   public ByteCodeInstructions(int length) {
      this._length = length;
   }

   public final void mergeArray() {
      if (!this._merged) {
         this._merged = true;
         if (this._targets == null) {
            return;
         }

         int targetIndex = this._targets.length;
         Instruction target = this._targets[--targetIndex];
         int targetIp = target.getIp();
         int instructionIndex = this._numInstructions;
         Instruction instruction = this._instructions[--instructionIndex];
         if (instruction == null) {
            instruction = this._instruction.setValueOp(this._opcodes[instructionIndex], this._ops[instructionIndex]);
         }

         int instructionIp = instruction.getIp();
         int index = this._numInstructions + this._targets.length;
         this._opcodes = MyArrays.resize(this._opcodes, index);
         this._ops = MyArrays.resize(this._ops, index);
         this._instructions = MyArrays.resize(this._instructions, index);

         while (target != null || instruction != null) {
            if (instructionIp >= targetIp) {
               this._opcodes[--index] = instruction.getValue();
               this._ops[index] = instruction.getOp();
               if (instruction == this._instruction) {
                  instruction = null;
               }

               this._instructions[index] = instruction;
               if (instructionIndex > 0) {
                  instruction = this._instructions[--instructionIndex];
                  if (instruction == null) {
                     instruction = this._instruction.setValueOp(this._opcodes[instructionIndex], this._ops[instructionIndex]);
                  }

                  instructionIp = instruction.getIp();
               } else {
                  instruction = null;
                  instructionIp = Integer.MIN_VALUE;
               }
            } else {
               index--;
               if (target != null) {
                  this._opcodes[index] = target.getValue();
                  this._ops[index] = target.getOp();
                  this._instructions[index] = target;
               }

               if (targetIndex > 0) {
                  target = this._targets[--targetIndex];
                  targetIp = target.getIp();
               } else {
                  target = null;
                  targetIp = Integer.MIN_VALUE;
               }
            }
         }

         this._numInstructions = this._instructions.length;
      }
   }

   public final int getNumInstructions() {
      return this._numInstructions;
   }

   private final void addInstruction(Instruction instruction) {
      if (this._instructions == null) {
         int num = this._length;
         if (num == 0) {
            num = 8;
         }

         this._opcodes = new int[num];
         this._ops = new int[num];
         this._instructions = new Instruction[num];
         this._instruction = new Instruction(0, 0);
      } else if (this._numInstructions == this._instructions.length) {
         this._opcodes = MyArrays.resize(this._opcodes, this._opcodes.length * 2);
         this._ops = MyArrays.resize(this._ops, this._ops.length * 2);
         this._instructions = MyArrays.resize(this._instructions, this._instructions.length * 2);
      }

      this._instructions[this._numInstructions++] = instruction;
   }

   private final void setOpcode(int ip, int opcode, int op) {
      int index = this._numInstructions - 1;
      this._opcodes[index] = ip << 12 | opcode & 4095;
      this._ops[index] = op;
   }

   public final void addInstructionBranch(int ip, int opcode) {
      this.addInstruction(new InstructionBranch(ip, opcode));
      this.setOpcode(ip, opcode, 0);
   }

   public final void addInstruction(int ip, int opcode) {
      this.addInstruction(null);
      this.setOpcode(ip, opcode, 0);
   }

   public final void addInstruction(int ip, int opcode, int op) {
      this.addInstruction(null);
      this.setOpcode(ip, opcode, op);
   }

   public final void addInstructionBytes(int ip, int opcode, int op, byte[] data) {
      this.addInstruction(new InstructionBytes(ip, opcode, op, data));
      this.setOpcode(ip, opcode, op);
   }

   public final void addInstructionLong(int ip, int opcode, int op, int op2) {
      this.addInstruction(new InstructionLong(ip, opcode, op, op2));
      this.setOpcode(ip, opcode, op);
   }

   public final void addInstructionLong(int ip, int opcode, long op) {
      this.addInstruction(new InstructionLong(ip, opcode, op));
      this.setOpcode(ip, opcode, 0);
   }

   public final void addInstructionStringArray(int ip, int opcode, String[] op) {
      this.addInstruction(new InstructionStringArray(ip, opcode, op));
      this.setOpcode(ip, opcode, 0);
   }

   public final void addInstructionString(int ip, int opcode, String op) {
      this.addInstruction(new InstructionString(ip, opcode, op));
      this.setOpcode(ip, opcode, 0);
   }

   public final void addInstructionType(int ip, int opcode, Type type) {
      this.addInstruction(new InstructionType(ip, opcode, type));
      this.setOpcode(ip, opcode, 0);
   }

   public final void addInstructionType(int ip, int opcode, Type type, int op) {
      this.addInstruction(new InstructionType(ip, opcode, type, op));
      this.setOpcode(ip, opcode, op);
   }

   public final void addInstructionInts(int ip, int opcode, int[] ints, boolean op) {
      this.addInstruction(new InstructionInts(ip, opcode, ints, op));
      this.setOpcode(ip, opcode, 0);
   }

   public final void addInstructionNameAndType(int ip, int opcode, ClassType classType, NameAndType nat) {
      this.addInstruction(new InstructionNameAndType(ip, opcode, classType, nat));
      this.setOpcode(ip, opcode, 0);
   }

   public final void addInstructionNameAndType(int ip, int opcode, ClassType classType, NameAndType nat, int op) {
      this.addInstruction(new InstructionNameAndType(ip, opcode, classType, nat, op));
      this.setOpcode(ip, opcode, op);
   }

   public final void addBranchTarget(InstructionTarget target) {
      Instruction instruction = this._instructions[this._numInstructions - 1];
      if (instruction instanceof InstructionBranch) {
         InstructionBranch instructionBranch = (InstructionBranch)instruction;
         instructionBranch.addBranchTarget(target);
      }
   }

   private final void insertTarget(InstructionTarget target, int index) {
      int length = this._targets.length;
      this._targets = MyArrays.resize(this._targets, length + 1);
      if (index < length) {
         System.arraycopy(this._targets, index, this._targets, index + 1, length - index);
      }

      this._targets[index] = target;
   }

   private final int findTarget(InstructionTarget[] targets, int num, int ip) {
      int start = -1;
      int l = 0;
      int r = num - 1;
      if (num > 0) {
         do {
            int m = (l + r) / 2;
            int instructionIp = targets[m].getIp();
            if (ip < instructionIp) {
               r = m - 1;
            } else {
               if (ip == instructionIp) {
                  start = m - 1;
                  break;
               }

               l = m + 1;
            }
         } while (l <= r);
      }

      if (start < 0) {
         start = (l + r) / 2;
         if (--start < 0) {
            start = 0;
         }
      }

      return start;
   }

   public final int getNumTargets() {
      return this._targets == null ? 0 : this._targets.length;
   }

   public final InstructionTarget getTarget(int index) {
      return this._targets[index];
   }

   public final InstructionTarget plantTarget(int ip, boolean isBranch) {
      int op = isBranch ? 1 : 0;
      InstructionTarget target = null;
      if (this._targets == null) {
         this._targets = new InstructionTarget[1];
         target = new InstructionTarget(ip, op);
         this._targets[0] = target;
      } else {
         int num = this._targets.length;

         int i;
         for (i = this.findTarget(this._targets, num, ip); i < num; i++) {
            target = this._targets[i];
            int targetIp = target.getIp();
            if (targetIp == ip) {
               if (isBranch) {
                  target.setOp(op);
               }

               return target;
            }

            if (targetIp > ip) {
               break;
            }
         }

         target = new InstructionTarget(ip, op);
         this.insertTarget(target, i);
      }

      return target;
   }

   public final void markBadTarget(int index, InstructionTarget target) {
      target.setIp(this._length + 1);
      int length = this._targets.length - 1;
      if (index < length) {
         System.arraycopy(this._targets, index + 1, this._targets, index, length - index);
         this._targets[index] = target;
      }
   }

   public final void allocateExceptionHandlers(int num) {
      if (this._exceptionHandlers == null && num > 0) {
         this._exceptionHandlers = new ByteCodeExceptionHandler[num];
      }
   }

   public final void addExceptionHandler(int index, int start, int end, int handler, ClassType classType) {
      InstructionTarget startTgt = this.plantTarget(start, false);
      InstructionTarget endTgt = this.plantTarget(end, false);
      InstructionTarget handlerTgt = this.plantTarget(handler, true);
      ByteCodeExceptionHandler eh = new ByteCodeExceptionHandler(startTgt, endTgt, handlerTgt, classType);
      this._exceptionHandlers[index] = eh;
   }

   public final int getNumExceptionHandlers() {
      return this._exceptionHandlers == null ? 0 : this._exceptionHandlers.length;
   }

   public final ByteCodeExceptionHandler getExceptionHandler(int index) {
      return this._exceptionHandlers[index];
   }

   public final void addStackEntry(int offset, InstructionStackEntry stackEntry) {
      this.plantTarget(offset, false).setStackEntry(stackEntry);
      this._numStackEntries++;
   }

   public final int getNumStackEntries() {
      return this._numStackEntries;
   }

   public final int setOffsets(int offset, boolean assumeFar) {
      int start = offset;
      int num = this._numInstructions;

      for (int i = 0; i < num; i++) {
         Instruction instruction = this._instructions[i];
         if (instruction == null) {
            instruction = this._instruction.setValueOp(this._opcodes[i], this._ops[i]);
         }

         offset = instruction.setOffset(offset, assumeFar);
         this._opcodes[i] = instruction.getValue();
         this._ops[i] = instruction.getOp();
      }

      this._length = offset - start;
      return offset;
   }

   public final boolean walkBlocks(InstructionWalker wlk) {
      int num = this._numInstructions;

      for (int i = 0; i < num; i++) {
         Instruction instruction = this._instructions[i];
         if (instruction == null) {
            instruction = this._instruction.setValueOp(this._opcodes[i], this._ops[i]);
         }

         instruction.walkInstruction(wlk);
         this._opcodes[i] = instruction.getValue();
         this._ops[i] = instruction.getOp();
      }

      return false;
   }
}
