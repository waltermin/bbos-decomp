package com.fourthpass.wmls;

final class L5 extends Lib {
   @Override
   public final Value invoke(int func, Interpreter$Engine engine) throws Exception {
      switch (func) {
         case -1:
            throw new Exception("Invalid Function Id");
         case 0:
         default:
            return prompt(engine.popStack(), engine.popStack(), engine.getDialog());
         case 1:
            return confirm(engine.popStack(), engine.popStack(), engine.popStack(), engine.getDialog());
         case 2:
            return alert(engine.popStack(), engine.getDialog());
      }
   }

   public static final Value prompt(Value message, Value defaultInput, IDialog dialog) {
      return message.isString() && defaultInput.isString()
         ? new StringValue(dialog.prompt(((StringValue)message).getValue(), ((StringValue)defaultInput).getValue()))
         : Value.INVALID;
   }

   public static final Value confirm(Value message, Value ok, Value cancel, IDialog dialog) {
      return message.isString() && ok.isString() && cancel.isString()
         ? new StringValue(dialog.confirm(((StringValue)message).getValue(), ((StringValue)ok).getValue(), ((StringValue)cancel).getValue()))
         : Value.INVALID;
   }

   public static final Value alert(Value message, IDialog dialog) {
      return message.isString() ? new StringValue(dialog.alert(((StringValue)message).getValue())) : Value.INVALID;
   }
}
