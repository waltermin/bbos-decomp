package net.rim.wica.runtime.util;

public class NullProgressMonitor implements ProgressMonitor {
   @Override
   public void beginTask(String name, int totalWorkUnits) {
   }

   @Override
   public void cancel() {
   }

   @Override
   public void done() {
   }

   @Override
   public boolean isCanceled() {
      return false;
   }

   @Override
   public void subTask(String name) {
   }

   @Override
   public void worked(int units) {
   }
}
