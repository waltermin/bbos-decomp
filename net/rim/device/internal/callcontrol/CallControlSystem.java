package net.rim.device.internal.callcontrol;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Phone;

public final class CallControlSystem {
   private CallCommandHandler _commandHandler;
   private RadioCommandHandler _radioHandler;
   private static final long CC_GUID = 6176826427881664509L;
   private static CallControlSystem _ccSystem;
   static final int ORDER_APPS = 2100;
   static final int ORDER_CC_LOGGER = 2090;
   static final int ORDER_EXTERNAL_MAX = 2000;
   public static final int ORDER_USABILITY = 1900;
   public static final int ORDER_POC = 1400;
   public static final int ORDER_PBX = 1300;
   public static final int ORDER_WLAN_FDN = 530;
   public static final int ORDER_WLAN = 500;
   public static final int ORDER_RADIO_RECTIFY = 150;
   public static final int ORDER_RADIO_FDN = 130;
   static final int ORDER_EXTERNAL_MIN = 100;
   static final int ORDER_RADIO = 10;
   public static final int DISPATCH_EVENT_ALTERNATE_LINES_UPDATED = 5000;
   public static final int DISPATCH_EVENT_CALL_TRANSFER_STATE_UPDATED = 5001;
   public static final int DISPATCH_EVENT_VOICEMAIL_COUNT_UPDATED = 5002;
   public static final int DISPATCH_EVENT_DTMF_DATA = 5003;

   public static final synchronized CallControlSystem getInstance() {
      if (_ccSystem == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _ccSystem = (CallControlSystem)ar.getOrWaitFor(6176826427881664509L);
         if (_ccSystem == null) {
            ar.put(6176826427881664509L, new CallControlSystem());
            _ccSystem = (CallControlSystem)ar.getOrWaitFor(6176826427881664509L);
         }
      }

      return _ccSystem;
   }

   private CallControlSystem() {
      AbstractCallEventHandler.internalRegister(new CallEventDispatcher());
      this._commandHandler = new CallCommandHandler(2100);
      AbstractCallCommandHandler.internalRegister(this._commandHandler);
      this._radioHandler = new RadioCommandHandler();
      new CallControlLogger("CC", 2090);
   }

   public static final Phone getCommandHandler() {
      return getInstance()._commandHandler;
   }

   public final void startListening(Application app) {
      this._radioHandler.startListening(app);
   }
}
