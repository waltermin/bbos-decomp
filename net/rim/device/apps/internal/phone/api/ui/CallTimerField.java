package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.phone.CallTimerListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.WeakCallTimerListener;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;

public final class CallTimerField extends LabelField implements CallTimerListener, Runnable {
   private int _time = -1;
   private StringBuffer _buf = new StringBuffer(8);
   private boolean _queuedUpdate;
   private static final boolean _charmScreenOrBetter = Display.getHeight() > 240 && Display.getWidth() > 240;
   private static int _networkType;

   public final void reset() {
      this._time = -1;
      this.showTime();
   }

   @Override
   public final void callTimerUpdated(int callId, int time) {
      LiveCall currCall = (LiveCall)VoiceServices.getVoiceApplication().getCurrentCall();
      if (currCall != null) {
         if (!currCall.showCallTimer()) {
            this.setText("");
         } else if ((_networkType == 3 || _networkType == 7) && time >= 1) {
            synchronized (this) {
               if (!this._queuedUpdate) {
                  this._queuedUpdate = true;
                  this._time = time - 1;
                  VoiceServices.getUiApplication().invokeLater(this);
               }
            }
         } else {
            if (time >= 0) {
               this._time = time;
               this.showTime();
            }
         }
      }
   }

   @Override
   public final void callConnected(int callId) {
   }

   @Override
   public final void run() {
      synchronized (this) {
         this._queuedUpdate = false;
      }

      this.showTime();
   }

   private final void showTime() {
      if (this._time >= 0) {
         DateTimeUtilities.formatElapsedTime(this._time, this._buf, true);
         LiveCall call = (LiveCall)VoiceServices.getVoiceApplication().getCurrentCall();
         if (call != null && call.hasAdvancedPrivacy()) {
            this._buf.append('\uf3aa');
         }

         this.setText(this._buf);
      } else {
         this.setText("");
      }
   }

   @Override
   public final int getPreferredWidth() {
      return 20;
   }

   public CallTimerField(Object call) {
      this((Font)null);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public CallTimerField(Font font) {
      super(null, 1152921504606846976L);
      if (font != null) {
         this.setFont(font);
      } else {
         FontFamily timerFont = null;
         boolean var5 = false /* VF: Semaphore variable */;

         label32:
         try {
            var5 = true;
            timerFont = FontFamily.forName("LargeTimerFont");
            var5 = false;
         } finally {
            if (var5) {
               System.err.println("LargeTimerFont typeface is not found");
               break label32;
            }
         }

         if (timerFont != null) {
            if (_charmScreenOrBetter) {
               this.setFont(timerFont.getFont(1, 44));
            } else {
               this.setFont(timerFont.getFont(1, 14));
            }
         }
      }

      VoiceServices.addCallTimerListener(new WeakCallTimerListener(this));
   }

   public CallTimerField() {
      this((Font)null);
   }

   static {
      if (_charmScreenOrBetter) {
         FontRegistry.loadFont("LargeTimerFont_44B.cbtf", "net_rim_bb_framework_api", "LargeTimerFont");
      } else {
         FontRegistry.loadFont("LargeTimerFont_14B.cbtf", "net_rim_bb_framework_api", "LargeTimerFont");
      }

      _networkType = RadioInfo.getNetworkType();
   }
}
