package com.fourthpass.wmls;

final class L4 extends Lib {
   @Override
   public final Value invoke(int func, Interpreter$Engine engine) {
      switch (func) {
         case -1:
            throw new Object("Invalid Function Id");
         case 0:
         default:
            return getVar(engine.popStack(), engine.getBrowser());
         case 1:
            return setVar(engine.popStack(), engine.popStack(), engine.getBrowser());
         case 2:
            return go(engine.popStack(), engine.getBrowser());
         case 3:
            return prev(engine.getBrowser());
         case 4:
            return newContext(engine.getBrowser());
         case 5:
            return getCurrentCard(engine.getBrowser());
         case 6:
            return refresh(engine.getBrowser());
      }
   }

   public static final Value setVar(Value string, Value value, IBrowser browser) {
      if (!string.isInvalid()) {
         string = string.toStringValue();
      } else {
         string = Value.INVALID;
      }

      if (value.isString() && string.isString()) {
         String s = value.toString();
         String s1 = string.toString();
         if (!isValidVariableName(s)) {
            return Value.INVALID;
         }

         if (!isValidVariableValue(s1)) {
            return Value.INVALID;
         }

         browser.setVar(s, s1);
         return BooleanValue.TRUE;
      } else {
         return Value.INVALID;
      }
   }

   public static final Value getVar(Value value, IBrowser browser) {
      if (value.isInvalid()) {
         return Value.INVALID;
      }

      if (!value.isString()) {
         return StringValue.EMPTY_STRING;
      }

      String sValue = ((StringValue)value).getValue();
      int valueLength = sValue.length();

      for (int i = 0; i < valueLength; i++) {
         char c = sValue.charAt(i);
         if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < '0' || c > '9') && c != '_') {
            return Value.INVALID;
         }
      }

      return new StringValue(browser.getVar(sValue));
   }

   public static final Value go(Value value, IBrowser browser) {
      if (value.isInvalid()) {
         return Value.INVALID;
      } else {
         return value.isString() ? new StringValue(browser.go(((StringValue)value).getValue())) : StringValue.EMPTY_STRING;
      }
   }

   public static final Value prev(IBrowser browser) {
      return new StringValue(browser.prev());
   }

   public static final Value newContext(IBrowser browser) {
      browser.newContext();
      return StringValue.EMPTY_STRING;
   }

   public static final Value getCurrentCard(IBrowser browser) {
      return new StringValue(browser.getCurrentCard(true));
   }

   public static final Value refresh(IBrowser browser) {
      return new StringValue(browser.refresh());
   }

   private static final boolean isValidVariableValue(String s) {
      if (s != null && s.length() > 0) {
         for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\t' && c != '\r' && c != '\n' && (c < ' ' || c > '\ud7ff') && (c < '\ue000' || c > '�')) {
               return false;
            }
         }
      }

      return true;
   }

   private static final boolean isValidVariableName(String s) {
      boolean flag = false;
      if (s != null && s.length() > 0) {
         flag = true;

         for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '_' && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < '0' || c > '9' || i <= 0)) {
               return false;
            }
         }
      }

      return flag;
   }
}
