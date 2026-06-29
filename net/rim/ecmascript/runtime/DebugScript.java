package net.rim.ecmascript.runtime;

public interface DebugScript {
   int[] getLineNumbers();

   String getSourceLine(int var1);

   CompiledScript getCompiledScript();

   int getClosestLine(int var1);
}
