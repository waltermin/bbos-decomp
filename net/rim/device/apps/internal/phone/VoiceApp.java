package net.rim.device.apps.internal.phone;

import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceApplication;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.quickcontact.QuickContactItem;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ribbon.indicators.IndicatorManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.Confirmation;
import net.rim.device.apps.internal.phone.api.Out;
import net.rim.device.apps.internal.phone.api.PTTKeyHandler;
import net.rim.device.apps.internal.phone.api.PhoneLogger;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.api.ui.PhoneAwareScreen;
import net.rim.device.apps.internal.phone.api.ui.gprs.GSM230Filter;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;
import net.rim.device.apps.internal.phone.api.verbs.InitiateCallVerb;
import net.rim.device.apps.internal.phone.api.verbs.OutgoingCallConnector;
import net.rim.device.apps.internal.phone.api.verbs.VoiceMailVerb;
import net.rim.device.apps.internal.phone.data.CallLogCollectionCleaner;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.data.MissedCallIndicator;
import net.rim.device.apps.internal.phone.data.PhoneCallModelImpl;
import net.rim.device.apps.internal.phone.data.PhoneFolders;
import net.rim.device.apps.internal.phone.data.SpeedDialItem;
import net.rim.device.apps.internal.phone.data.VoiceUnopenedCount;
import net.rim.device.apps.internal.phone.options.CallForwardRibbonIndicator;
import net.rim.device.apps.internal.phone.options.CallTunes;
import net.rim.device.apps.internal.phone.options.HACOption;
import net.rim.device.apps.internal.phone.options.HACRibbonIndicator;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.phone.options.SSManager;
import net.rim.device.apps.internal.phone.options.TTYOption;
import net.rim.device.apps.internal.phone.options.TTYRibbonIndicator;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.device.internal.system.AudioInternal;
import net.rim.device.internal.system.VoiceDataUsage;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.component.PopupDialogClosedListener;
import net.rim.device.internal.ui.component.SimpleInputDialog;
import net.rim.vm.PersistentInteger;

public final class VoiceApp
   extends UiApplication
   implements VoiceApplication,
   PhoneEventListener,
   CallManager$Listener,
   PopupDialogClosedListener,
   GlobalEventListener,
   FieldProvider,
   PersistentContentListener {
   private boolean _foregroundToShowCallDisconnection;
   private Confirmation _switchBackgroundConfirmation;
   private boolean _switchBackgroundOnIdle;
   private boolean _switchedBackgroundOnIdle;
   private boolean _activatedBySENDKey;
   private boolean _activatedForAnsweredIncomingCall;
   private Object _appActivationContext;
   private Runnable _activationRunnable;
   private PhoneAppScreen _idleScreen;
   private ActivePhoneScreen _activeScreen;
   private NewCallScreen _newCallScreen;
   private long _callSessionTimer;
   private boolean _gotCallsEmpty;
   private boolean _hidingActiveScreen;
   private boolean _knownMissedCall;
   private boolean _active;
   private boolean _phoneEnabled;
   private GSM230Filter _gsm230Filter;
   private Runnable _switchBackgroundOnIdleRunnable = new VoiceApp$4(this);
   private Runnable _showActiveScreenRunnable = new VoiceApp$5(this);
   private Runnable _hideActiveScreenRunnable = new VoiceApp$6(this);
   private boolean _editingNotes;
   private boolean _foregroundedToEditNotes;
   private EditCallNotesScreen _editNotesScreen;
   private static final long EVENT_LOG_GUID = 7963442286867385855L;
   private static final long REDIAL_INFO_KEY = -5114641656481802126L;
   private static final long SIM_CARD_KEY = 4897752255641431829L;
   public static final int DEFAULT_AUDIO_VOLUME = 50;
   private static final int CS_FIRST_ALPHA_ID = 0;
   private static final int CS_SECOND_ALPHA_ID = 1;
   private static final Integer USSD_INPUT_DIALOG_COOKIE = (Integer)(new Object(0));
   private static Field[] RIBBON_COMPONENTS;
   private static final int[] COMPONENT_MAP = new int[]{4, 5, 6, 9, -804650997, 17, 18, 33, 41, 42, 43, 146, 147, 148, 153, 154};

   final void showHomeScreen() {
      RibbonLauncher rl = RibbonLauncher.getInstance();
      if (rl != null) {
         rl.showRootFolder();
      }
   }

   final void stopEditingCallNotes() {
      label36:
      try {
         Screen activeScreen = this.getActiveScreen();
         if (activeScreen instanceof EditCallNotesScreen) {
            this.popScreen(activeScreen);
         }
      } finally {
         break label36;
      }

      this._editingNotes = false;
      this._editNotesScreen = null;
      if (this._foregroundedToEditNotes) {
         this.requestBackground();
      }

      if (!this._active && ApplicationManager.getApplicationManager().isSystemLocked()) {
         this.invokeLater(this._switchBackgroundOnIdleRunnable);
      }
   }

   final boolean isPhoneEnabled() {
      return this._phoneEnabled;
   }

   final void preCallAnswer() {
      if (this.isForeground() && !this._activeScreen.isDisplayed()) {
         this._activeScreen.preCallAnswer();
      }
   }

   final void onMissedCall(boolean userRejected) {
      if (this._activeScreen.isDisplayed() && !this._hidingActiveScreen) {
         this._knownMissedCall = true;
      } else {
         MissedCallIndicator.getInstance().onMissedCall(userRejected);
      }
   }

   final void showNewCallScreen() {
      this._newCallScreen = new NewCallScreen(this);
      this.pushModalScreen(this._newCallScreen);
   }

   final void onNewCallScreenClosed(int closeReason) {
      this._newCallScreen = null;
      switch (closeReason) {
         case 1:
            if (this._gotCallsEmpty) {
               this.hideActiveScreenLater();
            }
      }
   }

   final void preCallTuneFinished(LiveCall call) {
      audioSourcePhoneOn();
      if (call != null) {
         VoiceServices.postEvent(100200, call.getCallId(), call);
         if (call.shouldAutoToggleSpeakerphone()) {
            call.toggleSpeakerphone();
         }
      }
   }

   @Override
   public final void onCallManagerEvent(int eventId, Vector currentCalls, LiveCall call, int flags, Object context) {
      switch (eventId) {
         case 10:
            AudioInternal.mute(false);
            if (this._callSessionTimer != 0) {
               VoiceDataUsage.addVoiceSeconds((int)(System.currentTimeMillis() - this._callSessionTimer) / 1000);
               this._callSessionTimer = 0;
            }

            PhoneLogger.log(((StringBuffer)(new Object("callsmpt; switchbg="))).append(this._switchBackgroundOnIdle).toString());
            this._gotCallsEmpty = true;
            this._active = false;
            this._activeScreen.callsEmpty();
            if (this.getActiveScreen() == this._newCallScreen) {
               return;
            }

            boolean deviceLocked = ApplicationManager.getApplicationManager().isSystemLocked();
            if ((flags & 4) != 0 && !deviceLocked) {
               if ((flags & 8) == 0) {
                  if (call != null && call.getFlag(2)) {
                     this.hideActiveScreenLater();
                     return;
                  }

                  this.hideActiveScreenNow();
                  return;
               }
               break;
            }

            if (!this._switchBackgroundOnIdle && !deviceLocked) {
               this.hideActiveScreenLater();
               return;
            }

            if (this._editingNotes) {
               this.popActiveScreen();
               return;
            }

            this.invokeLater(this._switchBackgroundOnIdleRunnable, 1500, false);
            return;
         case 20:
            if (call == null) {
               call = (LiveCall)ContextObject.get(context, -6075010664073451177L);
            }

            String preCallTuneName = (String)ContextObject.get(context, 2848872683723475070L);
            byte[] preCallTuneBytes = (byte[])ContextObject.get(context, -1582226542930718334L);
            if (preCallTuneBytes == null && PhoneUtilities.isEmptyString(preCallTuneName)) {
               audioSourcePhoneOn();
            } else {
               boolean forceSpeaker = PhoneUtilities.getPrivateFlag(context, 92);
               new PreCallTunePlayer(this, preCallTuneName, preCallTuneBytes, call, forceSpeaker).startTune();
            }

            PhoneLogger.log("calls !empty");
            this._callSessionTimer = System.currentTimeMillis();
            this._gotCallsEmpty = false;
            this._active = true;
            this._activeScreen.callsNonEmpty();
            if (!this._activatedForAnsweredIncomingCall) {
               this._activeScreen.updateCalls(currentCalls);
               LiveCall currentCall = (LiveCall)this.getCurrentCall();
               if (!this.isForeground()) {
                  this._switchBackgroundOnIdle = currentCall == null ? true : currentCall.shouldSwitchToBackgroundWhenIdle();
                  this.requestForeground(this._showActiveScreenRunnable, null);
                  return;
               }

               this.invokeLater(this._showActiveScreenRunnable);
               return;
            }
            break;
         case 30:
            Vector calls = currentCalls;
            if (this._activeScreen != null) {
               if (!this.isForeground()) {
                  this.requestForeground(new VoiceApp$2(this, calls), null);
                  return;
               }

               if ((flags & 16) != 0 || (flags & 32) != 0) {
                  this.invokeLater(new VoiceApp$3(this, calls), 1500, false);
                  return;
               }

               this._activeScreen.updateCalls(calls);
               if (this._editNotesScreen != null && this._editNotesScreen.isDisplayed()) {
                  this._editNotesScreen.updateCalls(calls);
               }
            }
      }
   }

   @Override
   public final void phoneEventNotify(int eventId, int param, Object context) {
      int callId = param;
      switch (eventId) {
         case 1000:
            this.handleCallIncoming(callId);
            return;
         case 1100:
            synchronized (this.getAppEventLock()) {
               this._activeScreen.onCallInitiated();
               this._showActiveScreenRunnable.run();
               if (PhoneUtilities.getPrivateFlag(context, 62)) {
                  String callSetupMsg = (String)ContextObject.get(context, -6776810758251164441L);
                  if (callSetupMsg != null) {
                     Status.show(callSetupMsg, 5000);
                  }
               }
            }

            Object callInitContext = context;
            this.invokeLater(new VoiceApp$7(this, callInitContext), 1000, false);
            return;
         case 1105:
            if (!this.isForeground()) {
               LiveCall currentCall = (LiveCall)this.getCurrentCall();
               this._switchBackgroundOnIdle = currentCall == null ? true : currentCall.shouldSwitchToBackgroundWhenIdle();
               this.requestForeground();
               return;
            }
            break;
         case 1110:
            audioSourcePhoneOn();
            if (!this.isForeground()) {
               this._activatedForAnsweredIncomingCall = true;
               this._switchBackgroundOnIdle = true;
               this.requestForeground();
            }

            this._activeScreen.onCallInitiated();
            return;
         case 2200:
            this.handleCallerIDUpdated(callId);
            return;
         case 3005:
            if (this._foregroundToShowCallDisconnection) {
               this.requestBackground();
               return;
            }
         case 100201:
            this.updateCFUIndicator(false);
            SSManager.getInstance().onSIMInvalid();
            return;
         case 5100:
            this.handleUssdDisplay(param, context);
            return;
         case 10100:
            if (context instanceof Object) {
               int newCallID = context;
               Out.p(((StringBuffer)(new Object("PHONE-CC("))).append(param).append(',').append(newCallID).append(')').toString());
               int resourceId = this.getCallControlEventStringId(param);
               if (resourceId != -1 && newCallID <= 0) {
                  String msg = CommonResources.getString(resourceId);
                  if (msg != null) {
                     Out.p(msg);
                     new CallControlInformationDialog(msg).doModal();
                     return;
                  }

                  Out.p("PHONE-CC no msg");
                  return;
               }
            }
            break;
         case 10200:
            this.handleCallSetupMessage(param, true, context);
            return;
         case 10201:
            this.handleCallSetupMessage(param, false, context);
            return;
         case 100200:
            SSManager.getInstance().onSIMValid();
            int id = PersistentInteger.getId(4897752255641431829L, 0);
            int currentSIMId = SIMCard.getIMSICRC();
            int oldSIMId = PersistentInteger.get(id);
            if (currentSIMId != oldSIMId) {
               PersistentInteger.set(id, currentSIMId);
               SSManager.updateCallForwardingUnconditionalActive(false);
               this.updateCFUIndicator(false);
               return;
            }
            break;
         case 100203:
            if (this._activeScreen != null) {
               this._activeScreen.updateNumber();
            }
            break;
         case 150110:
            this.updateCFUIndicator();
            SSManager.getInstance().onFeatureReady();
            return;
         case 150120:
            Out.p("PHONE: CFU status chg");
            this.updateCFUIndicator();
            return;
         case 160000:
            if (context instanceof Object) {
               String number = (String)context;
               String errorString = MessageFormat.format(PhoneResources.getString(146), new Object[]{number});
               Dialog.alert(errorString);
               return;
            }
            break;
         case 180000:
            TTYRibbonIndicator.getInstance().updateIndicator();
            if ((Phone.getInstance().getNetworkFeatures() & 524288) != 0) {
               HACRibbonIndicator.getInstance().updateIndicator();
               return;
            }
      }
   }

   @Override
   public final void dialogClosed(PopupDialog dialog, int closeReason) {
      if (!(dialog instanceof NetworkOrStkMsgDlg)) {
         Object cookie = dialog.getCookie();
         if (cookie == USSD_INPUT_DIALOG_COOKIE) {
            byte[] responseBytes = new byte[0];
            if (closeReason == 0) {
               SimpleInputDialog dlg = (SimpleInputDialog)dialog;
               String userInput = dlg.getText();
               responseBytes = this.convertInput(userInput);
            } else {
               responseBytes = null;
               this.activate();
            }

            try {
               Phone.getInstance().setUSSDResponse(responseBytes);
               return;
            } finally {
               return;
            }
         }
      } else {
         NetworkOrStkMsgDlg dlg = (NetworkOrStkMsgDlg)dialog;
         switch (dlg._type) {
            case 0:
               this.handleCallSetupUserResponse(closeReason == 0, dlg.getMsg2());
               return;
         }
      }
   }

   @Override
   public final boolean stopCurrentCall(Object context) {
      CallManager callMgr = CallManager.getInstance();
      if (callMgr != null) {
         LiveCall currentCall = (LiveCall)callMgr.getCurrentCall();
         if (currentCall != null) {
            currentCall.endByUser(context);
            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean stopConferenceCall() {
      CallManager callMgr = CallManager.getInstance();
      if (callMgr != null) {
         ConferenceCall conferenceCall = callMgr.getConferenceCall();
         if (conferenceCall != null) {
            conferenceCall.endByUser();
            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean stopAllCalls(Object context) {
      CallManager cm = CallManager.getInstance();
      Vector calls = cm.getCurrentCalls();
      if (calls != null && calls.size() > 0) {
         int num = calls.size();

         for (int i = num - 1; i >= 0; i--) {
            LiveCall call = (LiveCall)calls.elementAt(i);
            if (call instanceof Object) {
               call.endByUser(context);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final Object getSpeedDialInfo(char key) {
      return this.getSpeedDialInfo(key, false);
   }

   @Override
   public final Object getSpeedDialInfo(char key, boolean suppressPrompt) {
      QuickContactItem qci = QuickContactList.getInstance().getQuickContactItem(key);
      if (qci instanceof Object) {
         SpeedDialItem sdi = (SpeedDialItem)qci;
         CallerIDInfo cidi = sdi.getCallerIDInfo();
         Object number = null;
         Object address = cidi.getAddress();
         boolean voicemail = cidi.isVoicemailCallerIDInfo();
         if (voicemail) {
            number = PhoneUtilities.getVoiceMailNumber();
         } else {
            number = cidi.getNumber();
         }

         if (number != null) {
            String numberString = number.toString();
            ContextObject tmpContext = (ContextObject)(new Object());
            if (voicemail) {
               PhoneUtilities.setPrivateFlag(tmpContext, 7);
            }

            if (suppressPrompt) {
               PhoneUtilities.setPrivateFlag(tmpContext, 91);
            }

            Object connectionParameters = PhoneUtilities.getCallConnectionParameters(number, address, cidi, tmpContext);
            if (connectionParameters instanceof Object) {
               ((LongHashtable)connectionParameters).put(247, numberString);
            }

            return connectionParameters;
         }
      }

      return null;
   }

   @Override
   public final Object getRedialInfo() {
      if (PersistentContent.getTicket() == null) {
         return null;
      }

      PersistentObject store = RIMPersistentStore.getPersistentObject(-5114641656481802126L);
      Object contents = store.getContents();
      if (contents instanceof Object) {
         CallerIDInfo cidi = (CallerIDInfo)contents;
         Object number = null;
         if (cidi.isVoicemailCallerIDInfo()) {
            number = PhoneUtilities.getVoiceMailNumber();
         } else {
            number = cidi.getNumber();
         }

         if (number != null) {
            String numberAsString = number.toString();
            if (numberAsString.length() > 0) {
               ContextObject result = (ContextObject)(new Object());
               result.put(247, numberAsString);
               result.put(5898398779440734986L, cidi);
               return result;
            }
         }
      }

      return null;
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      PersistentObject store = RIMPersistentStore.getPersistentObject(-5114641656481802126L);
      synchronized (store) {
         Object contents = store.getContents();
         if (contents instanceof Object) {
            CallerIDInfo cidi = (CallerIDInfo)store.getContents();
            if (!cidi.checkCrypt(true, true)) {
               Object newObject = cidi.reCrypt(true, true);
               if (newObject != null) {
                  store.setContents(newObject, 51);
               }

               store.commit();
            }
         }
      }
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   @Override
   public final Object getCurrentCall() {
      return CallManager.getInstance().getCurrentCall();
   }

   @Override
   public final void editCallNotes() {
      this._editingNotes = true;
      if (!this.isForeground()) {
         this._foregroundedToEditNotes = true;
         this.requestForeground();
      } else {
         this._foregroundedToEditNotes = false;
         this.doEditCallNotes();
      }
   }

   @Override
   public final void run() {
      this._idleScreen = new PhoneAppScreen(this, this, false, null);
      this.pushScreen(this._idleScreen);
      this.enterEventDispatcher();
   }

   @Override
   public final boolean inForeground() {
      return this.isForeground();
   }

   @Override
   public final Object getIncomingCall() {
      return CallManager.getInstance().getIncomingCall();
   }

   @Override
   public final Object getInactiveCall() {
      return CallManager.getInstance().getInactiveCall();
   }

   @Override
   public final Vector getCurrentCalls() {
      return CallManager.getInstance().getCurrentCalls();
   }

   @Override
   public final Object getCallCacheLock() {
      return CallManager.getInstance().getCallCacheLock();
   }

   @Override
   public final void displayScreen(Screen screen) {
      this.pushScreen(screen);
   }

   @Override
   public final void onVoiceAppExit() {
      this._activeScreen.stopListening();
      this._activeScreen = null;
      this._idleScreen.stopListening();
      this._idleScreen = null;
      if (this._gsm230Filter != null) {
         this._gsm230Filter.deregister();
         this._gsm230Filter = null;
      }
   }

   @Override
   public final void requestForeground(Runnable runnable, Object context) {
      if (this.isForeground()) {
         if (runnable != null) {
            if (Application.getApplication() instanceof Object) {
               runnable.run();
               return;
            }

            this.invokeLater(runnable);
         }
      } else {
         this._activationRunnable = runnable;
         this._appActivationContext = ContextObject.clone(context);
         this._activatedBySENDKey = ContextObject.getFlag(context, 119);
         if (ContextObject.getFlag(context, 96)) {
            this._switchBackgroundOnIdle = true;
            this._switchBackgroundConfirmation = (Confirmation)ContextObject.get(context, 8128293842573788963L);
         } else if (PhoneUtilities.getPrivateFlag(context, 71)) {
            if (this._switchBackgroundOnIdle) {
               this._switchBackgroundOnIdle = false;
               this._switchBackgroundConfirmation = null;
            }

            this._foregroundToShowCallDisconnection = true;
         }

         this.requestForeground();
      }
   }

   @Override
   public final void placeCall() {
      VoiceServices.getVoiceApplication().requestForeground(null, null);
   }

   @Override
   public final void startEmergencyCall(String number, boolean backgroundOnCompletion) {
      PhoneLogger.log("startEmergCall");
      ContextObject context = (ContextObject)(new Object());
      if (backgroundOnCompletion) {
         context.setFlag(96);
      }

      if (PhoneUtilities.getDebugFlag(8128272366344448397L)) {
         number = "15198887465";
      }

      PhoneUtilities.setPrivateFlag(context, 33);
      Object params = PhoneUtilities.getCallConnectionParameters(number, null, null, context);
      if (params != null) {
         OutgoingCallConnector.startCall(params);
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -2282475915901395486L) {
         PhoneFolders.registerFoldersWithMergedMessageCollection();
      } else {
         if (guid == 5508103077902582983L) {
            int oldShowCallLogsOption = data0;
            int newShowCallLogsOption = data1;
            if (oldShowCallLogsOption != newShowCallLogsOption) {
               Runnable runnable = new VoiceApp$8(this, oldShowCallLogsOption, newShowCallLogsOption);
               PhoneUtilities.performLongRunningOperationWithStatus(runnable, PhoneResources.getString(6288));
               switch (newShowCallLogsOption) {
                  case -1:
                     break;
                  case 0:
                  case 1:
                  case 3:
                  default:
                     if (oldShowCallLogsOption == 2) {
                        VoiceUnopenedCount.getInstance().showMissedCallsInMessageListOptionChanged(true);
                        return;
                     }
                     break;
                  case 2:
                     VoiceUnopenedCount.getInstance().showMissedCallsInMessageListOptionChanged(false);
                     return;
               }
            }
         } else {
            if (guid == 7207871974803693937L) {
               PhoneCallModelImpl.updateDateTimeFormats();
               return;
            }

            if (guid == -3502867315182341539L) {
               if (data0 == 0 && this._activeScreen.isDisplayed()) {
                  this.requestForeground();
                  return;
               }

               this.showHomeScreen();
               return;
            }

            if (guid == -7131874474196788121L) {
               if (this._activeScreen.isDisplayed()) {
                  this.requestForeground();
                  return;
               }
            } else if (guid == 8508406279413621091L) {
               boolean phoneEnabled = ITPolicy.getBoolean(1, true);
               if (phoneEnabled != this._phoneEnabled) {
                  this._phoneEnabled = phoneEnabled;
                  if (phoneEnabled) {
                     this.registerVerbs();
                     return;
                  }

                  deregisterVerbs();
               }
            }
         }
      }
   }

   @Override
   public final Field getField(Object context) {
      if (context instanceof Object) {
         int id = context;
         switch (id) {
            case -1:
               break;
            case 0:
            case 1:
            case 2:
            case 3:
            default:
               return getRibbonComponent(id);
         }
      }

      return null;
   }

   @Override
   public final int getOrder(Object context) {
      return 0;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   private final void updateCFUIndicator(boolean state) {
      CallForwardRibbonIndicator.getInstance().updateIndicators(state);
   }

   @Override
   public final void deactivate() {
      PhoneLogger.log("app-deac");
      if (this._switchedBackgroundOnIdle && this._knownMissedCall) {
         MissedCallIndicator.getInstance().enable();
         IndicatorManager im = IndicatorManager.getInstance();
         if (im != null) {
            im.updateIndicators();
         }
      } else if (!this._active) {
         this.clearNewMessages();
      }

      if (this._active && this._switchedBackgroundOnIdle) {
         this._switchedBackgroundOnIdle = false;
         PhoneLogger.log("newly-actv-rqstFG");
         this.requestForeground(this._showActiveScreenRunnable, null);
      } else {
         if (this._editingNotes && this._editNotesScreen != null) {
            this._editingNotes = false;
            this._editNotesScreen.appDeactivated();

            label62:
            try {
               this.popScreen(this._editNotesScreen);
            } finally {
               break label62;
            }

            this._editNotesScreen = null;
         }

         if (!this._active) {
            this.popActiveScreen();
         }

         if (this.getActiveScreen() == this._idleScreen) {
            Audio.enable(false);
         }

         this._switchedBackgroundOnIdle = false;
         VoiceServices.broadcastEvent(2101);
         super.deactivate();
      }
   }

   private final boolean isPTTKey(int keycode) {
      PTTKeyHandler hdlr = this.getPTTKeyHandler();
      return hdlr != null && hdlr.isPTTKey(keycode);
   }

   private final PTTKeyHandler getPTTKeyHandler() {
      PTTKeyHandler handler = (PTTKeyHandler)ApplicationRegistry.getApplicationRegistry().get(-7975050928526187730L);
      return handler != null ? handler : null;
   }

   private static final void storeRedialInfo(Object lastCallInfo) {
      if (!PhoneUtilities.emergencyCall(lastCallInfo)) {
         String phoneNumber = null;
         CallerIDInfo cidi = null;
         if (!(lastCallInfo instanceof Object)) {
            if (lastCallInfo instanceof Object) {
               phoneNumber = (String)ContextObject.get(lastCallInfo, 6486659828352467672L);
               cidi = (CallerIDInfo)ContextObject.get(lastCallInfo, 5898398779440734986L);
               cidi = (CallerIDInfo)(new Object(cidi, false));
            }
         } else {
            phoneNumber = (String)lastCallInfo;
            cidi = PhoneUtilities.createCallerIDInfo(phoneNumber);
         }

         if (!PhoneUtilities.isCDMAServiceCall(phoneNumber)) {
            if (cidi != null) {
               PersistentObject store = RIMPersistentStore.getPersistentObject(-5114641656481802126L);
               if (cidi instanceof Object) {
                  EncryptableProvider encryptableProvider = cidi;
                  if (!encryptableProvider.checkCrypt(true, true)) {
                     encryptableProvider.reCrypt(true, true);
                  }
               }

               synchronized (store) {
                  store.setContents(cidi, 51);
                  store.commit();
               }
            }
         }
      }
   }

   @Override
   protected final boolean allowKeyEventFromPreviousApp(int event, int keycode) {
      return (event == 515 || event == 514) && this.isPTTKey(keycode);
   }

   private final void hideActiveScreenNow() {
      this._hidingActiveScreen = true;
      this.invokeLater(this._hideActiveScreenRunnable);
   }

   private final void clearNewMessages() {
      SimpleFolder missedCallFolder = PhoneFolders.getPhoneFolder(7042951934619290849L);
      if (missedCallFolder != null) {
         synchronized (missedCallFolder) {
            ReadableList items = (ReadableList)missedCallFolder.getContainedItems();
            int length = -1;
            if (items != null && (length = items.size()) >= 1) {
               for (int i = length - 1; i >= 0; i--) {
                  Object model = items.getAt(i);
                  if (model instanceof Object) {
                     ((ActionProvider)model).perform(5213547777258110094L, null);
                  }
               }

               MissedCallIndicator.getInstance().disable();
            }
         }
      }
   }

   private static final void deregisterVerbs(long repositoryName, long type) {
      VerbRepository vr = VerbRepository.getVerbRepository(repositoryName);
      Verb[] verbs = vr.getVerbs(null);

      for (int i = 0; i < verbs.length; i++) {
         Verb verb = verbs[i];
         if (verb instanceof Object) {
            vr.deregister(verb, type);
         }
      }
   }

   private static final void deregisterVerbs() {
      deregisterVerbs(8016149483483360697L, 3797587162219887872L);
      deregisterVerbs(-7881764549058890736L, 3797587162219887872L);
      deregisterVerbs(733292209077921914L, 3797587162219887872L);
      deregisterVerbs(-6761056765378641298L, 3797587162219887872L);
      deregisterVerbs(-1180661228443544094L, 3797587162219887872L);
      deregisterVerbs(3391562901592837683L, 3797587162219887872L);
      deregisterVerbs(-5389783330697330291L, 3797587162219887872L);
   }

   private final void registerVerbs() {
      DialVerb dialVerb = (DialVerb)(new Object());
      VoiceMailVerb voicemailVerb = (VoiceMailVerb)(new Object());
      Verb newCallVerb = InitiateCallVerb.getSingleton();
      VerbRepository.getVerbRepository(8016149483483360697L).register((Verb)(new Object(newCallVerb)), 3797587162219887872L);
      VerbRepository.getVerbRepository(-7881764549058890736L).register(newCallVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(733292209077921914L).register(dialVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(-6761056765378641298L).register(dialVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(-1180661228443544094L).register(voicemailVerb, 3797587162219887872L);
      VerbRepository.getVerbRepository(3391562901592837683L).register((Verb)(new Object()), 3797587162219887872L);
   }

   private static final void initializeComponents(VoiceApp voiceApp) {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      if (appRegistry.get(8424795840406028030L) == null) {
         appRegistry.put(8424795840406028030L, "net.rim.device.apps.internal.phone.VoiceApp");
      }

      appRegistry.replace(8059360440319940910L, new Object());
      ContextObject context = (ContextObject)(new Object());
      context.put(-5475650791114535957L, voiceApp);
      CallManager.initialize(context);
      CallManager.getInstance().addListener(voiceApp);
      RIMPhone rimPhone = RIMPhone.initialize(voiceApp);
      VoiceServices.addPhoneEventListener(rimPhone);
      VoiceServices.addPhoneEventListener(voiceApp);
      InHolsterEventHandler.register(voiceApp, rimPhone);
      PhoneVerbManager.initialize();
      CallTimerIndicator.initialize();
      SSManager.initialize();
      registerPromptForCorporatePhoneNumber();
      net.rim.device.apps.internal.phone.data.PackageManager.registerOnceOnSystemStart();
      CallForwardRibbonIndicator.initialize();
      MissedCallIndicator.initialize();
      TTYRibbonIndicator.initialize();
      HACRibbonIndicator.initialize();
      PackageManager.registerOnceOnSystemStart();
      PhoneLogger.init();
      CallLogCollectionCleaner.initialize(voiceApp);
      initRibbonBarComponents();
   }

   private final void doEditCallNotes() {
      LiveCall call = (LiveCall)this.getCurrentCall();
      if (call != null) {
         EditCallNotesScreen screen = new EditCallNotesScreen(call, this);
         this._editNotesScreen = screen;
         this.pushScreen(screen);
      }
   }

   private static final void startVoiceServices() {
      VoiceServices.initializeOnSystemStart(null);
   }

   private final void handleCallerIDUpdated(int callId) {
      if (this._activeScreen.isDisplayed()) {
         Object callerIDInfo = RIMPhone.getInstance().getUpdatedCallerIDInfo(callId);
         this._activeScreen.callerIDUpdated(callId, callerIDInfo);
      }
   }

   static final void registerPromptForCorporatePhoneNumber() {
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      if (appReg.get(-4905871728754809133L) == null) {
         Runnable prompter = new VoiceApp$1();
         appReg.put(-4905871728754809133L, prompter);
      }
   }

   public VoiceApp(Object context) {
      this.addGlobalEventListener(this);
      this.enableKeyUpEvents(true);
      PersistentContent.addWeakListener(this);
      this._activeScreen = new ActivePhoneScreen(this, null);
      if (CallManager.getInstance().getNumCalls() > 0) {
         this._active = true;
      }

      if (DeviceInfo.isSimulator()) {
         label31:
         try {
            Phone.getInstance().activateCallWaiting(true);
         } finally {
            break label31;
         }
      }

      deregisterVerbs();
      this._phoneEnabled = ITPolicy.getBoolean(1, true);
      if (this._phoneEnabled) {
         this.registerVerbs();
      }

      VerbRepository.getVerbRepository(-5389783330697330291L).register((Verb)(new Object()), 3797587162219887872L);
   }

   private static final void audioSourcePhoneOn() {
      AudioRouter audioRouter = AudioRouter.getInstance();
      if (!audioRouter.isSourceAdded(0)) {
         System.out.println("phone: audio source on");
         audioRouter.addSource(0);
      }
   }

   private final void popActiveScreen() {
      if (this._activeScreen.isDisplayed()) {
         this.popScreen(this._activeScreen);
         PhoneLogger.log("poppedactvscrn");
      }
   }

   private final void hideActiveScreenLater() {
      this._hidingActiveScreen = true;
      this.invokeLater(this._hideActiveScreenRunnable, 1500, false);
   }

   public static final void main(String[] args) {
      if (!Phone.isSupported()) {
         throw new Object("Phone is not supported on this network.");
      }

      startVoiceServices();
      TTYOption.updateTTYMode();
      HACOption.updateHACMode();
      ApplicationManager appMgr = ApplicationManager.getApplicationManager();
      appMgr.setInHolsterInputProcess();
      ((ApplicationManagerInternal)appMgr).setSecurityLockSupercedingProcess();
      VoiceApp voiceAppInstance = new VoiceApp(null);
      VoiceServices.setVoiceApplication(voiceAppInstance);
      PhoneOptions.getOptions().enableSynchronization();
      initializeComponents(voiceAppInstance);
      TTYRibbonIndicator.getInstance().updateIndicator();
      if ((Phone.getInstance().getNetworkFeatures() & 524288) != 0) {
         HACRibbonIndicator.getInstance().updateIndicator();
      }

      if (GSM230Filter.isSupported()) {
         voiceAppInstance._gsm230Filter = (GSM230Filter)(new Object());
         voiceAppInstance._gsm230Filter.register();
      }

      RibbonLauncher launcher = RibbonLauncher.getInstance();
      if (launcher != null) {
         launcher.disableHotKeys(PhoneUtilities.isQwertyReducedKeyboard() ? true : PhoneOptions.getOptions().getBooleanOption(16384));
      }

      if (PhoneUtilities.cdmaTypeNetwork()) {
         voiceAppInstance.updateCFUIndicator();
      }

      PhoneEntry entry = new PhoneEntry();
      entry.register();
      MissedCallIndicator.getInstance().setAction(entry);
      entry.set(12, VoiceUnopenedCount.getInstance());
      VoicemailEntry vmailEntry = new VoicemailEntry();
      vmailEntry.register();
      voiceAppInstance.run();
   }

   private final void handleCallIncoming(int callId) {
      if (!this._activeScreen.isDisplayed()) {
         LiveCall incomingCall = (LiveCall)this.getIncomingCall();
         this._activeScreen.setIncomingCall(incomingCall);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final byte[] convertInput(String input) {
      byte[] responseBytes = null;
      if (input != null && input.length() > 0) {
         boolean var5 = false /* VF: Semaphore variable */;

         try {
            var5 = true;
            responseBytes = input.getBytes("SMS");
            String e = new Object(responseBytes, "SMS");
            if (!input.equals(e)) {
               return input.getBytes("UTF-16BE");
            }

            var5 = false;
         } finally {
            if (var5) {
               Out.p("PHONE-USSD-unsupp-encoding");
               return null;
            }
         }

         return responseBytes;
      } else {
         return null;
      }
   }

   @Override
   public final void activate() {
      this._knownMissedCall = MissedCallIndicator.getInstance().isEnabled();
      this._foregroundToShowCallDisconnection = false;
      if (this._activatedForAnsweredIncomingCall) {
         this._activatedForAnsweredIncomingCall = false;
         if (!this._activeScreen.isDisplayed()) {
            this._showActiveScreenRunnable.run();
         }

         super.activate();
      } else {
         VoiceServices.broadcastEvent(2100);
         if (this._editingNotes) {
            this.doEditCallNotes();
            super.activate();
         } else {
            if (this._activationRunnable != null) {
               this._activationRunnable.run();
               this._activationRunnable = null;
            } else if (this._active && !this._activeScreen.isDisplayed()) {
               this._showActiveScreenRunnable.run();
               this._activationRunnable = null;
               super.activate();
               return;
            }

            char inputKey = 0;
            Character chObj = (Character)ContextObject.get(this._appActivationContext, -2621706732407506661L);
            if (chObj != null) {
               if (ApplicationManager.getApplicationManager().isSystemLocked()) {
                  this.requestBackground();
                  this._appActivationContext = null;
                  super.activate();
                  return;
               }

               inputKey = chObj;
            }

            Screen activeScreen = null;
            if (this._activatedBySENDKey
               || inputKey != 0
               || PhoneUtilities.getPrivateFlag(this._appActivationContext, 72)
               || ApplicationManager.getApplicationManager().isSystemLocked()) {
               for (Screen var7 = this.getActiveScreen(); var7 != this._idleScreen && var7 != this._activeScreen; var7 = this.getActiveScreen()) {
                  try {
                     this.popScreen(var7);
                  } finally {
                     continue;
                  }
               }
            }

            activeScreen = this.getActiveScreen();
            if (activeScreen instanceof Object) {
               ((PhoneAwareScreen)activeScreen).onAppActivated(this._appActivationContext, inputKey);
            }

            this._appActivationContext = null;
            this._activatedBySENDKey = false;
            super.activate();
         }
      }
   }

   private final void handleCallSetupUserResponse(boolean proceed, String msg) {
      boolean simulator = DeviceInfo.isSimulator();
      boolean callPermitted = OutgoingCallConnector.outgoingCallPermitted();
      if (!callPermitted) {
         Dialog.alert(PhoneResources.getString(146));
         proceed = false;
      }

      try {
         if (!proceed) {
            if (!simulator) {
               SIMCard.atSetUpCallAck(false);
            }
         } else {
            int setupCallId;
            if (simulator) {
               setupCallId = 1234567;
            } else {
               setupCallId = SIMCard.atSetUpCallAck(true);
            }

            Object cidInfo = null;
            ContextObject callSetupContext = null;
            if (setupCallId != 0) {
               String number = null;

               label119:
               try {
                  if (!simulator) {
                     number = Phone.getInstance().getCallPhoneNumber(setupCallId);
                  } else {
                     number = "1234567";
                  }

                  if (number == null) {
                     number = "";
                  }

                  callSetupContext = (ContextObject)(new Object());
                  PhoneUtilities.setPrivateFlag(callSetupContext, 62);
                  if (msg != null && msg.length() > 0) {
                     callSetupContext.put(-6776810758251164441L, msg);
                  }

                  Object numberModel = PhoneUtilities.createNumberModel(number);
                  if (numberModel != null) {
                     if (!simulator) {
                        cidInfo = PhoneUtilities.createCallerIDInfo((RIMModel)numberModel, 24, setupCallId, null);
                     } else {
                        cidInfo = new Object((PersistableRIMModel)numberModel, null, false, false);
                     }

                     if (cidInfo != null) {
                        callSetupContext.put(5898398779440734986L, cidInfo);
                     }
                  }
               } finally {
                  break label119;
               }

               if (cidInfo != null) {
                  VoiceServices.broadcastEvent(1100, setupCallId, callSetupContext);
                  return;
               }
            }
         }
      } finally {
         ;
      }
   }

   private final void handleCallSetupMessage(int action, boolean promptUser, Object context) {
      byte[][] messages = (byte[][])context;
      String[] callSetupMessages = new Object[2];
      if (messages instanceof byte[][]) {
         for (int i = 0; i < messages.length; i++) {
            callSetupMessages[i] = SIMCard.decodeAlphaId(messages[i]);
            Out.p(((StringBuffer)(new Object("PHONE-CS-msg="))).append(callSetupMessages[i]).toString());
         }
      }

      if (!promptUser) {
         this.handleCallSetupUserResponse(true, callSetupMessages[1]);
      } else {
         if (callSetupMessages[0] == null || callSetupMessages[0].length() == 0) {
            callSetupMessages[0] = PhoneResources.getString(6077);
         }

         NetworkOrStkMsgDlg callSetupDialog = null;
         if (promptUser) {
            callSetupDialog = new NetworkOrStkMsgDlg(
               callSetupMessages[0], callSetupMessages[1], 0, CommonResources.getYesNoArray(0), new int[]{0, -1, -804651005, 1, 1, 1234, -804651005, 3}
            );
         } else {
            callSetupDialog = new NetworkOrStkMsgDlg(callSetupMessages[0], callSetupMessages[1], 2, null, null);
         }

         callSetupDialog.setPopupDialogClosedListener(this);
         Screen screen = this.getActiveScreen();
         if (!(screen instanceof CallControlInformationDialog)) {
            callSetupDialog.show();
         } else {
            CallControlInformationDialog dlg = (CallControlInformationDialog)screen;
            dlg.showOnCompletion(callSetupDialog);
            dlg.setTimeout(2000);
         }
      }
   }

   private static final void initRibbonBarComponents() {
      RIBBON_COMPONENTS = new Object[COMPONENT_MAP.length];
      RibbonBanner ribbon = RibbonBanner.getInstance();
      if (ribbon != null) {
         for (int i = 0; i < COMPONENT_MAP.length; i++) {
            RIBBON_COMPONENTS[i] = ribbon.getStatusBanner(null, COMPONENT_MAP[i]);
         }
      }
   }

   public static final Field getRibbonComponent(int component) {
      if (RIBBON_COMPONENTS == null) {
         initRibbonBarComponents();
      }

      if (component >= 0 && component < RIBBON_COMPONENTS.length) {
         Field field = RIBBON_COMPONENTS[component];
         if (field != null) {
            Manager manager = field.getManager();
            if (manager != null) {
               if (manager.isValidLayout()) {
                  RibbonBanner ribbon = RibbonBanner.getInstance();
                  field = ribbon.getStatusBanner(null, COMPONENT_MAP[component]);
                  RIBBON_COMPONENTS[component] = field;
                  return field;
               }

               field.setChangeListener(null);
               field.setFocusListener(null);
               manager.delete(field);
            }
         }

         return field;
      } else {
         throw new Object();
      }
   }

   private final void handleUssdDisplay(int collectInput, Object context) {
      String msg = (String)context;
      Out.p(((StringBuffer)(new Object("PHONE-USSD "))).append(msg).toString());
      Out.p(((StringBuffer)(new Object("PHONE-USSD-input="))).append(collectInput).toString());
      if (!DeviceInfo.isInHolster()) {
         Backlight.enable(true);
      }

      if (!PhoneUtilities.isQuietProfileOn()) {
         if (Alert.isMIDISupported()) {
            Alert.setVolume(85);
            Alert.startMIDI(CallTunes.getPolyTune(8), 2);
         } else if (Alert.isAudioSupported()) {
            Alert.startAudio(CallTunes.getTune(8), 100);
         }
      }

      if (collectInput == 1) {
         SimpleInputDialog dlg = new UssdInputDialog(msg);
         dlg.setCookie(USSD_INPUT_DIALOG_COOKIE);
         dlg.setPopupDialogClosedListener(this);
         dlg.insert((Field)(new Object(PhoneResources.getString(6057))), 0);
         dlg.insert((Field)(new Object()), 1);
         dlg.show();
      } else {
         Out.p(((StringBuffer)(new Object("PHONE-USSD-showmsg:"))).append(msg).toString());
         new NetworkOrStkMsgDlg(msg, null, 1, new Object[]{CommonResources.getString(117)}, null, true).show();
      }
   }

   private final int getCallControlEventStringId(int callControlEvent) {
      switch (callControlEvent) {
         case 2:
            return 20;
         case 5:
            return 24;
         case 7:
            return 26;
         case 8:
            return 27;
         case 12:
            return 29;
         case 14:
            return 26;
         default:
            return -1;
      }
   }

   private final void updateCFUIndicator() {
      CallForwardRibbonIndicator.getInstance().updateIndicators();
   }
}
