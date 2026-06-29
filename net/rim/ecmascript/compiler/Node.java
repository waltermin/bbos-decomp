package net.rim.ecmascript.compiler;

import net.rim.ecmascript.runtime.Value;

class Node implements OpcodeConstants {
   void generate(Function _1) {
      throw null;
   }

   Node fold(Function f) {
      return this;
   }

   void generateAndDiscard(Function f) {
      this.generate(f);
      f.addCode(92);
   }

   void generateAndSave(Function f) {
      this.generate(f);
      f.popGlobalReturn();
   }

   void generateAndSaveIfNecessary(Function f) {
      if (f.needGlobalReturn()) {
         this.generateAndSave(f);
      } else {
         this.generateAndDiscard(f);
      }
   }

   void genIf(Function f, Label trueLabel, Label falseLabel) {
      this.generate(f);
      f.addCode(-123);
      f.genIf(trueLabel, falseLabel);
   }

   long getFoldableValue() {
      return Value.UNDEFINED;
   }
}
