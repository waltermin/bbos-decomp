package com.fourthpass.wmls;

import java.util.Stack;

final class FunctionCall {
   private WMLScript _script;
   private Value[] _vars;
   private byte[] _instructions;
   private int _offset;
   private int _index;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   FunctionCall(WMLScript script, int index, Stack stack) {
      this._script = script;
      this._index = index;
      Function function = script.getFunctionPool().getFunction(this._index);
      int argsNum = function.getNumArgs();
      int varNum = argsNum + function.getNumLocals();
      if (varNum > 0) {
         this._vars = new Value[varNum];

         for (int i = 0; i < argsNum; i++) {
            boolean var10 = false /* VF: Semaphore variable */;

            try {
               var10 = true;
               this._vars[argsNum - 1 - i] = ((Value)stack.pop()).clone();
               var10 = false;
            } finally {
               if (var10) {
                  this._vars[argsNum - 1 - i] = null;
                  continue;
               }
            }
         }

         for (int i = argsNum; i < varNum; i++) {
            this._vars[i] = StringValue.EMPTY_STRING.clone();
         }
      }

      this._instructions = function.getInstructions();
      this._offset = 0;
   }

   final byte getInstruction(int i) {
      return i == this._instructions.length ? 59 : this._instructions[i];
   }

   final int getOffset() {
      return this._offset;
   }

   final WMLScript getScript() {
      return this._script;
   }

   final int getUByte(int i) {
      return 0xFF & this._instructions[i];
   }

   final Value getVar(int i) {
      return this._vars[i].clone();
   }

   final void setOffset(int i) {
      this._offset = i;
   }

   final void setVar(int i, Value value) {
      this._vars[i] = value.clone();
   }
}
