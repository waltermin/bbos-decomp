package java.util;

public class TimerTask implements Runnable {
   final Object lock = new Object();
   int state = 0;
   long nextExecutionTime;
   long period = 0;
   static final int VIRGIN;
   static final int SCHEDULED;
   static final int EXECUTED;
   static final int CANCELLED;

   public long scheduledExecutionTime() {
      synchronized (this.lock) {
         return this.period < 0 ? this.nextExecutionTime + this.period : this.nextExecutionTime - this.period;
      }
   }

   public boolean cancel() {
      synchronized (this.lock) {
         boolean result = this.state == 1;
         this.state = 3;
         return result;
      }
   }

   @Override
   public void run() {
      throw null;
   }

   protected TimerTask() {
   }
}
