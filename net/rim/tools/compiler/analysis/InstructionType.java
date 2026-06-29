package net.rim.tools.compiler.analysis;

import net.rim.tools.compiler.types.Type;

public final class InstructionType extends InstructionBranch {
   private Type _type;

   public InstructionType(int ip, int opcode, Type type) {
      this(ip, opcode, type, 0);
   }

   public InstructionType(int ip, int opcode, Type type, int op) {
      super(ip, opcode, op);
      this._type = type;
   }

   public final Type getType() {
      return this._type;
   }

   @Override
   public final void walkInstruction(InstructionWalker wlk) {
      wlk.walkInstruction(this);
   }

   @Override
   public final int setOffset(int offset, boolean assumeFar) {
      offset = super.setOffset(offset, assumeFar);
      if (assumeFar) {
         switch (this.getOpcode()) {
            case 168:
            case 170:
            case 184:
            case 186:
            case 191:
            case 193:
            case 195:
            case 198:
            case 200:
               offset++;
         }
      }

      return offset;
   }
}
