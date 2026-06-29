package net.rim.wica.runtime.script;

public final class CompilerException extends Exception {
   public CompilerException(String message) {
   }

   @Override
   public final String toString() {
      String s = "Script Compiler Error";
      String message = this.getMessage();
      return message != null ? s + ": " + message : s;
   }
}
