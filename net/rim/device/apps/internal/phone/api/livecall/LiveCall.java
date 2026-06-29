package net.rim.device.apps.internal.phone.api.livecall;

import java.util.Vector;
import net.rim.device.api.media.control.AudioPathControl;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.CallTimerListener;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceApplication;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.AddressBookDependentObject;
import net.rim.device.apps.internal.phone.api.DTMFEcho;
import net.rim.device.apps.internal.phone.api.DTMFToneQueue;
import net.rim.device.apps.internal.phone.api.DroppedCallListener;
import net.rim.device.apps.internal.phone.api.EnhanceCallAudioServices;
import net.rim.device.apps.internal.phone.api.Out;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;
import net.rim.device.apps.internal.phone.api.PhoneCallModel;
import net.rim.device.apps.internal.phone.api.PhoneLogger;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.verbs.EnhanceCallAudioVerb;
import net.rim.device.apps.internal.phone.api.verbs.FlashVerb;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.data.PhoneFolders;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberServices;
import net.rim.device.apps.internal.phone.options.CallTunes;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.system.AudioInternal;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Array;

public class LiveCall
   extends AddressBookListener
   implements RIMModel,
   PhoneEventListener,
   CallTimerListener,
   FieldProvider,
   PaintProvider,
   KeyProvider,
   VerbProvider {
   protected int _callId;
   PhoneCallModel _phoneCallModel;
   protected CallerIDInfo _callerIDInfo;
   int _elapsedTime;
   long _timeStamp;
   private int _errorCode;
   private String _userDialedPhoneNumber;
   private String _transmittedPhoneNumber;
   protected String _redirectedNumber;
   private String _transferNumber;
   private int _flags;
   private DroppedCallListener _droppedCallListener;
   private DTMFToneQueue _dtmfQueue;
   public static final int CONNECTED;
   public static final int DISCONNECTED;
   public static final int INCOMING;
   public static final int CONFERENCE_MEMBER;
   public static final int ENDING;
   public static final int MUTED;
   public static final int ADVANCED_PRIVACY;
   public static final int EMERGENCY_CALL;
   public static final int OTA_CALL;
   public static final int INSERT_CALLER_NAME;
   public static final int NO_NOTES;
   public static final int REDIRECTED;
   public static final int WAS_CONFERENCE_MEMBER;
   public static final int ENDED_BY_ANSWER_DROP_CURRENT;
   public static final int HAS_FLASH_BY_USER;
   public static final int ENDED_BY_USER;
   public static final int SAVED;
   public static final int NO_MUTE;
   public static final int SHOW_TRANSMITTED_NUMBER;
   public static final int CALL_DELIVERED;
   public static final int CALL_TO_VOICEMAIL;
   public static final int FAILED;
   public static final int STATUS_CONNECTING;
   public static final int STATUS_CONNECTED;
   public static final int STATUS_CONNECTED_MUTED;
   public static final int STATUS_HELD;
   public static final int STATUS_DISCONNECTED;
   public static final int STATUS_TRANSFERRING;
   public static final int STATUS_COUNT;
   public static final int CALL_TYPE_INTERCONNECT;
   public static final int CALL_TYPE_DIRECTCONNECT;
   private static String _longestStatusString = null;
   private static final int END_TUNE_VOLUME;
   private static final int FAIL_TUNE_VOLUME;
   private static final int MUTE_TUNE_VOLUME;
   private static final int SHOW_TRANSMITTED_NUMBER_TIMEOUT;
   static final int LOG_CALL_MUTE;
   static final int LOG_CALL_HOLD;
   static final int LOG_CALL_RESUME;
   static final int LOG_CALL_END_BY_USER;
   public static final int LOG_CALL_ID;

   protected void onConstruction(PhoneCallInitialData data, Object context) {
   }

   public String longestStatusString() {
      return getLongestStatusString();
   }

   protected DTMFToneQueue createDTMFToneQueue() {
      return new DTMFToneQueue(this._callId);
   }

   protected PhoneCallModel createPhoneCallModel(PhoneCallInitialData data) {
      return (PhoneCallModel)PhoneUtilities.createPhoneCallModel(data);
   }

   public void end(Object context) {
      if (context != null) {
         this._droppedCallListener = (DroppedCallListener)ContextObject.get(context, 1714907342028355590L);
         if (PhoneUtilities.getPrivateFlag(context, 56)) {
            this.setFlag(16384);
         }
      }

      if (RadioInfo.getNetworkType() == 5 && this.getFlag(2) && !this.getFlag(4) && VoiceServices.getCallState(this.getCallId()) == 0) {
         VoiceServices.broadcastEvent(1002, this.getCallId(), null);
      }

      if (this.getFlag(4) && this._droppedCallListener != null) {
         this._droppedCallListener.callDisconnected(this._callId);
      }

      if (RadioInfo.getNetworkType() != 5 || !this.getFlag(4)) {
         PhoneUtilities.playInCallTune(0, END_TUNE_VOLUME, true);
         Backlight.enable(true);
      }

      if (!this.getFlag(4)) {
         VoiceServices.stopCall(this._callId);
      }
   }

   public final void end() {
      this.end(null);
   }

   public final void endByUser() {
      this.endByUser(null);
   }

   public void endByUser(Object context) {
      PhoneLogger.log(((StringBuffer)(new Object("endcallbyuser "))).append(this._callId).toString());
      Out.p(1128352844, 1162757205, this._callId);
      this.setFlag(65536);
      this.end(context);
      Backlight.enable(true);
      if (this.canRemoveCallImmediately(context)) {
         VoiceServices.broadcastEvent(3004, this.getCallId(), context);
         UiApplication voiceApp = VoiceServices.getUiApplication();
         synchronized (voiceApp.getAppEventLock()) {
            voiceApp.repaint();
            Out.p("Phone: paint disconnect done.");
         }
      } else {
         UiApplication.getUiApplication().getActiveScreen().invalidate();
      }
   }

   public String getCallerIDDisplayString() {
      return null;
   }

   public boolean showCallTimer() {
      return true;
   }

   protected boolean canRemoveCallImmediately(Object context) {
      return this.getFlag(4) ? true : RadioInfo.getNetworkType() != 5 || VoiceServices.getCallState(this.getCallId()) == 0;
   }

   public boolean startDTMFTone(char tone) {
      DTMFEcho.startTone(tone);
      return this._dtmfQueue.startTone(tone);
   }

   public void stopDTMFTone() {
      DTMFEcho.stopTone();
      this._dtmfQueue.stopTone();
   }

   public boolean sendDTMFTone(char tone) {
      DTMFEcho.echoTone(tone);
      return this._dtmfQueue.appendTone(tone);
   }

   public boolean sendDTMFTones(String tones, boolean programmatic) {
      int length = tones.length();

      for (int idx = 0; idx < length; idx++) {
         boolean sent = this._dtmfQueue.appendTone(tones.charAt(idx), programmatic);
         if (!sent) {
            return false;
         }
      }

      return true;
   }

   public String getDTMFTones() {
      return this._dtmfQueue.getTones();
   }

   public boolean getFlag(int flag) {
      return (this._flags & flag) != 0;
   }

   public void setFlag(int flag) {
      this._flags |= flag;
   }

   protected void clearFlag(int flag) {
      this._flags &= ~flag;
   }

   public boolean isValid() {
      if (this.isConnecting()) {
         return true;
      }

      int state = this.getCallState();
      return state == 3 || state == 4 || state == 5;
   }

   public boolean isRedirected(String connectedNumber) {
      if (supportsCallRedirectedApi()) {
         return VoiceServices.isCallRedirected(this._callId);
      }

      if (connectedNumber != null && connectedNumber.length() > 0 && this._transmittedPhoneNumber != null && this._transmittedPhoneNumber.length() > 0) {
         Object num1 = PhoneUtilities.createNumberModel(this._transmittedPhoneNumber);
         Object num2 = PhoneUtilities.createNumberModel(connectedNumber);
         if (num1 instanceof Object) {
            if (!((AbstractPhoneNumberModel)num1).equals(num2, false)) {
               return true;
            }

            return false;
         }
      }

      return false;
   }

   public void setRedirected(String redirectedNumber) {
      this.setFlag(4096);
      this._redirectedNumber = redirectedNumber;
   }

   public String getRedirectedNumber() {
      return this.getFlag(4096) ? this._redirectedNumber : null;
   }

   public void setTransferNumber(String transferNumber) {
      this._transferNumber = transferNumber;
   }

   public String getTransferNumber() {
      return this._transferNumber;
   }

   public void startListeningForPhoneEvents() {
      VoiceServices.addPhoneEventListener(this);
      VoiceServices.addCallTimerListener(this);
   }

   public boolean hasAdvancedPrivacy() {
      return this.getFlag(128);
   }

   public boolean isMuted() {
      return this.getFlag(64);
   }

   public void mute() {
      if (!this.getFlag(262144)) {
         if (!this.isHeld()) {
            if (this.isMuted()) {
               int tuneLength = PhoneUtilities.playInCallTune(4, MUTE_TUNE_VOLUME);
               Runnable muteRunner = new LiveCall$2(this);
               VoiceServices.getUiApplication().invokeLater(muteRunner, tuneLength, false);
               return;
            }

            AudioInternal.mute(true);
            this.setFlag(64);
            Out.p(1128352844, 1297437765, this._callId, this.isMuted());
            VoiceServices.broadcastEvent(150040, this._callId, this);
            PhoneUtilities.playInCallTune(3, MUTE_TUNE_VOLUME);
         }
      }
   }

   public boolean isOutgoing() {
      return !this.getFlag(8);
   }

   public CallerIDInfo getCallerIDInfo() {
      return this._callerIDInfo;
   }

   public void setCallerIDInfo(CallerIDInfo callerIDInfo) {
      this._callerIDInfo = callerIDInfo;
      ((PhoneCallModel)this.getPhoneCall()).updateCallerIDInfo(callerIDInfo);
   }

   public CallerIDInfo getDisplayCallerIDInfo() {
      return this._callerIDInfo;
   }

   public String getPhoneNumberTypeString() {
      CallerIDInfo cidi = (CallerIDInfo)this.getDisplayCallerIDInfo();
      return cidi.getDisplayableString(6);
   }

   public String getDisplayPhoneNumber() {
      return this.getDisplayPhoneNumber(true);
   }

   public String getDisplayPhoneNumber(boolean includeNumberType) {
      Object ticket = PersistentContent.getTicket();
      if (ticket == null) {
         return this.getFlag(2097152) ? null : this._transmittedPhoneNumber;
      }

      String displayNumber = null;
      if (this.getFlag(524288) && !PhoneUtilities.isEmptyString(this._transmittedPhoneNumber)) {
         return this._transmittedPhoneNumber;
      }

      CallerIDInfo cidi = (CallerIDInfo)this.getDisplayCallerIDInfo();
      int displayType = includeNumberType ? 5 : 4;
      String number = cidi.getDisplayableString(displayType);
      return PhoneNumberServices.convertForDisplayWithExtension(number, true);
   }

   public boolean useStandardPhoneAppMenu() {
      return true;
   }

   public boolean acceptingDTMF() {
      return this.isConnecting() || this.isActive() || this.isHeld();
   }

   protected void clearUserDialedPhoneNumber() {
      this._userDialedPhoneNumber = null;
   }

   public int getRefId() {
      return this._phoneCallModel.getRefId();
   }

   public boolean isActive() {
      return !this.isEnding() && this.getCallState() == 3;
   }

   public boolean isHeld() {
      return !this.isEnding() && this.getCallState() == 4;
   }

   public boolean isConnecting() {
      return this.getCallState() == 1;
   }

   protected boolean canHold() {
      return RadioInfo.getNetworkType() == 5 ? !this.getFlag(8192) : PhoneUtilities.canHold(this._callId);
   }

   protected boolean canSwap() {
      return PhoneUtilities.canSwap(this._callId);
   }

   public boolean wasConferenceMember() {
      return this.getFlag(8192);
   }

   public boolean isIncoming() {
      return this.getFlag(8) && !this.getFlag(2);
   }

   public int getElapsedTime() {
      return this._elapsedTime;
   }

   public int[] getCallIds() {
      return new int[]{this._callId};
   }

   public int getCallId() {
      return this._callId;
   }

   public PhoneCallModel getPhoneCall() {
      return this._phoneCallModel;
   }

   public void setEnding() {
      this.setFlag(32);
   }

   public boolean isEnding() {
      return this.getFlag(32);
   }

   public String getStatusString() {
      return doGetStatusString(this.getStatus());
   }

   public int getStatus() {
      if (this.getFlag(65536)) {
         return 4;
      }

      int state = this.getCallState();
      switch (state) {
         case -1:
            if (this.isOutgoing() && !this.getFlag(2)) {
               return 0;
            } else {
               if (this.isIncoming() && this._elapsedTime == 0) {
                  return 1;
               }

               return -1;
            }
         case 0:
            if (this.isOutgoing() && !this.getFlag(2)) {
               return 0;
            } else {
               if (!this.getFlag(16384) && !this.isIncoming()) {
                  return 4;
               }

               return 1;
            }
         case 1:
         case 2:
         default:
            return 0;
         case 3:
            if (this.isMuted()) {
               return 2;
            } else {
               if (this.isTransferring()) {
                  return 5;
               }

               return 1;
            }
         case 4:
            return 3;
      }
   }

   public void updateCallMeters() {
      throw null;
   }

   public synchronized void save() {
      if (this.getFlag(131072)) {
         this.doResaveWork();
      } else {
         if (this.logRequired()) {
            this.doSaveWork();
         }
      }
   }

   protected void doSaveWork() {
      if (this.hasFailed()) {
         this._phoneCallModel.setErrorCode(this._errorCode);
      }

      if (this._elapsedTime >= 0) {
         this._phoneCallModel.setElapsedTime(this._elapsedTime);
      }

      PhoneFolders.logPhoneCall(this._phoneCallModel);
      this.setFlag(131072);
   }

   protected void doResaveWork() {
   }

   protected boolean logRequired() {
      if (!this.getFlag(2)) {
         return this.getFlag(8) ? PhoneOptions.getOptions().getBooleanOption(4) : PhoneOptions.getOptions().getBooleanOption(2);
      }

      if (this.wasConferenceMember()) {
         return false;
      }

      switch (this._phoneCallModel.getType()) {
         case 0:
            return PhoneOptions.getOptions().getBooleanOption(8);
         case 1:
            return PhoneOptions.getOptions().getBooleanOption(16);
         case 4:
            return true;
         default:
            return false;
      }
   }

   protected void onCallConnected(Object context) {
      this.setFlag(2);
      if (PhoneUtilities.gsmTypeNetwork()) {
         if (this.getFlag(524288)) {
            this.clearFlag(524288);
            VoiceServices.broadcastEvent(2210, this._callId, null);
         }

         if (this.isOutgoing()) {
            if (this.isCallerNumberMissing()) {
               this.updateCallInfo(24, context);
               return;
            }
         } else {
            this.updateCallInfo(22, context);
         }
      }
   }

   protected void onCallResumed(Object context) {
   }

   protected void onCallHeld(Object context) {
   }

   protected void onCallDisconnected(Object context) {
      this.setFlag(4);
      if (this.getFlag(2) || this.isOutgoing()) {
         this.save();
      }

      this.stopListeningForEvents();
      this._dtmfQueue.abort();
   }

   protected void onCallFailed(Object context) {
      this.setFlag(4194304);
      this._dtmfQueue.abort();
      if (this.isOutgoing() || this.getFlag(2)) {
         if (context instanceof Object) {
            this._errorCode = context;
            if (this._errorCode != 28) {
               if (this.shouldPlayCallFailedErrorTune(this._errorCode)) {
                  PhoneUtilities.playInCallTune(1, FAIL_TUNE_VOLUME, true);
                  Backlight.enable(true);
               }

               this.save();
            }
         }

         this.stopListeningForEvents();
      }
   }

   public boolean shouldShowCallFailedDialog(int error) {
      return true;
   }

   protected boolean shouldPlayCallFailedErrorTune(int errorCode) {
      switch (errorCode) {
         case 1:
         case 4:
         case 31:
            return false;
         default:
            return true;
      }
   }

   public boolean shouldAutoToggleSpeakerphone() {
      return false;
   }

   protected void onFlashByUser(Object context) {
      this.setFlag(32768);
   }

   public boolean shouldSwitchToBackgroundWhenIdle() {
      return true;
   }

   protected boolean matchCallId(int callId) {
      return callId == this.getCallId();
   }

   public Bitmap getActiveCallBitmap() {
      return null;
   }

   public Field getCallControlField(Object context) {
      return null;
   }

   protected Field getCallerIDField(Object context) {
      CallerIDInfo model = (CallerIDInfo)this.getDisplayCallerIDInfo();
      if (!(model instanceof Object)) {
         return null;
      }

      FieldProvider fieldProvider = model;
      boolean showActual = this.getFlag(524288);
      if (showActual && this._transmittedPhoneNumber != null) {
         ContextObject.put(context, -799495460678763170L, this._transmittedPhoneNumber);
      } else if (this._userDialedPhoneNumber != null) {
         String convertedNumber = PhoneNumberServices.convertForDisplayWithExtension(this._userDialedPhoneNumber, true);
         ContextObject.put(context, -799495460678763170L, convertedNumber);
      } else {
         ContextObject.remove(context, -799495460678763170L);
      }

      if (this.getFlag(4096)) {
         PhoneUtilities.setPrivateFlag(context, 57);
         if (this._redirectedNumber != null && this._redirectedNumber.length() > 0) {
            ContextObject.put(context, 9190530831625408279L, this._redirectedNumber);
         }
      }

      return fieldProvider.getField(context);
   }

   protected Field getStatusField(Object context) {
      return new LiveCallStatusField(this, context);
   }

   protected Verb getSystemVerbs(Object context, Verb[] verbs) {
      Verb defaultVerb = null;
      if (!PhoneUtilities.gsmTypeNetwork() && !PhoneUtilities.wifiTypeNetwork()) {
         if (!PhoneUtilities.cdmaTypeNetwork()) {
            Array.resize(verbs, 0);
            return null;
         }

         defaultVerb = this.getSystemVerbsForCDMAStyleNetwork(context, verbs);
      } else {
         defaultVerb = this.getSystemVerbsForGSMStyleNetwork(context, verbs);
      }

      Verb secondaryDefaultVerb = this.getNetworkIndependentSystemVerbs(context, verbs);
      if (secondaryDefaultVerb != null) {
         defaultVerb = secondaryDefaultVerb;
      }

      this.addExternalLiveCallVerbs(context, verbs);
      return defaultVerb;
   }

   public void toggleSpeakerphone() {
      AudioPathControl control = AudioRouter.getInstance().getAudioPathControl(0);
      control.toggleSpeakerphone();
   }

   protected boolean canJoin() {
      return Phone.getInstance().canJoin(this._callId);
   }

   protected boolean canPark() {
      return Phone.getInstance().canPark(this._callId);
   }

   protected boolean canSendToVoicemail() {
      return Phone.getInstance().canSendToVoicemail(this._callId);
   }

   protected LiveCallVerb getJoinVerb() {
      Vector currCalls = VoiceServices.getVoiceApplication().getCurrentCalls();

      for (int i = currCalls.size() - 1; i >= 0; i--) {
         LiveCall call = (LiveCall)currCalls.elementAt(i);
         if (!call.canJoin()) {
            return null;
         }
      }

      return new LiveCallVerb(this, 4, 70912);
   }

   public LiveCallVerb getMuteCallVerb() {
      return new LiveCallVerb(this, 7, 71936);
   }

   public int getCallType() {
      return 0;
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      LiveCallVerb notesVerb = null;
      if (PersistentContent.getTicket() != null) {
         notesVerb = new LiveCallVerb(this, 5, 77824);
      }

      if (ContextObject.getFlag(context, 90)) {
         return this.getSystemVerbs(context, verbs);
      }

      if (!PhoneUtilities.getPrivateFlag(context, 30) && !PhoneUtilities.getPrivateFlag(context, 82) && !PhoneUtilities.getPrivateFlag(context, 41)) {
         if (PhoneUtilities.getPrivateFlag(context, 52)) {
            LiveCallVerb endCallVerb = new LiveCallVerb(this, 0, 69632);
            Array.resize(verbs, 1);
            verbs[0] = endCallVerb;
            return verbs[0];
         } else if (PhoneUtilities.getPrivateFlag(context, 31)) {
            return this.getInHolsterDefaultVerb(context, verbs);
         } else if (notesVerb != null && PhoneUtilities.getPrivateFlag(context, 27)) {
            Array.resize(verbs, 1);
            verbs[0] = notesVerb;
            return verbs[0];
         } else {
            Array.resize(verbs, 0);
            return null;
         }
      } else {
         if (this.getFlag(262144)) {
            return null;
         }

         LiveCallVerb muteVerb = this.getMuteCallVerb();
         Array.resize(verbs, 1);
         verbs[0] = muteVerb;
         return verbs[0];
      }
   }

   @Override
   public int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      if (keyRequested == 92199951187614847L) {
         keyArray[index] = this._timeStamp;
         return 1;
      } else {
         return 0;
      }
   }

   @Override
   public int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public void callConnected(int callId) {
   }

   @Override
   public Field getField(Object context) {
      if (ContextObject.getFlag(context, 27)) {
         return this.getStatusField(context);
      }

      if (ContextObject.getFlag(context, 58)) {
         return this.getCallerIDField(context);
      }

      if (PhoneUtilities.getPrivateFlag(context, 42)) {
         return this.getCallControlField(context);
      }

      RIMModel phoneCall = (RIMModel)this.getPhoneCall();
      if (!(phoneCall instanceof Object)) {
         return null;
      }

      FieldProvider fieldProvider = (FieldProvider)phoneCall;
      return fieldProvider.getField(context);
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      if (!(this._phoneCallModel instanceof Object)) {
         return true;
      }

      FieldProvider fieldProvider = (FieldProvider)this._phoneCallModel;
      return fieldProvider.grabDataFromField(field, context);
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public int getOrder(Object context) {
      return 0;
   }

   @Override
   public void callTimerUpdated(int callId, int time) {
      if (this.matchCallId(callId)) {
         this._elapsedTime = time;
         if (time >= 7 && this.getFlag(524288)) {
            this.clearFlag(524288);
            VoiceServices.broadcastEvent(2210, this._callId, null);
         }
      }
   }

   @Override
   public int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      return 0;
   }

   @Override
   public void phoneEventNotify(int eventId, int callId, Object context) {
      switch (eventId) {
         case 1001:
            if (this.matchCallId(callId)) {
               this.onCallConnected(context);
               return;
            }
            break;
         case 1002:
            if (this.matchCallId(callId) && !this.getFlag(4)) {
               this.onCallDisconnected(context);
               if (this._droppedCallListener != null) {
                  this._droppedCallListener.callDisconnected(this._callId);
                  return;
               }
            }
            break;
         case 1003:
            if (this.matchCallId(callId)) {
               this.onCallHeld(context);
               return;
            }
            break;
         case 1004:
            if (this.matchCallId(callId)) {
               this.onCallResumed(context);
               return;
            }
            break;
         case 1006:
            if (this.matchCallId(callId)) {
               this.onCallFailed(context);
               if (this._droppedCallListener != null) {
                  this._droppedCallListener.callFailed(this._callId);
                  return;
               }
            }
            break;
         case 1011:
            if (this.matchCallId(callId) && this.isOutgoing() && !this.getFlag(2)) {
               this.setFlag(1048576);
               return;
            }
            break;
         case 2200:
            if (this.matchCallId(callId)) {
               String newName = VoiceServices.getCallName(callId);
               if (newName != null && newName.length() > 0 && this._callerIDInfo != null) {
                  String oldName = this._callerIDInfo.getFriendlyName();
                  if (oldName == null) {
                     this._callerIDInfo.setFriendlyName(newName);
                  }
               }
            }
            break;
         case 3004:
         case 201010:
            if (this.matchCallId(callId) && !this.getFlag(4)) {
               this.onCallDisconnected(context);
               return;
            }
            break;
         case 3006:
            this.onFlashByUser(context);
            return;
         case 150060:
            if (this.matchCallId(callId) && context instanceof Object) {
               this.onPrivacyStateChange(context);
               return;
            }
      }
   }

   @Override
   public String toString() {
      return this.getDisplayCallerIDInfo().toString();
   }

   private Verb getInHolsterDefaultVerb(Object context, Verb[] verbs) {
      Object integer = ContextObject.get(context, -2949044237254437889L);
      if (integer instanceof Object) {
         int inHolsterEvent = integer;
         switch (inHolsterEvent) {
            case 1:
            case 4:
               break;
            case 2:
            default:
               return this.getEndCallVerb(verbs);
            case 3:
            case 5:
               if (PhoneOptions.getOptions().getBooleanOption(512) && Audio.hasBuiltInHeadset()) {
                  switch (AudioRouter.getInstance().getSink()) {
                     case 1:
                        int callId = this.getCallId();
                        if (VoiceServices.getCallState(callId) != 4) {
                           return this.getEndCallVerb(verbs);
                        }

                        Out.p("No AutoHangUp - call on HOLD");
                        return null;
                     case 2:
                     case 3:
                     default:
                        return null;
                  }
               }
         }
      }

      return null;
   }

   private void onPrivacyStateChange(boolean privacy) {
      if (privacy != this.getFlag(128)) {
         if (privacy) {
            this.setFlag(128);
         } else {
            this.clearFlag(128);
         }

         if (PhoneUtilities.platformNotifiesOnPrivacyChanges() && PhoneOptions.getOptions().getBooleanOption(1024)) {
            short[] tune = CallTunes.getTune(privacy ? 5 : 6);
            Alert.startAudio(tune, 50);
         }
      }
   }

   private Verb getEndCallVerb(Verb[] verbs) {
      LiveCallVerb endCallVerb = new LiveCallVerb(this, 0, 69632);
      Array.resize(verbs, 1);
      verbs[0] = endCallVerb;
      return endCallVerb;
   }

   private void addExternalLiveCallVerbs(Object context, Verb[] verbs) {
      VerbFactory[] verbFactories = VerbFactoryRepository.getVerbFactories(-6212315761430930923L);
      if (verbFactories != null && verbFactories.length > 0) {
         ContextObject contextObject = ContextObject.clone(context);
         contextObject.put(-6075010664073451177L, this);

         for (int i = 0; i < verbFactories.length; i++) {
            VerbFactory factory = verbFactories[i];
            Verb[] newVerbs = factory.getVerbs(contextObject);
            if (newVerbs != null && newVerbs.length > 0) {
               int count = newVerbs.length;
               int oldLength = verbs.length;
               int newLength = oldLength + count;
               Array.resize(verbs, newLength);
               System.arraycopy(newVerbs, 0, verbs, oldLength, count);
            }
         }
      }
   }

   private Verb getNetworkIndependentSystemVerbs(Object context, Verb[] verbs) {
      Verb defaultVerb = null;
      int index = verbs.length;
      LiveCallVerb muteVerb = this.getMuteCallVerb();
      LiveCallVerb notesVerb = null;
      if (PersistentContent.getTicket() != null) {
         notesVerb = new LiveCallVerb(this, 5, 77824);
      }

      if (this.canPark()) {
         Array.resize(verbs, verbs.length + 1);
         verbs[index++] = new LiveCallVerb(this, 9, 70160);
      }

      if (this.canSendToVoicemail()) {
         Array.resize(verbs, verbs.length + 1);
         verbs[index++] = new LiveCallVerb(this, 10, 70920);
      }

      if (EnhanceCallAudioServices.getInstance().isECASupported() && AudioRouter.getInstance().getSink() == 0) {
         Array.resize(verbs, verbs.length + 1);
         verbs[index++] = new EnhanceCallAudioVerb();
      }

      if (!this.getFlag(262144) && !this.isHeld()) {
         Array.resize(verbs, verbs.length + 1);
         verbs[index++] = muteVerb;
         if (this.isActive() && (this.isMuted() || !PhoneUtilities.dualMode(context) && !PhoneUtilities.hasPhoneKey())) {
            defaultVerb = muteVerb;
         }
      }

      if (notesVerb != null && !this.getFlag(2048) && !PhoneUtilities.getPrivateFlag(context, 37)) {
         Array.resize(verbs, verbs.length + 1);
         verbs[index++] = notesVerb;
      }

      Phone phone = Phone.getInstance();

      for (int action = 0; action < 5; action++) {
         if (phone.canInvokeCallTransferAction(this._callId, action)) {
            Verb verb = new TransferCallVerb(this, action);
            Array.resize(verbs, verbs.length + 1);
            verbs[index++] = verb;
            if (action == 3) {
               defaultVerb = verb;
            }
         }
      }

      VoiceApplication app = VoiceServices.getVoiceApplication();
      VerbFactory[] factories = VerbFactoryRepository.getVerbFactories(-5280468186386428176L);
      if (factories != null) {
         for (int i = factories.length - 1; i >= 0; i--) {
            AudioPathControl control = AudioRouter.getInstance().getAudioPathControl(0);
            Arrays.append(verbs, factories[i].getVerbs(control));
         }
      }

      return defaultVerb;
   }

   private boolean hasFailed() {
      return this._errorCode != 0;
   }

   private void stopListeningForEvents() {
      Out.p(((StringBuffer)(new Object("PHONE: callId "))).append(this._callId).append(" stops listening.").toString());
      this.stopListeningForPhoneEvents();
      this.stopListeningForAddressBookUpdates();
   }

   private Verb getSystemVerbsForGSMStyleNetwork(Object context, Verb[] verbs) {
      Verb defaultVerb = null;
      LiveCallVerb endCallVerb = new LiveCallVerb(this, 0, 69632);
      if (this.getFlag(65536)) {
         Array.resize(verbs, 1);
         verbs[0] = endCallVerb;
         return endCallVerb;
      }

      LiveCallVerb resumeVerb = new LiveCallVerb(this, 2, 70400);
      LiveCallVerb swapVerb = new LiveCallVerb(this, 3, 70656);
      LiveCallVerb holdVerb = new LiveCallVerb(this, 1, 70144);
      LiveCallVerb joinVerb = this.getJoinVerb();
      LiveCallVerb ectVerb = null;
      boolean dualMode = PhoneUtilities.dualMode(context);
      boolean addEndCallVerb = !PhoneUtilities.getPrivateFlag(context, 88);
      boolean canSwap = false;
      if (PhoneUtilities.canSwap(this._callId)) {
         canSwap = true;
      }

      if (dualMode) {
         if (canSwap) {
            defaultVerb = swapVerb;
         } else {
            defaultVerb = endCallVerb;
         }
      } else if (this.isConnecting() || this.isActive()) {
         defaultVerb = endCallVerb;
      } else if (this.isHeld()) {
         defaultVerb = resumeVerb;
      } else {
         defaultVerb = endCallVerb;
      }

      int verbCount = 0;
      if (!dualMode) {
         if (this.isConnecting()) {
            if (addEndCallVerb) {
               Array.resize(verbs, 1);
               verbs[verbCount++] = endCallVerb;
            }

            if (PhoneUtilities.explicitCallTransferSupported() && PhoneUtilities.heldConnectingMode(context) && this.getFlag(1048576)) {
               ectVerb = new LiveCallVerb(this, 6, 70913);
               Array.resize(verbs, verbs.length + 1);
               verbs[verbCount++] = ectVerb;
               return defaultVerb;
            }
         } else if (this.isActive() && this.canHold()) {
            Array.resize(verbs, addEndCallVerb ? 2 : 1);
            verbs[verbCount++] = holdVerb;
            if (addEndCallVerb) {
               verbs[verbCount++] = endCallVerb;
               return defaultVerb;
            }
         } else if (this.isHeld()) {
            Array.resize(verbs, addEndCallVerb ? 2 : 1);
            verbs[verbCount++] = resumeVerb;
            if (addEndCallVerb) {
               verbs[verbCount++] = endCallVerb;
               return defaultVerb;
            }
         } else if (addEndCallVerb) {
            Array.resize(verbs, 1);
            verbs[verbCount++] = endCallVerb;
         }
      } else {
         int numVerbs = 0;
         boolean canJoin = true;
         if (joinVerb != null && !PhoneUtilities.getPrivateFlag(context, 34)) {
            numVerbs++;
         } else {
            canJoin = false;
         }

         if (PhoneUtilities.explicitCallTransferSupported()) {
            ectVerb = new LiveCallVerb(this, 6, 70917);
            numVerbs++;
         }

         if (canSwap) {
            numVerbs++;
         }

         if (addEndCallVerb) {
            numVerbs++;
         }

         Array.resize(verbs, numVerbs);
         if (addEndCallVerb) {
            verbs[verbCount++] = endCallVerb;
         }

         if (canSwap) {
            verbs[verbCount++] = swapVerb;
         }

         if (canJoin) {
            verbs[verbCount++] = joinVerb;
         }

         if (ectVerb != null) {
            verbs[verbCount++] = ectVerb;
            return defaultVerb;
         }
      }

      return defaultVerb;
   }

   private Verb getSystemVerbsForCDMAStyleNetwork(Object context, Verb[] verbs) {
      Verb defaultVerb = null;
      LiveCallVerb endCallVerb = new LiveCallVerb(this, 0, 69632);
      FlashVerb flashVerb = new FlashVerb();
      int verbCount = 0;
      Array.resize(verbs, 2);
      verbs[verbCount++] = endCallVerb;
      if (!this.getFlag(256)) {
         flashVerb.setData((String)ContextObject.get(context, 7528018505720453076L));
         verbs[verbCount++] = flashVerb;
      }

      defaultVerb = endCallVerb;
      Array.resize(verbs, verbCount);
      return defaultVerb;
   }

   private int getCallState() {
      return VoiceServices.getCallState(this._callId);
   }

   private static boolean supportsCallRedirectedApi() {
      switch (RadioInfo.getNetworkType()) {
         case 3:
            if ((InternalServices.getOSAPIVersion() & -65536) >= 1048576) {
               return true;
            }

            return false;
         case 7:
            return true;
         default:
            return false;
      }
   }

   private static String doGetStatusString(int status) {
      switch (status) {
         case -1:
            return "";
         case 0:
         default:
            return PhoneResources.getString(304);
         case 1:
            return PhoneResources.getString(300);
         case 2:
            return PhoneResources.getString(6026);
         case 3:
            return PhoneResources.getString(301);
         case 4:
            return PhoneResources.getString(305);
         case 5:
            return PhoneResources.getString(6312);
      }
   }

   private boolean isCallerNumberMissing() {
      if (this._callerIDInfo == null) {
         return true;
      }

      if (this._callerIDInfo instanceof CallerIDInfo) {
         CallerIDInfo cidInfo = this._callerIDInfo;
         AbstractPhoneNumberModel number = (AbstractPhoneNumberModel)cidInfo.getNumber();
         if (number == null || number.hashCode() == 0) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return !(o instanceof LiveCall) ? false : ((LiveCall)o)._callId == this._callId;
      }
   }

   @Override
   protected void addressBookUpdated() {
      if (this._callerIDInfo instanceof AddressBookDependentObject) {
         int lastUpdateType = this.getLastAddressBookUpdateType();
         Object lastUpdateObject = this.getLastAddressBookUpdateObject();
         if (this._callerIDInfo.addressBookUpdated(lastUpdateType, lastUpdateObject)) {
            this.setIsOutOfSyncWithAddressBook(true);
            return;
         }

         this.setIsOutOfSyncWithAddressBook(false);
      }
   }

   public LiveCall(PhoneCallInitialData data, Object context) {
      super(true, context);
      this.onConstruction(data, context);
      this._timeStamp = data._timestamp;
      this._callId = data._callId;
      this._flags = data._flags;
      boolean incoming = this.getFlag(8);
      this._callerIDInfo = data._callerIDInfo;
      this._phoneCallModel = (PhoneCallModel)this.createPhoneCallModel(data);
      if (incoming) {
         this.setFlag(8);
         this._elapsedTime = 0;
         Object ticket = PersistentContent.getTicket();
         if (ticket == null) {
            CallerIDInfo cidi = (CallerIDInfo)this.getCallerIDInfo();
            if (cidi != null) {
               if (cidi.isPrivateNumber()) {
                  this._transmittedPhoneNumber = PhoneResources.getString(156);
               } else if (cidi.isUnknownNumber()) {
                  this._transmittedPhoneNumber = PhoneResources.getString(117);
               } else {
                  Object number = cidi.getNumber();
                  if (number instanceof Object) {
                     this._transmittedPhoneNumber = ((PhoneNumberModel)number).getValue();
                  }
               }
            }

            if (this._transmittedPhoneNumber == null) {
               this._transmittedPhoneNumber = PhoneResources.getString(117);
            }
         }
      } else {
         if (!this.getFlag(2)) {
            this.setFlag(524288);
         }

         this._elapsedTime = -1;
         if (!ContextObject.getFlag(data._context, 117)) {
            this._userDialedPhoneNumber = (String)ContextObject.get(data._context, -799495460678763170L);
         }

         this._transmittedPhoneNumber = (String)ContextObject.get(data._context, 6486659828352467672L);
      }

      this.startListeningForPhoneEvents();
      this._dtmfQueue = (DTMFToneQueue)this.createDTMFToneQueue();
      Application voiceApp = (Application)VoiceServices.getVoiceApplication();
      voiceApp.invokeLater(new LiveCall$1(this));
      if (PhoneUtilities.emergencyCall(data._context)) {
         this.setFlag(256);
      }

      if (PhoneUtilities.getPrivateFlag(data._context, 7)) {
         this.setFlag(2097152);
      }
   }

   private boolean isTransferring() {
      int xferState = Phone.getInstance().getCallTransferState(this._callId);
      return xferState != 1;
   }

   private void updateCallInfo(int callTypeFlag, Object context) {
      CallerIDInfo callerIDInfo = PhoneUtilities.getCallDisplayInfo(this._callId, callTypeFlag, context);
      if (callerIDInfo != null) {
         this.setCallerIDInfo(callerIDInfo);
      }
   }

   private void stopListeningForPhoneEvents() {
      VoiceServices.removePhoneEventListener(this);
      VoiceServices.removeCallTimerListener(this);
   }

   public static String getLongestStatusString() {
      return _longestStatusString;
   }

   static {
      _longestStatusString = doGetStatusString(0);
      int max = _longestStatusString.length();

      for (int status = 0; status <= 6; status++) {
         String str = doGetStatusString(status);
         int len = str.length();
         if (len > max) {
            max = len;
            _longestStatusString = str;
         }
      }

      MUTE_TUNE_VOLUME = 50;
      FAIL_TUNE_VOLUME = 50;
      switch (RadioInfo.getNetworkType()) {
         case 3:
            END_TUNE_VOLUME = 100;
            return;
         default:
            END_TUNE_VOLUME = 50;
      }
   }
}
