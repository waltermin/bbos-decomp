package net.rim.device.apps.internal.phone.api.livecall;

import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;

class CallTask extends Thread implements PhoneEventListener {
   private long _startTime;
   private static Object _callTaskLock = new Object();
   private static Object _currentTask;
   private static final long TIMEOUT = 10000L;

   public CallTask() {
      VoiceServices.addPhoneEventListener(this);
   }

   static Object getCallTaskLock() {
      return _callTaskLock;
   }

   static Object getCurrentTask() {
      return _currentTask;
   }

   static void setCurrentTask(Object task) {
      _currentTask = task;
   }

   static boolean canExecute() {
      return getCurrentTask() == null;
   }

   static void executeTask(CallTask task) {
      if (task != null) {
         task.start();
      }
   }

   protected boolean isFinished() {
      throw null;
   }

   protected void execute() {
      throw null;
   }

   protected void onPhoneEvent(int _1, int _2, Object _3) {
      throw null;
   }

   @Override
   public void run() {
      this._startTime = System.currentTimeMillis();
      setCurrentTask(this);
      synchronized (getCallTaskLock()) {
         this.execute();

         while (!this.isFinished() && !this.timeoutHasExpired()) {
         }
      }

      _currentTask = null;
      VoiceServices.removePhoneEventListener(this);
   }

   private boolean timeoutHasExpired() {
      long diff = System.currentTimeMillis() - this._startTime;
      return diff > 10000;
   }

   @Override
   public void phoneEventNotify(int eventId, int callId, Object context) {
      this.onPhoneEvent(eventId, callId, context);
   }
}
