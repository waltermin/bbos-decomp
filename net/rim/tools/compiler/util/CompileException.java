package net.rim.tools.compiler.util;

public class CompileException extends Exception {
   protected int _resultCode;
   protected String _fileName;
   protected String _word;

   public CompileException(int resultCode, String fileName, String word) {
      super(word);
      this._resultCode = resultCode;
      this._fileName = fileName;
      this._word = word;
   }

   public CompileException(String fileName, String word) {
      this(0, fileName, word);
   }

   public CompileException(String word) {
      this(0, null, word);
   }

   public int getResultCode() {
      return this._resultCode;
   }

   @Override
   public String getMessage() {
      return this._word;
   }

   @Override
   public String toString() {
      StringBuffer buf = (StringBuffer)(new Object());
      if (this._fileName != null) {
         buf.append(this._fileName).append(": ");
      }

      buf.append("Error!");
      if (this._resultCode != 0) {
         buf.append(this._resultCode);
      }

      return buf.append(": ").append(this.getMessage()).toString();
   }
}
