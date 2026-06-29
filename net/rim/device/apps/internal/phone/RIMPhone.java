package net.rim.device.apps.internal.phone;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.notification.NotificationsEngineListener;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.LED;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceApplication;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ribbon.RibbonKeyListener;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ui.ClickAndHoldKey;
import net.rim.device.apps.internal.phone.api.InHolsterEventListener;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;
import net.rim.device.apps.internal.phone.api.PhoneLogger;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.api.livecall.LiveCallFactoryRegistry;
import net.rim.device.apps.internal.phone.api.ui.CallDisplayListener;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.data.PhoneCallModelImpl;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.phone.resource.PhoneContexts;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.apps.internal.profiles.Profile;
import net.rim.device.apps.internal.profiles.Profiles;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.LEDEngine;
import net.rim.device.internal.ui.UiSettings;
import net.rim.tid.im.layout.DefaultKeyLayout;
import net.rim.tid.im.layout.SLKeyLayout;

public final class RIMPhone
   implements PhoneEventListener,
   RibbonKeyListener,
   Runnable,
   InHolsterEventListener,
   CallDisplayListener,
   GlobalEventListener,
   NotificationsEngineListener {
   private UiApplication _app;
   private boolean _pendingForegroundRequest;
   private boolean _rejectCallWhenCallerIDInfoArrives;
   private RIMPhone$HookswitchHandler _hookswitchHandler;
   private RIMPhone$PhoneUnhandledGlobalKeyListenerImpl _unhandledKeyListener;
   private CallDisplayDialog _incomingCallScreen;
   private ContextObject _incomingCallContext;
   private LiveCall _incomingCall;
   private boolean _waiting;
   private int _notificationTimerId = -1;
   private VerbFactory[] _incomingCallVerbFactories;
   private VerbFactory[] _inHolsterEventVerbFactories;
   private long _lastOutOfHolsterTime;
   private int _networkType = RadioInfo.getNetworkType();
   private ClickAndHoldKey _clickAndHoldKey;
   private char _previousRepeatedKey = 0;
   private int _previousRepeatTime = 0;
   long _cachedId = -1;
   public static final long GUID;
   private static final long DTMF_ECHO_TIMEOUT;
   private static final int INCOMING_CALL_NOTIFICATION_PAUSE_LENGTH;
   private static final int WAITING_CALL_NOTIFICATION_PAUSE_LENGTH;
   private static final int INVALID_NOTIFICATION_TIMER_ID;
   private static final int MAX_INCOMING_CALL_REPEATS;
   private static final int HOLSTER_IN_OUT_THRESHOLD;
   private static final int CALL_WAITING_TUNE_VOLUME;
   private static final int DEBUG_INCOMING_CALL_ID_INVALID;
   private static final int DEBUG_HOOKSWITCH_DOWN;
   private static final int DEBUG_HOOKSWITCH_UP;
   private static RIMPhone _instance;

   public final UiApplication getApplication() {
      return this._app;
   }

   final void clearIncomingCall() {
      this._incomingCall = null;
   }

   final void requestVoiceApplicationForeground() {
      this.requestVoiceApplicationForeground(null);
   }

   final void requestVoiceApplicationForeground(Object context) {
      if (this._app.isForeground()) {
         this._pendingForegroundRequest = false;
      } else {
         this._pendingForegroundRequest = true;
         byte phoneState = VoiceServices.getPhoneState();
         boolean isPhoneActive = phoneState != 0 && phoneState != 11;
         boolean backgroundOnCompletion = isPhoneActive;
         ContextObject contextObj = ContextObject.castOrCreate(context);
         new Object();
         if (backgroundOnCompletion) {
            contextObj.setFlag(96);
         }

         ((VoiceApplication)this._app).requestForeground(null, contextObj);
      }

      Backlight.enable(true);
   }

   final LiveCall getIncomingCall() {
      return this._incomingCall;
   }

   final RIMModel getUpdatedCallerIDInfo(int callId) {
      return this.getIncomingCallerIDInfo();
   }

   @Override
   public final void proceedWithDeferredEvent(long sourceIdLong, long eventIdLong, Object eventReferenceObject, Object contextObject) {
      if (!PhoneUtilities.getPrivateFlag(this._incomingCallContext, 61)) {
         if (!DeviceInfo.isInHolster()) {
            Backlight.enable(true);
         }

         boolean waiting = PhoneUtilities.getPrivateFlag(this._incomingCallContext, 23);
         this.showCallDisplay(this._incomingCallContext, waiting);
         Runnable startNotificationRunnable = new RIMPhone$2(this);
         this._app.invokeLater(startNotificationRunnable, 750, false);
      }
   }

   @Override
   public final void deferredEventWasSuperseded(long sourceIdLong, long eventIdLong, Object eventReferenceObject, Object contextObject) {
      NotificationsManager.cancelDeferredEvent(sourceIdLong, eventIdLong, eventReferenceObject, 1, contextObject);
   }

   @Override
   public final void notificationsEngineStateChanged(int stateInt, long sourceIdLong, long eventIdLong, Object eventReferenceObject, Object contextObject) {
      if (stateInt == 1 && this._incomingCallContext != null && !PhoneUtilities.isBacklitScreen()) {
         Backlight.enable(true);
      }
   }

   @Override
   public final void callDisplayStopRepeatNotification() {
      this.stopRepeatingNotification();
   }

   @Override
   public final void callDisplayClosed() {
      this.stopRepeatingNotification();
      NotificationsManager.cancelAllDeferredEvents(this._cachedId, 1, PhoneContexts.NOTIFICATIONS_CONTEXT_WR.getContextObject());
      this._incomingCallScreen = null;
      if (!PhoneUtilities.isBacklitScreen()) {
         Backlight.enable(false);
      }
   }

   @Override
   public final void phoneEventNotify(int eventId, int param1, Object context) {
      if (!ITPolicy.getBoolean(1, true) && eventId != 2015 && (RadioInfo.getNetworkService() & 256) == 0) {
         this._pendingForegroundRequest = false;
         if (eventId == 1000 && PhoneUtilities.canReject()) {
            try {
               Phone.getInstance().rejectCall(param1);
            } finally {
               return;
            }
         }
      } else {
         switch (eventId) {
            case 1000:
               this.handleCallIncoming(param1, false);
               return;
            case 1001:
               this.handleCallConnected(param1);
               return;
            case 1002:
               this.handleCallDisconnected(param1);
               return;
            case 1005:
               this.handleCallIncoming(param1, true);
               return;
            case 1006:
               this.handleCallFailed(param1, context);
               return;
            case 1110:
               this.clearCallDisplay();
               return;
            case 2001:
               this.handleCallRejected(param1);
               return;
            case 2020:
               this.handleCallIgnored(param1);
               return;
            case 2100:
               this._pendingForegroundRequest = false;
               return;
            case 2200:
               this.handleCallerIDUpdated(param1);
               return;
            case 3006:
               this.clearCallDisplay();
               this.clearIncomingCall();
               this.clearIncomingCallContext();
               return;
            case 100300:
               long time = System.currentTimeMillis();
               this.deviceInHolster(time - this._lastOutOfHolsterTime);
               return;
            case 100301:
               this._lastOutOfHolsterTime = System.currentTimeMillis();
               this.deviceOutOfHolster();
               return;
            case 100400:
               this._hookswitchHandler.headsetButtonClick(0, param1);
               return;
            case 100401:
               this._hookswitchHandler.headsetButtonUnclick(0, param1);
               return;
            case 150090:
               registerAllLines();
               return;
            case 180000:
               this.clearCallDisplay();
               this.clearIncomingCall();
               this.clearIncomingCallContext();
         }
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      this._previousRepeatedKey = 0;
      if (key == ' ' && !Keypad.hasSendEndKeys()) {
         this.showVoiceAppForPhoneNumberInput(key);
         Backlight.enable(true);
         return true;
      }

      if (!PhoneUtilities.isQwertyReducedKeyboard() && !PhoneOptions.getOptions().getBooleanOption(16384)) {
         return false;
      }

      if (status != 0 && (status & 32768) == 0) {
         return false;
      }

      if (CallManager.getInstance().getNumCalls() > 0) {
         return false;
      }

      if (!PhoneNumberInput.validateCharacter(key) && (status != 0 || !PhoneNumberInput.validateCharacter(Keypad.getAltedChar(key)))) {
         return false;
      }

      this.showVoiceAppForPhoneNumberInput(key);
      return true;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      this._previousRepeatedKey = 0;
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      char key = Keypad.map(keycode);
      int modifiers = SLKeyLayout.convertStatusToModifiers(Keypad.status(keycode));
      int originalCode = Keypad.getLayout().getOriginalKeyCode(key, modifiers);
      StringBuffer keyChars = DefaultKeyLayout.getDefaultKeyLayout().getKeyChars(originalCode, modifiers);
      key = keyChars.charAt(0);
      if (key == 'm' && PhoneUtilities.getDebugFlag(-3318255709650210549L, false)) {
         ShowMessageApp.showMessageApp();
         return true;
      }

      if (!InternalServices.isReducedFormFactor() || key != '0' && Keypad.getAltedChar(key) != '0') {
         if (key != this._previousRepeatedKey || time - this._previousRepeatTime > 1000) {
            Runnable runner = new RIMPhone$1SpeedDialRunnable(this, keycode);
            Application.getApplication().invokeLater(runner);
         }

         this._previousRepeatTime = time;
         this._previousRepeatedKey = key;
         return true;
      } else {
         this.showVoiceAppForPhoneNumberInput('+');
         return true;
      }
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      this._previousRepeatedKey = 0;
      return false;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -5324686711008477091L) {
         if (object0 instanceof Object) {
            Application voiceApp = (Application)VoiceServices.getVoiceApplication();
            voiceApp.invokeLater(new RIMPhone$StartCallRunnable((String)object0, data0));
         }
      }
   }

   @Override
   public final void run() {
      if (this._waiting) {
         PhoneUtilities.playInCallTune(2, 50);
      } else {
         if (this._notificationTimerId != -1) {
            ContextObject context = (ContextObject)(new Object());
            if (this._incomingCallContext != null) {
               Object obj = this._incomingCallContext.get(5898398779440734986L);
               if (obj instanceof Object) {
                  CallerIDInfo callerIDInfo = (CallerIDInfo)obj;
                  int addresscardUID = (int)callerIDInfo.getUid();
                  ContextObject.put(context, -7004855975111283545L, new Object(addresscardUID));
               }
            }

            NotificationsManager.triggerImmediateEvent(this.getPhoneLineSource(), 0, null, context);
         }
      }
   }

   @Override
   public final void inHolsterEvent(int eventId, int param) {
      if (eventId == 0 && VoiceServices.isPhoneActive()) {
         AudioRouter.getInstance().incrementMasterVolume(-10 * param);
      } else if (eventId == 6) {
         this._unhandledKeyListener.applicationKeyDown(param);
      } else if (eventId == 7) {
         this._unhandledKeyListener.applicationKeyUp(param);
      } else if (eventId != 8 && eventId != 9) {
         this.handleInHolsterEvent(eventId);
      } else {
         this.stopRepeatingNotification();
      }
   }

   private final void clearCallDisplay() {
      if (this._incomingCallScreen != null) {
         this._incomingCallScreen.dismiss();
         this._incomingCallScreen = null;
      }
   }

   private final VerbFactory getIncomingCallVerbFactory() {
      if (this._incomingCallVerbFactories == null || this._incomingCallVerbFactories.length == 0) {
         this._incomingCallVerbFactories = VerbFactoryRepository.getVerbFactories(-689504907596508088L);
         if (this._incomingCallVerbFactories == null || this._incomingCallVerbFactories.length == 0) {
            return null;
         }
      }

      return this._incomingCallVerbFactories[0];
   }

   private final LiveCall getIncomingCall(int callId) {
      return this._incomingCall != null && this._incomingCall.getCallId() == callId ? this._incomingCall : null;
   }

   public static final void registerAllLines() {
      NotificationsManager.registerSource(2868625504212929964L, PhoneResources.getString(118), 0);
      Profiles.getInstance().hideSource(2868625504212929964L);
      long[] relatedIds = NotificationsManager.getRelatedSourceIds(2868625504212929964L, false);
      if (relatedIds != null) {
         for (int idx = 0; idx < relatedIds.length; idx++) {
            NotificationsManager.deregisterSource(relatedIds[idx]);
         }
      }

      int[] lineIds = PhoneUtilities.getAllLineIds();
      String[] profileDescriptions = PhoneUtilities.getAllLineNumbers();
      if (lineIds.length == 1) {
         RIMPhone$PhoneSource source = new RIMPhone$PhoneSource();
         NotificationsManager.registerSource(lineIds[0] + 2868625504212929964L, source, 0, 2868625504212929964L);
      } else {
         for (int i = lineIds.length - 1; i >= 0; i--) {
            if (profileDescriptions[i] == null || profileDescriptions[i] == "") {
               profileDescriptions[i] = PhoneResources.getString(117);
            }

            RIMPhone$PhoneSource source = new RIMPhone$PhoneSource();
            source.setLineNumber(((StringBuffer)(new Object("["))).append(profileDescriptions[i]).append("]").toString());
            NotificationsManager.registerSource(lineIds[i] + 2868625504212929964L, source, 0, 2868625504212929964L);
         }
      }
   }

   private final void handleCallerIDUpdated(int callId) {
      System.out.println(((StringBuffer)(new Object("EV_CALLER_ID_UPDATED ("))).append(callId).append(")").toString());
      CallerIDInfo callerIDInfo = PhoneUtilities.getCallDisplayInfo(callId, 22, this._incomingCallContext);
      if (callerIDInfo != null) {
         if (this._rejectCallWhenCallerIDInfoArrives) {
            this._rejectCallWhenCallerIDInfoArrives = false;
            VoiceServices.rejectCall(callId);
         }

         if (this._incomingCallScreen != null) {
            this._incomingCallScreen.updateCallerIDInfo(callerIDInfo);
         }

         if (this._incomingCallContext != null) {
            System.out.println(((StringBuffer)(new Object("callid number="))).append(this._incomingCallContext.get(-6346895525857192403L)).toString());
            this._incomingCallContext.put(5898398779440734986L, callerIDInfo);
         }

         LiveCall incomingCall = this.getIncomingCall(callId);
         if (incomingCall != null) {
            incomingCall.setCallerIDInfo(callerIDInfo);
            return;
         }
      } else {
         System.out.println("--no callid info--");
      }
   }

   private final void handleCallIncoming(int callId, boolean waiting) {
      if (this._networkType == 3 || this._networkType == 7) {
         this._rejectCallWhenCallerIDInfoArrives = false;
      }

      if (!PhoneUtilities.gsmTypeNetwork() || this._incomingCallContext == null || this.getIncomingCallId() != callId) {
         if (PhoneUtilities.getDebugFlag(-2489431779144400366L)) {
            Verb answerVerb = (Verb)(new Object(callId, null, 0, 0, null));
            this._app.invokeLater(new VerbRunner(answerVerb), 7000, false);
         }

         this._incomingCallContext = (ContextObject)(new Object());
         int callTypeFlag = waiting ? 23 : 22;
         PhoneUtilities.setPrivateFlag(this._incomingCallContext, callTypeFlag);
         ContextObject.setFlag(this._incomingCallContext, 117);
         CallerIDInfo callerIDInfo = PhoneUtilities.getCallDisplayInfo(callId, callTypeFlag, this._incomingCallContext);
         if (callerIDInfo != null) {
            this._incomingCallContext.put(5898398779440734986L, callerIDInfo);
         }

         this._incomingCallContext.put(2321140177253895719L, new Object(callId));
         if (CallManager.getInstance().getConferenceCall() != null) {
            this._incomingCallContext.setFlag(80);
         }

         PhoneCallInitialData data = (PhoneCallInitialData)(new Object(callId, (byte)0, 8, callerIDInfo, this._incomingCallContext));
         this._incomingCall = LiveCallFactoryRegistry.getRegistry().createLiveCall(data, this._incomingCallContext);
         if (PhoneUtilities.getPrivateFlag(this._incomingCallContext, 85)) {
            Integer autoAnswerDelay = (Integer)this._incomingCallContext.get(1390781651728252971L);
            if (autoAnswerDelay instanceof Object) {
               Verb autoAnswer = (Verb)(new Object(callId, null, 0, 0, null));
               this._app.invokeLater(new VerbRunner(autoAnswer), autoAnswerDelay.intValue(), false);
               return;
            }
         }

         if (PhoneUtilities.getPrivateFlag(this._incomingCallContext, 86)) {
            Profile profile = Profiles.getInstance().getEnabled();
            byte profileId = profile.getIdentifier();
            if (profile.getIdentifier() != 2) {
               this._app.invokeLater(new RIMPhone$1(this), 100, false);
            }

            Backlight.enable(true);
            this.showCallDisplay(this._incomingCallContext, waiting);
         } else {
            ContextObject negotiationContext = PhoneContexts.NOTIFICATIONS_CONTEXT_WR.getContextObject();
            if (negotiationContext.get(4086083307293257364L) != null) {
               negotiationContext.remove(4086083307293257364L);
            }

            NotificationsManager.negotiateDeferredEvent(this.getPhoneLineSource(), 0, this._incomingCallContext, 0, 1, negotiationContext);
            Object result = negotiationContext.get(4086083307293257364L);
            if (result instanceof Object) {
               Boolean success = (Boolean)result;
               if (!success) {
                  System.out.println(((StringBuffer)(new Object("PHONE: DND callId "))).append(callId).toString());
                  if (PhoneUtilities.canReject()) {
                     if (this._networkType != 3 && this._networkType != 7) {
                        VoiceServices.rejectCall(callId);
                     } else {
                        this._rejectCallWhenCallerIDInfoArrives = true;
                     }
                  }
               }
            }
         }

         logIncomingCallInfo(callId, waiting, this._incomingCallContext);
      }
   }

   private static final void logIncomingCallInfo(int callId, boolean waiting, ContextObject context) {
      StringBuffer logString = (StringBuffer)(new Object());
      logString.append(waiting ? "cwtg" : "cinc");
      logString.append(callId);
      if (!PersistentContent.isEncryptionEnabled()) {
         String origCallPhoneNumber = (String)context.get(7641283818372650975L);
         if (origCallPhoneNumber != null) {
            logString.append(":orig:");
            logString.append(origCallPhoneNumber);
         }

         String displayPhoneNumber = (String)context.get(-6346895525857192403L);
         if (displayPhoneNumber != null) {
            logString.append(":disp:");
            logString.append(displayPhoneNumber);
         }
      }

      PhoneLogger.log(logString);
   }

   private final void showVoiceAppForPhoneNumberInput(char key) {
      ContextObject context = (ContextObject)(new Object());
      context.put(-2621706732407506661L, new Object(key));
      ((VoiceApp)this._app).requestForeground(null, context);
      Backlight.enable(true);
   }

   static final RIMPhone initialize(VoiceApp voiceApp) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = new RIMPhone(voiceApp);
      ar.replace(-7141493357986385054L, _instance);
      return _instance;
   }

   public RIMPhone(UiApplication app) {
      this._app = app;
      this._hookswitchHandler = new RIMPhone$HookswitchHandler(this, this._app);
      Audio.addListener(app, this._hookswitchHandler);
      this._unhandledKeyListener = new RIMPhone$PhoneUnhandledGlobalKeyListenerImpl(this);
      app.addKeyListener(this._unhandledKeyListener);
      RibbonLauncher launcher = RibbonLauncher.getInstance();
      if (launcher != null) {
         launcher.addRibbonListener(this);
      }

      app.addGlobalEventListener(this);
      NotificationsManager.deregisterNotificationsEngineListener(2868625504212929964L, this);
      NotificationsManager.registerNotificationsEngineListener(2868625504212929964L, this);
   }

   private final boolean isForegroundPending() {
      return this._pendingForegroundRequest;
   }

   private final void clearIncomingCallContext() {
      this._incomingCallContext = null;
      this._rejectCallWhenCallerIDInfoArrives = false;
   }

   static final RIMPhone getInstance() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (RIMPhone)ar.getOrWaitFor(-7141493357986385054L);
      }

      return _instance;
   }

   private final void handleCallConnected(int callId) {
      if (this._incomingCallContext != null && this.getIncomingCallId() == callId) {
         this._incomingCallContext.put(-1189301621374034083L, new Object(callId));
         this.clearIncomingCall();
         this.clearCallDisplay();
         this.clearIncomingCallContext();
         this.stopRepeatingNotification();
         if (!PhoneUtilities.isBacklitScreen()) {
            Backlight.enable(false);
         }
      }
   }

   private final void handleCallDisconnected(int callId) {
      if (this._incomingCallContext != null && this.getIncomingCallId() == callId && this.getConnectedCallId() != callId) {
         this.recordIncomingCallDisconnection(callId);
         if (this._incomingCallScreen != null) {
            this.clearCallDisplay();
         }

         this.handleMissedCall(callId, false);
         this.clearIncomingCall();
         this.stopRepeatingNotification();
      } else {
         if (this.getIncomingCall(callId) != null) {
            this.clearIncomingCall();
         }
      }
   }

   private final void recordIncomingCallDisconnection(int callId) {
      PhoneLogger.log(((StringBuffer)(new Object("inc-call-disc "))).append(callId).toString());
      PhoneUtilities.setPrivateFlag(this._incomingCallContext, 61);
   }

   private final void handleCallFailed(int callId, Object reason) {
      if (this._incomingCallContext != null && this.getIncomingCallId() == callId) {
         this.recordIncomingCallDisconnection(callId);
         if (reason instanceof Object) {
            int error = reason;
            String msg = PhoneUtilities.getCallFailureErrorString(error);
            PhoneOptions.getOptions().logPhoneError("PH", error, msg);
         }

         this.handleCallRejected(callId);
         this.clearIncomingCall();
      } else {
         if (this.getIncomingCall(callId) != null) {
            this.clearIncomingCall();
         }
      }
   }

   private final void handleCallRejected(int callId) {
      this.clearCallDisplay();
      this.handleMissedCall(callId, true);
   }

   private final long getPhoneLineSource() {
      this._cachedId = PhoneUtilities.getCurrentLineId(this.getIncomingCallId()) + 2868625504212929964L;
      return this._cachedId;
   }

   private final void startRepeatingNotification(boolean waiting) {
      this._waiting = waiting;
      int delay = -1;
      short var9;
      if (waiting) {
         PhoneUtilities.playInCallTune(2, 50);
         var9 = 7500;
      } else {
         var9 = 3000;
         ContextObject context = (ContextObject)(new Object());
         ContextObject.put(context, -2000078441617626078L, new Object(10));
         ContextObject.put(context, 3423823800652933171L, new Object(var9));
         ContextObject.put(context, -8706641515457485416L, new Object(2));
         if (this._incomingCallContext != null) {
            Object obj = this._incomingCallContext.get(5898398779440734986L);
            if (obj instanceof Object) {
               CallerIDInfo callerIDInfo = (CallerIDInfo)obj;
               int addresscardUID = (int)callerIDInfo.getUid();
               ContextObject.put(context, -7004855975111283545L, new Object(addresscardUID));
            }
         }

         label50:
         try {
            int lightStyle = PhoneOptions.getOptions().getRingtoneLightStyle();
            if ((lightStyle & 1) != 0) {
               LED.setState(1, 4);
            }

            if ((lightStyle & 2) != 0) {
               LED.setState(0, 4);
            }
         } finally {
            break label50;
         }

         NotificationsManager.triggerImmediateEvent(this.getPhoneLineSource(), 0, null, context);
      }

      if (!PhoneUtilities.cdmaTypeNetwork() && waiting) {
         this._notificationTimerId = this._app.invokeLater(this, var9, true);
      }
   }

   private final void stopRepeatingNotification() {
      ContextObject contextObject = (ContextObject)(new Object());
      contextObject.setFlag(39);
      NotificationsManager.cancelImmediateEvent(this._cachedId, 0, null, contextObject);

      label32:
      try {
         int lightStyle = PhoneOptions.getOptions().getRingtoneLightStyle();
         if (lightStyle != 0) {
            LED.setState(1, 0);
         }

         LEDEngine.getInstance().updateGSMFlag(UiSettings.getLEDCoverageIndicatorStatus());
      } finally {
         break label32;
      }

      if (this._notificationTimerId != -1) {
         this._app.cancelInvokeLater(this._notificationTimerId);
         this._notificationTimerId = -1;
      }
   }

   private final VerbFactory getInHolsterEventVerbFactory() {
      if (this._inHolsterEventVerbFactories == null || this._inHolsterEventVerbFactories.length == 0) {
         this._inHolsterEventVerbFactories = VerbFactoryRepository.getVerbFactories(6344899092283249610L);
         if (this._inHolsterEventVerbFactories == null || this._inHolsterEventVerbFactories.length == 0) {
            return null;
         }
      }

      return this._inHolsterEventVerbFactories[0];
   }

   private final void deviceInHolster(long elapsedTime) {
      int eventId = 5;
      if (elapsedTime < 1500) {
         eventId = 3;
      }

      this.handleInHolsterEvent(eventId);
   }

   private final void deviceOutOfHolster() {
      if (this._incomingCallContext != null && !PhoneUtilities.isBacklitScreen()) {
         Backlight.enable(true);
      }

      this.handleInHolsterEvent(4);
   }

   private final RIMModel getIncomingCallerIDInfo() {
      if (this._incomingCallContext != null) {
         Object o = this._incomingCallContext.get(5898398779440734986L);
         if (o instanceof Object) {
            return (RIMModel)o;
         }
      }

      return null;
   }

   private final int getIncomingCallId() {
      if (this._incomingCallContext != null) {
         Object o = this._incomingCallContext.get(2321140177253895719L);
         if (o instanceof Object) {
            return o;
         }
      }

      return 0;
   }

   private final int getConnectedCallId() {
      if (this._incomingCallContext != null) {
         Object o = this._incomingCallContext.get(-1189301621374034083L);
         if (o instanceof Object) {
            return o;
         }
      }

      return 0;
   }

   private final void handleCallIgnored(int callId) {
      if (this._incomingCallContext != null && this.getIncomingCallId() == callId) {
         PhoneUtilities.setPrivateFlag(this._incomingCallContext, 78);
      }
   }

   private final void handleMissedCall(int callId, boolean userRejected) {
      if (this._incomingCallContext != null) {
         LiveCall incomingCall = this.getIncomingCall(callId);
         if (incomingCall != null) {
            userRejected |= PhoneUtilities.getPrivateFlag(this._incomingCallContext, 78);
            PhoneCallModelImpl callLog = (PhoneCallModelImpl)incomingCall.getPhoneCall();
            if (callLog != null) {
               callLog.logAsMissed();
            }

            ((VoiceApp)this._app).onMissedCall(userRejected);
            this._incomingCallContext.put(5898398779440734986L, incomingCall.getCallerIDInfo());
            this._incomingCallContext.put(-6075010664073451177L, incomingCall);
            VoiceServices.broadcastEvent(2002, callId, this._incomingCallContext);
         }

         this.clearIncomingCall();
         this.clearIncomingCallContext();
         this.stopRepeatingNotification();
      }
   }

   private final void showCallDisplay(ContextObject incomingCallContext, boolean waiting) {
      VerbFactory factory = this.getIncomingCallVerbFactory();
      if (factory != null && incomingCallContext != null) {
         CallerIDInfo callerIDInfo = (CallerIDInfo)incomingCallContext.get(5898398779440734986L);
         Verb[] verbs = factory.getVerbs(incomingCallContext);
         if (verbs != null && verbs.length > 0) {
            int titleResourceId = waiting ? 161 : 160;
            int callId = this.getIncomingCallId();
            CallDisplayDialog callDisplayDialog = new CallDisplayDialog(
               callId, waiting, callerIDInfo, PhoneResources.getString(titleResourceId), verbs, this.getApplication(), this, incomingCallContext
            );
            if (waiting && PhoneUtilities.cdmaTypeNetwork()) {
               callDisplayDialog.setAutomaticTimeout(30000, new RIMPhone$3(this, callId));
            }

            if (PhoneUtilities.getPrivateFlag(incomingCallContext, 61)) {
               return;
            }

            this._incomingCallScreen = callDisplayDialog;
            CallerIDInfo updatedCallerIDInfo = (CallerIDInfo)incomingCallContext.get(5898398779440734986L);
            this._incomingCallScreen.updateCallerIDInfo(updatedCallerIDInfo);
            callDisplayDialog.show();
         }
      }
   }

   private final void handleInHolsterEvent(int eventId) {
      if (this._incomingCallContext != null && eventId == 3) {
         this.stopRepeatingNotification();
      }

      VerbFactory factory = this.getInHolsterEventVerbFactory();
      Verb[] verbs = null;
      Object context = null;
      if (factory != null) {
         if (this._incomingCallContext != null) {
            context = this._incomingCallContext;
         } else {
            context = PhoneContexts.GET_VERBS_CONTEXT_WR.getContextObject();
            ((ContextObject)context).reset();
         }

         ContextObject.put(context, -2949044237254437889L, new Object(eventId));
         PhoneUtilities.setPrivateFlag(context, 31);
         verbs = factory.getVerbs(context);
         if (verbs != null && verbs.length > 0 && verbs[0] != null) {
            if (eventId == 4) {
               this._app.invokeLater(new VerbRunner(verbs[0], context), 1000, false);
               return;
            }

            this._app.invokeLater(new IgnoreVerbRunner(verbs[0], context), 1000, false);
         }
      }
   }
}
