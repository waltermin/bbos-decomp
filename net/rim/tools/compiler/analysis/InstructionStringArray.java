package net.rim.tools.compiler.analysis;

import net.rim.tools.compiler.codfile.Code;

public final class InstructionStringArray extends Instruction {
   private String[] _str;

   public InstructionStringArray(int ip, int opcode, String[] str) {
      super(ip, opcode, 0);
      this._str = str;
   }

   public final String[] getStringArray() {
      return this._str;
   }

   @Override
   public final int setOffset(int offset, boolean assumeFar) {
      this.setIp(offset);
      return offset + Code.getStringArrayInitSize(this.getOpcode(), this._str.length);
   }

   @Override
   public final void walkInstruction(InstructionWalker wlk) {
      wlk.walkInstruction(this);
   }
}
