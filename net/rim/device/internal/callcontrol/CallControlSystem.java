package net.rim.device.internal.callcontrol;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Phone;

public final class CallControlSystem {
   private CallCommandHandler _commandHandler;
   private RadioCommandHandler _radioHandler;
   private static final long CC_GUID;
   private static CallControlSystem _ccSystem;
   static final int ORDER_APPS;
   static final int ORDER_CC_LOGGER;
   static final int ORDER_EXTERNAL_MAX;
   public static final int ORDER_USABILITY;
   public static final int ORDER_POC;
   public static final int ORDER_PBX;
   public static final int ORDER_WLAN_FDN;
   public static final int ORDER_WLAN;
   public static final int ORDER_RADIO_RECTIFY;
   public static final int ORDER_RADIO_FDN;
   static final int ORDER_EXTERNAL_MIN;
   static final int ORDER_RADIO;
   public static final int DISPATCH_EVENT_ALTERNATE_LINES_UPDATED;
   public static final int DISPATCH_EVENT_CALL_TRANSFER_STATE_UPDATED;
   public static final int DISPATCH_EVENT_VOICEMAIL_COUNT_UPDATED;
   public static final int DISPATCH_EVENT_DTMF_DATA;

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
