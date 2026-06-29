package net.rim.ecmascript.runtime;

interface ESThunkInterface {
   long runDWord(String var1, int[] var2, int[] var3, int var4);

   int runWord(String var1, int[] var2, int[] var3, int var4);

   void runVoid(String var1, int[] var2, int[] var3, int var4);
}
