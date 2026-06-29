package net.rim.wica.runtime.script;

public interface ScriptMonitor {
   long getTimeoutValue();

   void scriptPreExecute();

   void scriptPostExecute();

   void scriptUnhandledError(String var1, Exception var2);

   void scriptTimedOut();
}
