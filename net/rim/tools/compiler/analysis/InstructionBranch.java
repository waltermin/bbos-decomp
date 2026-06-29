package net.rim.tools.compiler.analysis;

import net.rim.tools.compiler.exec.MyArrays;

public class InstructionBranch extends Instruction {
   private InstructionTarget[] _branchTargets;

   public InstructionBranch(int ip, int opcode) {
      this(ip, opcode, 0);
   }

   public InstructionBranch(int ip, int opcode, int op) {
      super(ip, opcode, op);
   }

   public void addBranchTarget(InstructionTarget target) {
      if (this._branchTargets == null) {
         this._branchTargets = new InstructionTarget[1];
      } else {
         this._branchTargets = MyArrays.resize(this._branchTargets, this._branchTargets.length + 1);
      }

      this._branchTargets[this._branchTargets.length - 1] = target;
   }

   public int getNumBranchTargets() {
      return this._branchTargets == null ? 0 : this._branchTargets.length;
   }

   public InstructionTarget getBranchTarget(int index) {
      return this._branchTargets[index];
   }

   @Override
   public void walkInstruction(InstructionWalker wlk) {
      wlk.walkInstruction(this);
   }
}
