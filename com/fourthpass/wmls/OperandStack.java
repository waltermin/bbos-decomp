package com.fourthpass.wmls;

import java.util.Stack;

final class OperandStack extends Stack {
   public final void pushValue(Value v) {
      this.push(v);
   }

   public final Value popValue() {
      return (Value)this.pop();
   }
}
