package net.rim.ecmascript.runtime;

public interface DebugContext {
   DebugScope getScope();

   DebugScript getScript();

   ESObject getGlobalObject();

   int getLine();

   ThrownValue getThrownValue();

   DebugContext getCallingContext();

   long eval(String var1);

   String getFunctionName();
}
