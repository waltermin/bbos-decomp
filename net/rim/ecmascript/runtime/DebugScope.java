package net.rim.ecmascript.runtime;

public interface DebugScope {
   DebugScope getOuter();

   ESObject getObject();

   long getThis();
}
