package net.rim.tools.compiler.analysis;

import net.rim.tools.compiler.codfile.Code;

public final class InstructionInts extends InstructionBranch {
   private int[] _ops;

   public InstructionInts(int ip, int opcode, int[] ops) {
      this(ip, opcode, ops, false);
   }

   public InstructionInts(int ip, int opcode, int[] ops, boolean verifyError) {
      super(ip, opcode, verifyError ? 1 : 0);
      int n = ops.length;
      if (n == 0) {
         this._ops = ops;
      } else {
         this._ops = new int[n];

         for (int i = 0; i < n; i++) {
            this._ops[i] = ops[i];
         }
      }
   }

   public final int[] getInts() {
      return this._ops;
   }

   @Override
   public final void walkInstruction(InstructionWalker wlk) {
      wlk.walkInstruction(this);
   }

   @Override
   public final int setOffset(int offset, boolean assumeFar) {
      this.setIp(offset);
      return offset + Code.getOpcodeSize(this.getOpcode(), 0, this._ops, assumeFar, true);
   }

   public final boolean getVerifyError() {
      return super._op != 0;
   }
}
