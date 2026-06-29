package net.rim.wica.runtime.script;

public final class ScriptException extends Exception {
   public ScriptException(String message) {
   }

   @Override
   public final String toString() {
      String s = "Script Execution Error";
      String message = this.getMessage();
      return message != null ? ((StringBuffer)(new Object())).append(s).append(": ").append(message).toString() : s;
   }
}
