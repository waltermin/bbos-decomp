package net.rim.tools.compiler.analysis;

import net.rim.tools.compiler.types.ClassType;
import net.rim.tools.compiler.types.NameAndType;

public final class InstructionNameAndType extends Instruction {
   private ClassType _classType;
   private NameAndType _nameAndType;

   public InstructionNameAndType(int ip, int opcode, ClassType classType, NameAndType nameAndType) {
      this(ip, opcode, classType, nameAndType, 0);
   }

   public InstructionNameAndType(int ip, int opcode, ClassType classType, NameAndType nameAndType, int op) {
      super(ip, opcode, op);
      this._classType = classType;
      this._nameAndType = nameAndType;
   }

   public final ClassType getClassType() {
      return this._classType;
   }

   public final NameAndType getNameAndType() {
      return this._nameAndType;
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
            case 1:
            case 3:
            case 5:
            case 7:
            case 12:
            case 105:
            case 107:
            case 109:
            case 111:
               offset++;
         }
      }

      return offset;
   }
}
