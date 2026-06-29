package net.rim.device.apps.api.phone;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.DirectConnectListener;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.PhoneListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardATListener;
import net.rim.device.api.system.SIMCardStatusListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.vm.PersistentInteger;
import net.rim.vm.Process;

public final class VoiceServices implements PhoneListener, HolsterListener, SIMCardStatusListener, SIMCardATListener, DirectConnectListener, SystemListener2 {
   private VoiceApplication _voiceApp;
   private Object[] _phoneEventListeners;
   private Object[] _callTimerListeners;
   private PhoneNumberFilter _phoneNumberFilter;
   private PostEventRunner _postEventRunner = new PostEventRunner();
   private int _lastConnectedCallId = 0;
   private int _lastDisconnectedCallId = 0;
   private final Integer _talkStatusCanTalk = (Integer)(new Object(1));
   private final Integer _talkStatusCannotTalk = (Integer)(new Object(2));
   private final Integer _talkStatusPushToTalk = (Integer)(new Object(3));
   private final Integer _callTypePrivate = (Integer)(new Object(1));
   private final Integer _callTypeAlert = (Integer)(new Object(2));
   private final Integer _callTypeGroup = (Integer)(new Object(3));
   public static final long GUID = 5533786620429140277L;
   public static final long PHONE_OPTIONS_SYNC_ITEM = 9065083732853317491L;
   public static final long PHONE_REQUEST_UNLOCK = 1981938861510850567L;
   public static final int CONTINUE_WITH_ACTUAL_CALL = -1;
   public static final long GLOBAL_EVENT_CALL_ANSWERED = -5577605925563127340L;
   public static final long GLOBAL_EVENT_CALL_STARTED = -3502867315182341540L;
   public static final long RESTORE_CONNECTIONS_RUNNABLE = 1185883946270450222L;
   private static VoiceServices _instance;
   private static Phone _phone = Phone.getInstance();
   private static int VERIFY_BEFORE_STARTING_CALL_ID = PersistentInteger.getId(2687054694005903237L, 0);
   private static PhoneState _phoneState = new PhoneState();
   private static final byte[] _outgoingCallPermissionBitsGSM = new byte[]{1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
   private static final byte[] _outgoingCallPermissionBitsCDMA = new byte[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

   final void doSetVoiceApplication(VoiceApplication voiceApp) {
      if (voiceApp == null) {
         throw new Object("Voice application instance is null.");
      }

      this._voiceApp = voiceApp;
      if (this._voiceApp instanceof Object) {
         Application app = (Application)voiceApp;
         app.addRadioListener(this);
         app.addHolsterListener(this);
         app.addSystemListener(this);
         if (RadioInfo.getNetworkType() != 4) {
            SIMCard.addListener(app, this);
         }

         Audio.addListener(app, AudioRouter.getInstance());
      } else {
         throw new Object("Voice application is not a valid Application instance.");
      }
   }

   final synchronized void addListener(PhoneEventListener listener) {
      this._phoneEventListeners = ListenerUtilities.addListener(this._phoneEventListeners, listener);
   }

   final synchronized void removeListener(PhoneEventListener listener) {
      this._phoneEventListeners = ListenerUtilities.removeListener(this._phoneEventListeners, listener);
   }

   final synchronized void internalAddCallTimerListener(Object listener) {
      this._callTimerListeners = ListenerUtilities.addListener(this._callTimerListeners, listener);
   }

   final synchronized void internalRemoveCallTimerListener(Object listener) {
      this._callTimerListeners = ListenerUtilities.removeListener(this._callTimerListeners, listener);
   }

   final void internalPostEvent(int eventId, int param, Object context) {
      this._postEventRunner.postEvent(eventId, param, context);
   }

   final void dispatchPhoneEvent(int eventId, int param, Object context) {
      Object[] listeners = this._phoneEventListeners;
      if (listeners != null) {
         for (Object listener : listeners) {
            ((PhoneEventListener)listener).phoneEventNotify(eventId, param, context);
         }
      }
   }

   public final boolean inForeground() {
      return this._voiceApp.inForeground();
   }

   @Override
   public final void dtmfData(int dtmf) {
   }

   @Override
   public final void alternateLinesUpdated() {
      this.dispatchPhoneEvent(150090, 0, null);
   }

   @Override
   public final void voicemailCountUpdated(int line, int count) {
      VoicemailIconManager.getInstance().setVoicemailCount(line, count);
   }

   @Override
   public final void callIncoming(int callId) {
      this.dispatchPhoneEvent(1000, callId, null);
   }

   @Override
   public final void callWaiting(int callId) {
      this.dispatchPhoneEvent(1005, callId, null);
   }

   @Override
   public final void callInitiated(int callId) {
      this.dispatchPhoneEvent(1100, callId, null);
   }

   @Override
   public final void callConnected(int callId) {
      switch (RadioInfo.getNetworkType()) {
         case 3:
         case 7:
            if (this._lastConnectedCallId == callId) {
               this._lastConnectedCallId = 0;
               return;
            }

            this._lastConnectedCallId = callId;
      }

      this.dispatchPhoneEvent(1001, callId, null);
      Object[] listeners = this._callTimerListeners;
      if (listeners != null) {
         int size = listeners.length;

         for (int i = 0; i < size; i++) {
            ((CallTimerListener)listeners[i]).callConnected(callId);
         }
      }
   }

   @Override
   public final void callFailed(int callId, int reason) {
      this.dispatchPhoneEvent(1006, callId, new Object(reason));
   }

   @Override
   public final void callDelivered(int callId) {
      this.dispatchPhoneEvent(1011, callId, null);
   }

   @Override
   public final void callManipulateFailed(int callId, int reason) {
      this.dispatchPhoneEvent(1009, callId, new Object(reason));
   }

   @Override
   public final void callDisconnected(int callId) {
      switch (RadioInfo.getNetworkType()) {
         case 3:
         case 7:
            if (callId == this._lastDisconnectedCallId && callId != 32769) {
               return;
            } else {
               this._lastDisconnectedCallId = callId;
            }
         default:
            this.dispatchPhoneEvent(1002, callId, null);
      }
   }

   @Override
   public final void callHeld(int callId) {
      this.dispatchPhoneEvent(1003, callId, null);
   }

   @Override
   public final void callResumed(int callId) {
      this.dispatchPhoneEvent(1004, callId, null);
   }

   @Override
   public final void callAdded(int callId) {
      this.dispatchPhoneEvent(1007, callId, null);
   }

   @Override
   public final void callRemoved(int callId) {
      this.dispatchPhoneEvent(1008, callId, null);
   }

   @Override
   public final void callTransferred(int status, int reason) {
      System.out.println(((StringBuffer)(new Object("PHONE-callTx("))).append(status).append(",").append(reason).append(")").toString());
      this.dispatchPhoneEvent(1010, 0, new int[]{status, reason});
   }

   @Override
   public final void callTimerUpdated(int callId, int time) {
      Object[] listeners = this._callTimerListeners;
      if (listeners != null) {
         int size = listeners.length;

         for (int i = 0; i < size; i++) {
            ((CallTimerListener)listeners[i]).callTimerUpdated(callId, time);
         }
      }
   }

   @Override
   public final void callDisplayUpdated(int callId) {
      this.dispatchPhoneEvent(2200, callId, null);
   }

   @Override
   public final void callVoicePrivacyUpdated(int callId, boolean on) {
      this.dispatchPhoneEvent(150060, callId, new Object(on));
   }

   @Override
   public final void callOTAStatusUpdated(int callId, int status) {
      this.dispatchPhoneEvent(150070, callId, new Object(status));
   }

   @Override
   public final void ssRequestSucceeded(int ss, int action, int result, int bearerService, boolean isUSSDCmd, boolean forwardingNumberAvailable) {
      System.out
         .println(
            ((StringBuffer)(new Object("SS_RQ_OK (")))
               .append(ss)
               .append(", ")
               .append(action)
               .append(", ")
               .append(result)
               .append(", ")
               .append(bearerService)
               .append(", ")
               .append(forwardingNumberAvailable)
               .toString()
         );
      int[] params = new int[]{ss, action, result, bearerService, forwardingNumberAvailable ? 1 : 0};
      this.dispatchPhoneEvent(5000, 0, params);
   }

   @Override
   public final void ssRequestFailed(int reason, int bearerService, boolean isUSSDCmd) {
      System.out.println(((StringBuffer)(new Object("SS_RQ_FAILED ("))).append(reason).append(", ").append(bearerService).append(')').toString());
      int[] params = new int[]{reason, bearerService};
      this.dispatchPhoneEvent(5001, reason, params);
   }

   @Override
   public final void ssRequestRejected(boolean isUSSDCmd) {
      System.out.println("SS_RQ_REJECTED");
      this.dispatchPhoneEvent(5002, 0, null);
   }

   @Override
   public final void ssRequestReleased(boolean isUSSDCmd) {
      System.out.println("SS_REQUEST_RELEASED");
      this.dispatchPhoneEvent(5003, isUSSDCmd ? 1 : 0, null);
   }

   @Override
   public final void ssRequestInvalidPassword() {
      System.out.println("SS_REQUEST_INVALID_PASSWORD");
      this.dispatchPhoneEvent(5004, 0, null);
   }

   @Override
   public final void ssPasswordRequested(int requestType) {
      this.dispatchPhoneEvent(5005, requestType, null);
   }

   @Override
   public final void ssUpdated(int ssOption, int state) {
      System.out.println(((StringBuffer)(new Object("SS_UPDATED ("))).append(state).append(')').toString());
      this.dispatchPhoneEvent(5008, 0, new int[]{ssOption, state});
   }

   @Override
   public final void ssNotification(int ssOption) {
      this.dispatchPhoneEvent(5007, ssOption, null);
   }

   @Override
   public final void ssUssDisplay(byte[] data, int messageCoding, boolean collectInput) {
      try {
         String msg;
         if (messageCoding == 0) {
            msg = (String)(new Object(data, "SMS"));
         } else {
            msg = (String)(new Object(data));
         }

         this.dispatchPhoneEvent(5100, collectInput ? 1 : 0, msg);
      } finally {
         return;
      }
   }

   @Override
   public final void featureReady() {
      System.out.println("SS_FEATURE_READY");
      this.dispatchPhoneEvent(150110, 0, null);
   }

   @Override
   public final void responseEnableFDN(int status) {
   }

   @Override
   public final void voiceLineChanged(int line) {
      this.dispatchPhoneEvent(150130, line, null);
   }

   @Override
   public final void cardInserted() {
   }

   @Override
   public final void cardReady() {
      System.out.println("EV_SIM_VALID");
      broadcastEvent(100200);
   }

   @Override
   public final void cardUpdated() {
      System.out.println("EV_SIM_UPDATED");
      broadcastEvent(100203);
   }

   @Override
   public final void cardInvalid(int reason, int subReason) {
      System.out.println(((StringBuffer)(new Object("EV_SIM_INVALID "))).append(reason).append(" : ").append(subReason).toString());
      broadcastEvent(100201);
   }

   @Override
   public final void cardFault(int reason) {
   }

   @Override
   public final void smsEFFull() {
   }

   @Override
   public final void responseDeleteSMS(int status, int packetId) {
   }

   @Override
   public final void responseMarkSMSAsRead(int status, int packetId) {
   }

   @Override
   public final void atCallControl(int status, int callId) {
      postEvent(10100, status, new Object(callId));
   }

   @Override
   public final void atSetUpCall(byte[] firstAlphaID, byte[] secondAlphaID, boolean askUser, int action) {
      if (askUser) {
         postEvent(10200, action, new byte[][][]{(byte[][])firstAlphaID, (byte[][])secondAlphaID});
      } else {
         postEvent(10201, action, new byte[][][]{(byte[][])firstAlphaID, (byte[][])secondAlphaID});
      }
   }

   @Override
   public final void atDisplayText(byte[] data, int messageCoding, boolean highPriority, boolean userClear, boolean immediateResponse) {
   }

   @Override
   public final void atGetInkey(byte[] data, int messageCoding, int allowedKeys, boolean helpAvailable, int inputMessageCoding) {
   }

   @Override
   public final void atGetInput(
      byte[] data,
      byte[] defaultText,
      int messageCoding,
      int allowedKeys,
      int minLength,
      int maxLength,
      boolean echo,
      boolean helpAvailable,
      int inputMessageCoding
   ) {
   }

   @Override
   public final void atSelectItem(byte[] title, Object[] items, int[] ids, int defaultId, boolean helpAvailable) {
   }

   @Override
   public final void atSetUpMenu(byte[] title, Object[] items, int[] ids, boolean helpAvailable) {
   }

   @Override
   public final void atPlayTone(byte[] data) {
   }

   @Override
   public final void atLaunchBrowser(int browserId, byte[] url, byte[] bearerList, byte[] message, int messageCoding, byte[] data) {
   }

   @Override
   public final void atSessionEnd() {
   }

   @Override
   public final void atTimeout() {
   }

   @Override
   public final void atSetUpIdleModeText(byte[] data, int messageCoding) {
   }

   @Override
   public final void atDisplayAlphaID(byte[] data) {
   }

   @Override
   public final void inHolster() {
      this.dispatchPhoneEvent(100300, 0, null);
   }

   @Override
   public final void outOfHolster() {
      this.dispatchPhoneEvent(100301, 0, null);
   }

   @Override
   public final void dcCallConnected(int callId, int callType, boolean incoming) {
      int[] params = new int[]{callType, incoming ? 1 : 0};
      this.dispatchPhoneEvent(201000, callId, params);
   }

   @Override
   public final void dcCallDisconnected(int callId, int callType, int reason) {
      this.dispatchPhoneEvent(201010, callId, new Object(reason));
   }

   @Override
   public final void dcRequestFailed(int callId, int callType, int reason) {
      this.dispatchPhoneEvent(202000, callId, new Object(reason));
   }

   @Override
   public final void dcCallStatusUpdated(int callId, int callType) {
      this.dispatchPhoneEvent(202010, callId, this.callTypeParameter(callType));
   }

   @Override
   public final void dcTalkStatusUpdated(int callId, int callType, int talkStatus) {
      this.dispatchPhoneEvent(202011, callId, this.talkStatusParameter(talkStatus));
   }

   @Override
   public final void dcTalkGroupIdUpdated(int callId, boolean success, int talkGroupId) {
      if (success) {
         this.dispatchPhoneEvent(202012, talkGroupId, null);
      } else {
         this.dispatchPhoneEvent(202013, 0, null);
      }
   }

   @Override
   public final void dcCallAlertUpdate(int callId, int alertState, int reason) {
      switch (alertState) {
         case 0:
            throw new Object("Unknown alert state.");
         case 1:
         default:
            this.dispatchPhoneEvent(202020, callId, null);
            return;
         case 2:
            this.dispatchPhoneEvent(202021, callId, null);
            return;
         case 3:
            this.dispatchPhoneEvent(202022, callId, null);
            return;
         case 4:
            this.dispatchPhoneEvent(202023, callId, new Object(reason));
      }
   }

   @Override
   public final void dcServiceUpdated(int service, boolean success, boolean enabled) {
      if (success) {
         this.dispatchPhoneEvent(202030, service, new Object(enabled));
      } else {
         this.dispatchPhoneEvent(202031, service, null);
      }
   }

   @Override
   public final void callTransferStateUpdated(int callId, int state) {
      this.dispatchPhoneEvent(150080, callId, null);
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void powerUp() {
      this.dispatchPhoneEvent(4050, 0, null);
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerOffRequested(int reason) {
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
   }

   @Override
   public final void backlightStateChange(boolean on) {
   }

   @Override
   public final void usbConnectionStateChange(int state) {
   }

   @Override
   public final void fastReset() {
      this._lastDisconnectedCallId = 0;
      this._lastConnectedCallId = 0;
      this.dispatchPhoneEvent(180000, 0, null);
   }

   public static final int getCallState(int callId) {
      int state = 0;

      try {
         return _phone.getCallState(callId);
      } finally {
         ;
      }
   }

   public static final boolean rejectCall(int callId) {
      try {
         _phone.rejectCall(callId);
         return true;
      } finally {
         ;
      }
   }

   public static final boolean holdCall() {
      try {
         _phone.holdCall();
         return true;
      } finally {
         ;
      }
   }

   public static final boolean resumeCall() {
      try {
         _phone.resumeCall();
         return true;
      } finally {
         ;
      }
   }

   public static final String getCallName(int callId) {
      return getCallName(callId, false);
   }

   public static final String getCallName(int callId, boolean original) {
      try {
         return _phone.getCallName(callId, original);
      } finally {
         ;
      }
   }

   public static final boolean flash(String number) {
      try {
         _phone.flash(number);
         return true;
      } finally {
         ;
      }
   }

   public static final boolean swapCalls() {
      try {
         _phone.swapCalls();
         return true;
      } finally {
         ;
      }
   }

   public static final boolean joinCalls() {
      try {
         getInstance().dispatchPhoneEvent(3007, 0, null);
         _phone.addCallToConference();
         return true;
      } finally {
         ;
      }
   }

   public static final boolean transferCall() {
      try {
         _phone.transferCall();
         return true;
      } finally {
         ;
      }
   }

   public static final boolean exitEmergencyCallbackMode() {
      try {
         _phone.endEmergencyCallbackMode();
         return true;
      } finally {
         ;
      }
   }

   public static final boolean removeCallFromConference(int callId) {
      try {
         _phone.removeCallFromConference(callId);
         return true;
      } finally {
         ;
      }
   }

   public static final boolean startDTMF(int callId, byte dtmfTone) {
      try {
         _phone.startDTMF(callId, dtmfTone);
         return true;
      } finally {
         ;
      }
   }

   public static final boolean stopDTMF(int callId) {
      try {
         _phone.stopDTMF(callId);
         return true;
      } finally {
         ;
      }
   }

   public static final void activateCallBarring(int type, String password) {
      try {
         _phone.activateCallBarring(true, type, password);
      } finally {
         return;
      }
   }

   public static final void deactivateCallBarring(int type, String password) {
      try {
         _phone.activateCallBarring(false, type, password);
      } finally {
         return;
      }
   }

   public static final void setCallBarringPassword(String oldPassword, String newPassword) {
      try {
         _phone.setCallBarringPassword(oldPassword, newPassword);
      } finally {
         return;
      }
   }

   public static final String getCallForwardingNumber(int forwardingType, int bearerService) {
      try {
         return RadioInfo.getNetworkType() == 3
            ? _phone.getForwardingNumberForService(forwardingType, bearerService)
            : _phone.getCallForwardingNumber(forwardingType);
      } finally {
         ;
      }
   }

   public static final void setCallForwardingNumber(int forwardingType, String number) {
      try {
         _phone.setCallForwardingNumber(forwardingType, number);
      } finally {
         return;
      }
   }

   public static final void deactivateCallForwarding() {
      try {
         _phone.deactivateCallForwarding();
      } finally {
         return;
      }
   }

   public static final boolean isCallRedirected(int callId) {
      try {
         return Phone.getInstance().isCallRedirected(callId);
      } finally {
         ;
      }
   }

   public static final boolean getOutgoingCallPermissionBits(byte state, boolean cdma) {
      return cdma ? _outgoingCallPermissionBitsCDMA[state] != 0 : _outgoingCallPermissionBitsGSM[state] != 0;
   }

   private final void reset(Object context) {
      _instance = this;
      this._voiceApp = null;
      this._phoneEventListeners = null;
      this._phoneNumberFilter = new VoiceServices$1(this);
   }

   public static final int startCall(String param0, int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: getstatic net/rim/device/apps/api/phone/VoiceServices.VERIFY_BEFORE_STARTING_CALL_ID I
      // 03: invokestatic net/rim/vm/PersistentInteger.get (I)I
      // 06: ifeq 3a
      // 09: new java/lang/Object
      // 0c: dup
      // 0d: ldc_w "Call \""
      // 10: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 13: aload 0
      // 14: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 17: ldc_w "\" (flags="
      // 1a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1d: iload 1
      // 1e: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 21: ldc_w ")?"
      // 24: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 27: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2a: astore 2
      // 2b: bipush 3
      // 2d: aload 2
      // 2e: bipush -1
      // 30: invokestatic net/rim/device/api/ui/component/Dialog.ask (ILjava/lang/String;I)I
      // 33: bipush -1
      // 35: if_icmpne 3a
      // 38: bipush 0
      // 39: ireturn
      // 3a: bipush 0
      // 3b: istore 2
      // 3c: getstatic net/rim/device/apps/api/phone/VoiceServices._phone Lnet/rim/device/api/system/Phone;
      // 3f: aload 0
      // 40: iload 1
      // 41: invokevirtual net/rim/device/api/system/Phone.startCall (Ljava/lang/String;I)I
      // 44: istore 2
      // 45: ldc2_w -3502867315182341540
      // 48: invokestatic net/rim/device/api/system/RIMGlobalMessagePoster.postGlobalEvent (J)Z
      // 4b: pop
      // 4c: goto 54
      // 4f: astore 3
      // 50: goto 54
      // 53: astore 3
      // 54: iload 2
      // 55: ifne 60
      // 58: ldc_w 160000
      // 5b: bipush 0
      // 5c: aload 0
      // 5d: invokestatic net/rim/device/apps/api/phone/VoiceServices.postEvent (IILjava/lang/Object;)V
      // 60: iload 2
      // 61: ireturn
      // try (27 -> 35): 36 null
      // try (27 -> 35): 38 null
   }

   public static final void initializeOnSystemStart(Object context) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Object oldInstance = ar.get(5533786620429140277L);
      VoiceServices instance = new VoiceServices(context);
      if (!(oldInstance instanceof VoiceServices)) {
         ar.put(5533786620429140277L, instance);
      } else {
         VoiceServices previousInstance = (VoiceServices)oldInstance;
         Object[] listeners = previousInstance._phoneEventListeners;
         if (listeners != null) {
            for (Object listener : listeners) {
               if (listener instanceof ExternalPhoneEventListener) {
                  instance.addListener((ExternalPhoneEventListener)listener);
               }
            }
         }

         previousInstance._voiceApp.onVoiceAppExit();
         previousInstance._voiceApp = null;
         instance._phoneNumberFilter = previousInstance._phoneNumberFilter;
         ar.replace(5533786620429140277L, instance);
      }
   }

   public static final void setVoiceApplication(VoiceApplication voiceApp) {
      getInstance().doSetVoiceApplication(voiceApp);
   }

   public static final void addPhoneNumberFilter(PhoneNumberFilter filter) {
      getInstance().internalAddPhoneNumberFilter(filter);
   }

   private final synchronized void internalAddPhoneNumberFilter(PhoneNumberFilter filter) {
      filter.setNextFilter(this._phoneNumberFilter);
      this._phoneNumberFilter = filter;
   }

   public static final void removePhoneNumberFilter(PhoneNumberFilter filter) {
      getInstance().internalRemovePhoneNumberFilter(filter);
   }

   private final synchronized void internalRemovePhoneNumberFilter(PhoneNumberFilter filter) {
      if (this._phoneNumberFilter == filter) {
         this._phoneNumberFilter = filter.getNextFilter();
         filter.setNextFilter(null);
      } else {
         for (PhoneNumberFilter prevFilter = this._phoneNumberFilter; prevFilter != null; prevFilter = prevFilter.getNextFilter()) {
            if (prevFilter.getNextFilter() == filter) {
               prevFilter.setNextFilter(filter.getNextFilter());
               filter.setNextFilter(null);
               return;
            }
         }
      }
   }

   public static final synchronized void addPhoneEventListener(PhoneEventListener listener) {
      getInstance().addListener(listener);
   }

   public static final synchronized void removePhoneEventListener(PhoneEventListener listener) {
      getInstance().removeListener(listener);
   }

   public static final synchronized void addCallTimerListener(CallTimerListener listener) {
      getInstance().internalAddCallTimerListener(listener);
   }

   public static final synchronized void removeCallTimerListener(CallTimerListener listener) {
      getInstance().internalRemoveCallTimerListener(listener);
   }

   public static final void addRadioStatusListener(RadioStatusListener listener) {
      getUiApplication().addRadioListener(listener);
   }

   public static final synchronized void removeRadioStatusListener(RadioStatusListener listener) {
      getUiApplication().removeRadioListener(listener);
   }

   public static final void postEvent(int eventId, int param, Object context) {
      getInstance().internalPostEvent(eventId, param, context);
   }

   private VoiceServices(Object context) {
      this.reset(context);
   }

   public static final void broadcastEvent(int eventId, int param, Object context) {
      getInstance().dispatchPhoneEvent(eventId, param, context);
   }

   public static final void broadcastEvent(int eventId) {
      getInstance().dispatchPhoneEvent(eventId, 0, null);
   }

   public static final VoiceServices getInstance() {
      if (_instance == null || _instance.hasVoiceAppDied()) {
         _instance = (VoiceServices)ApplicationRegistry.getApplicationRegistry().waitFor(5533786620429140277L);
      }

      return _instance;
   }

   public static final int getCurrentLineId() {
      return getCurrentLineId(-1);
   }

   private final VoiceApplication getVoiceApp() {
      if (this._voiceApp == null) {
         throw new Object("No voice application instance.");
      } else {
         return this._voiceApp;
      }
   }

   public static final UiApplication getUiApplication() {
      return (UiApplication)getInstance().getVoiceApp();
   }

   public static final VoiceApplication getVoiceApplication() {
      return getInstance().getVoiceApp();
   }

   public static final byte getPhoneState() {
      _phoneState.calculateState();
      return _phoneState.getState();
   }

   public static final boolean isPhoneActive() {
      return getPhoneState() != 0;
   }

   private final boolean hasVoiceAppDied() {
      if (this._voiceApp != null) {
         int pid = ((Application)_instance._voiceApp).getProcessId();
         Process process = Process.getProcess(pid);
         return process == null || !process.isAlive();
      } else {
         return true;
      }
   }

   public static final int getCurrentLineId(int callId) {
      try {
         return Phone.getInstance().getAlternateLine(callId);
      } finally {
         ;
      }
   }

   public static final int getVoiceNetworkCapabilities() {
      return _phone.getNetworkFeatures();
   }

   public static final int filterStartCall(String phoneNumber, int flags) {
      return getInstance()._phoneNumberFilter.startCall(phoneNumber, flags);
   }

   public static final boolean stopCall(int callId) {
      new VoiceServices$1StopCallRunnable(callId, 8000).run();
      return true;
   }

   public static final boolean answerCall(int callId) {
      try {
         _phone.answerCall(callId);
      } finally {
         ;
      }

      RIMGlobalMessagePoster.postGlobalEvent(-5577605925563127340L);
      return true;
   }

   public static final boolean stopCurrentCall(Object context) {
      return getVoiceApplication().stopCurrentCall(context);
   }

   public static final boolean stopConferenceCall() {
      return getVoiceApplication().stopConferenceCall();
   }

   public static final boolean stopAllCalls(Object context) {
      return getVoiceApplication().stopAllCalls(context);
   }

   public static final String getCallPhoneNumber(int callId) {
      return getCallPhoneNumber(callId, false);
   }

   public static final String getCallPhoneNumber(int callId, boolean original) {
      String number = null;

      try {
         return _phone.getCallPhoneNumber(callId, original);
      } finally {
         ;
      }
   }

   private final Integer talkStatusParameter(int talkStatus) {
      switch (talkStatus) {
         case 0:
            return (Integer)(new Object(talkStatus));
         case 1:
         default:
            return this._talkStatusCanTalk;
         case 2:
            return this._talkStatusCannotTalk;
         case 3:
            return this._talkStatusPushToTalk;
      }
   }

   private final Integer callTypeParameter(int callType) {
      switch (callType) {
         case 0:
            return (Integer)(new Object(callType));
         case 1:
         default:
            return this._callTypePrivate;
         case 2:
            return this._callTypeAlert;
         case 3:
            return this._callTypeGroup;
      }
   }
}
