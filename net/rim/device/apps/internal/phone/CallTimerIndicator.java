package net.rim.device.apps.internal.phone;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.phone.CallTimerListener;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ribbon.indicators.Indicator;
import net.rim.device.apps.api.ribbon.indicators.IndicatorManager;
import net.rim.device.apps.api.ribbon.indicators.TestPoint;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class CallTimerIndicator implements Indicator, PhoneEventListener, CallTimerListener, CallManager$Listener, TestPoint {
   private IndicatorManager _indicatorManager;
   private int _callTimerCount;
   private StringBuffer _strBuffer = (StringBuffer)(new Object());
   private static final int STARTING_CALL_TIMER_VALUE = 0;
   private static final int INVALID_CALL_TIMER_VALUE = -1;
   private static final long GUID = -2142843617924625277L;
   private static CallTimerIndicator _timerInstance;
   private static int _networkType = RadioInfo.getNetworkType();

   private CallTimerIndicator() {
      this._indicatorManager = IndicatorManager.getInstance();
   }

   static final void initialize() {
      _timerInstance.reset();
      VoiceServices.addPhoneEventListener(_timerInstance);
      VoiceServices.addCallTimerListener(_timerInstance);
      CallManager.getInstance().addListener(_timerInstance);
      IndicatorManager indicatorManager = IndicatorManager.getInstance();
      if (indicatorManager != null) {
         indicatorManager.addIndicator(_timerInstance);
      }
   }

   final void reset() {
      this._callTimerCount = -1;
   }

   @Override
   public final void callTimerUpdated(int callId, int time) {
      if (VoiceServices.getCallState(callId) != 0) {
         if ((_networkType == 3 || _networkType == 7) && time >= 1) {
            time--;
         }

         this.setTimerCount(time);
         if (!VoiceServices.getVoiceApplication().inForeground() && this._indicatorManager != null) {
            this._indicatorManager.updateIndicators();
         }
      }
   }

   @Override
   public final void callConnected(int callId) {
   }

   @Override
   public final void phoneEventNotify(int eventId, int param1, Object param2) {
      switch (eventId) {
         case 1002:
         case 1006:
         case 201010:
            this.setTimerCount(-1);
            if (this._indicatorManager != null) {
               this._indicatorManager.updateIndicators();
            }
            break;
         case 1100:
            this.setTimerCount(-1);
            if (this._indicatorManager != null) {
               this._indicatorManager.updateIndicators();
               return;
            }
      }
   }

   @Override
   public final void onCallManagerEvent(int eventId, Vector currentCalls, LiveCall call, int flags, Object context) {
      if (eventId == 10) {
         this.setTimerCount(-1);
         if (this._indicatorManager != null) {
            this._indicatorManager.updateIndicators();
         }
      }
   }

   private final void setTimerCount(int count) {
      this._callTimerCount = count;
   }

   private final int getTimerCount() {
      return this._callTimerCount;
   }

   private final boolean validTimer() {
      return this.getTimerCount() != -1;
   }

   @Override
   public final int getPriority() {
      return 4;
   }

   @Override
   public final String getTypeName() {
      return "calltimer";
   }

   @Override
   public final void test(Object id, Object testvalue) {
      if (testvalue instanceof Object) {
         Integer integer = (Integer)testvalue;
         this.setTimerCount(integer);
      }
   }

   @Override
   public final int draw(Graphics graphics, int width, int height, int flags) {
      int drawWidth = 0;
      if (this.validTimer() && (flags & 4) == 0) {
         LiveCall currentCall = (LiveCall)CallManager.getInstance().getCurrentCall();
         Font font = graphics.getFont();
         this.getTimerString(this._strBuffer);
         drawWidth = font.getBounds(this._strBuffer, 0, this._strBuffer.length());
         drawWidth += this.getIndicatorWidth(font);
         int icon = 11;
         if (currentCall != null && currentCall.isMuted()) {
            icon = 12;
         }

         boolean iconOnLeft = (flags & 1) != 0;
         int xPos = 0;
         if (!iconOnLeft) {
            xPos = width - drawWidth;
         }

         if (iconOnLeft) {
            xPos += PhoneResources.drawIcon(graphics, xPos, 0, icon);
         }

         xPos += graphics.drawText(this._strBuffer, 0, this._strBuffer.length(), xPos, 0, 6, width);
         if (!iconOnLeft) {
            xPos += PhoneResources.drawIcon(graphics, xPos, 0, icon);
         }

         if (currentCall != null && currentCall.hasAdvancedPrivacy()) {
            xPos += PhoneResources.drawIcon(graphics, xPos, 0, 10);
         }
      }

      return drawWidth;
   }

   @Override
   public final int getWidth(Graphics graphics) {
      VoiceServices voiceServices = VoiceServices.getInstance();
      if (this.validTimer() && !voiceServices.inForeground()) {
         Font font = graphics.getFont();
         int width = this.getIndicatorWidth(font);
         this.getTimerString(this._strBuffer);
         return width + font.getBounds(this._strBuffer, 0, this._strBuffer.length());
      } else {
         return 0;
      }
   }

   @Override
   public final int getHeight(Graphics graphics) {
      return graphics.getFont().getHeight();
   }

   private final int getIndicatorWidth(Font font) {
      int width = PhoneResources.getIconWidth(font, 11);
      LiveCall currentCall = (LiveCall)CallManager.getInstance().getCurrentCall();
      if (currentCall != null && currentCall.hasAdvancedPrivacy()) {
         width *= 2;
      }

      return width;
   }

   private final void getTimerString(StringBuffer strbuf) {
      strbuf.setLength(0);
      long elapsedTime = this.getTimerCount();
      if (elapsedTime != -1) {
         DateTimeUtilities.formatElapsedTime(elapsedTime, strbuf, false);
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _timerInstance = (CallTimerIndicator)ar.getOrWaitFor(-2142843617924625277L);
      if (_timerInstance == null) {
         _timerInstance = new CallTimerIndicator();
         ar.put(-2142843617924625277L, _timerInstance);
      }
   }
}
