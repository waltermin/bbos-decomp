package net.rim.tools.compiler.util;

public final class DuplicateException extends CompileException {
   private String _where;

   public DuplicateException(String fileName, String dup, String where) {
      super(fileName, dup);
      this._where = where;
   }

   @Override
   public final String getMessage() {
      return "Duplicate definition for '" + super._word + "' found in: " + this._where;
   }
}
