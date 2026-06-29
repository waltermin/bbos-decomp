package net.rim.device.apps.api.phone;

import net.rim.device.api.system.RadioInfo;

class VoiceServices$1StopCallRunnable implements Runnable {
   private int _callId;
   private int _timeRemaining;
   private int _retryInterval;

   public VoiceServices$1StopCallRunnable(int callId, int timeout) {
      this._callId = callId;
      this._timeRemaining = timeout;
      this._retryInterval = 150;
   }

   @Override
   public void run() {
      try {
         VoiceServices._phone.stopCall(this._callId);
         System.out.println(((StringBuffer)(new Object("Phone.stopCall "))).append(this._callId).append(" successful.").toString());
      } finally {
         System.out.println(((StringBuffer)(new Object("Phone.stopCall "))).append(this._callId).append(" failed.").toString());
         if (this._timeRemaining > 0 && RadioInfo.getNetworkType() == 5) {
            VoiceServices.getUiApplication().invokeLater(this, this._retryInterval, false);
            this._timeRemaining = this._timeRemaining - this._retryInterval;
            this._retryInterval = this._retryInterval * 12 / 11;
         }

         return;
      }
   }
}
