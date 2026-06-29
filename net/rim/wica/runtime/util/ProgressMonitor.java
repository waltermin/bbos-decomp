package net.rim.wica.runtime.util;

public interface ProgressMonitor {
   void beginTask(String var1, int var2);

   void subTask(String var1);

   void done();

   void cancel();

   boolean isCanceled();

   void worked(int var1);
}
