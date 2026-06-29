package net.rim.tools.compiler.analysis;

public final class InstructionLong extends Instruction {
   private long _op2;

   public InstructionLong(int ip, int opcode, int op1, int op2) {
      super(ip, opcode, op1);
      this._op2 = op2;
   }

   public InstructionLong(int ip, int opcode, long op2) {
      super(ip, opcode, 0);
      this._op2 = op2;
   }

   public final long getOp2() {
      return this._op2;
   }

   @Override
   public final void walkInstruction(InstructionWalker wlk) {
      wlk.walkInstruction(this);
   }
}
