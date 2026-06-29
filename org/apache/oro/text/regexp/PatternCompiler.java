package org.apache.oro.text.regexp;

public interface PatternCompiler {
   Pattern compile(String var1);

   Pattern compile(String var1, int var2);

   Pattern compile(char[] var1);

   Pattern compile(char[] var1, int var2);
}
