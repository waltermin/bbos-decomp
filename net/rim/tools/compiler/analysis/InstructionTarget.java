package net.rim.tools.compiler.analysis;

import net.rim.tools.compiler.codfile.CodfileLabel;

public final class InstructionTarget extends Instruction {
   private InstructionStackEntry _stackEntry;
   private CodfileLabel _label;

   public InstructionTarget(int ip, int op) {
      super(ip, -1, op);
   }

   @Override
   public final void walkInstruction(InstructionWalker wlk) {
      wlk.walkInstruction(this);
   }

   @Override
   public final int setOffset(int offset, boolean assumeFar) {
      this.setIp(offset);
      return offset;
   }

   public final void setStackEntry(InstructionStackEntry stackEntry) {
      this._stackEntry = stackEntry;
   }

   public final InstructionStackEntry getStackEntry() {
      return this._stackEntry;
   }

   public final void setLabel(CodfileLabel label) {
      this._label = label;
   }

   public final CodfileLabel getLabel() {
      return this._label;
   }
}
