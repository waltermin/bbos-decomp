package net.rim.tools.compiler.analysis;

public final class InstructionBytes extends Instruction {
   private byte[] _bytes;

   public InstructionBytes(int ip, int opcode, int op, byte[] bytes) {
      super(ip, opcode, op);
      this._bytes = bytes;
   }

   public final byte[] getBytes() {
      return this._bytes;
   }

   @Override
   public final void walkInstruction(InstructionWalker wlk) {
      wlk.walkInstruction(this);
   }
}
