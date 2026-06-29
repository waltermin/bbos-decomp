package net.rim.device.apps.api.phone;

import net.rim.vm.Array;

class PostEventRunner implements Runnable {
   private int _count;
   private int[] _event = new int[5];
   private int[] _param = new int[5];
   private Object[] _context = new Object[5];

   public synchronized void postEvent(int event, int param, Object context) {
      if (this._count >= this._event.length) {
         int newMax = this._count + 5;
         Array.resize(this._event, newMax);
         Array.resize(this._param, newMax);
         Array.resize(this._context, newMax);
      }

      this._event[this._count] = event;
      this._param[this._count] = param;
      this._context[this._count] = context;
      this._count++;
      if (this._count == 1) {
         VoiceServices.getUiApplication().invokeLater(this);
      }
   }

   @Override
   public void run() {
      int index = 0;

      while (true) {
         synchronized (this) {
            if (index == this._count) {
               this._count = 0;
               return;
            }
         }

         VoiceServices.broadcastEvent(this._event[index], this._param[index], this._context[index]);
         this._context[index] = null;
         index++;
      }
   }
}
