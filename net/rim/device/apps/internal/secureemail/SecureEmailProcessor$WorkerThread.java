package net.rim.device.apps.internal.secureemail;

class SecureEmailProcessor$WorkerThread extends Thread {
   public SecureEmailProcessor$WorkerThread() {
   }

   @Override
   public void run() {
      while (true) {
         System.out.println("SecureEmail: Processing message");
         SecureEmailProcessor currentProcessor = (SecureEmailProcessor)SecureEmailProcessor._processorQueue.elementAt(0);
         currentProcessor.doActualProcessingWork();
         synchronized (currentProcessor) {
            currentProcessor.notifyAll();
         }

         synchronized (SecureEmailProcessor._processorQueue) {
            SecureEmailProcessor._processorQueue.removeElementAt(0);
            if (SecureEmailProcessor._processorQueue.size() == 0) {
               System.out.println("SecureEmail: Worker thread terminating");
               SecureEmailProcessor.access$202(null);
               return;
            }
         }
      }
   }
}
