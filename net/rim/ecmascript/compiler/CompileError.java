package net.rim.ecmascript.compiler;

import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;

public class CompileError extends Error {
   private boolean _inEval;
   private String _message;
   private int _lineNumber;
   private String _line;
   private int _column;

   CompileError(String error) {
      this(error, null);
   }

   CompileError(String error, String s1) {
      this(error, s1, null);
   }

   CompileError(String error, String s1, String s2) {
      this._message = Misc.replace(error, s1, s2);
      this._lineNumber = -1;
   }

   @Override
   public String toString() {
      if (this._lineNumber == -1) {
         return this._message;
      }

      String s;
      if (this._inEval) {
         s = Misc.replace(Resources.getString(8), this._message);
      } else {
         s = Misc.replace(Resources.getString(17), Integer.toString(this._lineNumber), this._message);
      }

      return ((StringBuffer)(new Object())).append(s).append('\n').append(this.getOffendingSource()).append('\n').toString();
   }

   void setTokenInfo(boolean inEval, String line, int lineNumber, int column) {
      this._inEval = inEval;
      this._lineNumber = lineNumber;
      this._column = column;
      this._line = line;
   }

   @Override
   public String getMessage() {
      return this._message;
   }

   public int getLineNumber() {
      return this._lineNumber;
   }

   public String getOffendingSource() {
      return this._line;
   }

   public String getCaretMarker() {
      StringBuffer b = (StringBuffer)(new Object());

      for (int i = 0; i < this._column; i++) {
         b.append(" ");
      }

      b.append("^");
      return b.toString();
   }
}
