package net.rim.device.cldc.io.sms;

import net.rim.device.api.io.DatagramStatusListener;

class SMSSegmentListener implements DatagramStatusListener {
   private DatagramStatusListener _listener;
   private int _totalSegments;
   private int _dgramId;
   private int _sentSegments;
   private boolean _errorOccurred;

   boolean errorOccurred() {
      return this._errorOccurred;
   }

   @Override
   public void updateDatagramStatus(int dgId, int code, Object context) {
      switch (code) {
         case -1:
         case 3:
         case 4:
            if (this._listener != null) {
               this._listener.updateDatagramStatus(this._dgramId, code, context);
            }

            this._errorOccurred = true;
            break;
         case 0:
            if (++this._sentSegments == this._totalSegments && this._listener != null) {
               this._listener.updateDatagramStatus(this._dgramId, code, context);
            }
            break;
         case 1:
         case 2:
         default:
            if (this._sentSegments == 0 && this._listener != null) {
               this._listener.updateDatagramStatus(this._dgramId, code, context);
            }

            return;
         case 5:
            if (this._sentSegments == this._totalSegments && this._listener != null) {
               this._listener.updateDatagramStatus(this._dgramId, code, context);
            }

            return;
      }

      synchronized (this) {
         this.notify();
      }
   }

   SMSSegmentListener(DatagramStatusListener listener, int totalSegments, int dgramId) {
      this._listener = listener;
      this._totalSegments = totalSegments;
      this._dgramId = dgramId;
   }
}
