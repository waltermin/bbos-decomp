package net.rim.tools.compiler.analysis;

public final class InstructionString extends Instruction {
   private String _str;

   public InstructionString(int ip, int opcode, String str) {
      super(ip, opcode, 0);
      this._str = str;
   }

   public final String getString() {
      return this._str;
   }

   @Override
   public final void walkInstruction(InstructionWalker wlk) {
      wlk.walkInstruction(this);
   }
}
