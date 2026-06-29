package net.rim.device.apps.internal.phone;

import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.Out;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;
import net.rim.device.apps.internal.phone.api.PhoneLogger;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.api.livecall.LiveCallFactoryRegistry;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.model.PhoneNumberConverter;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class CallManager implements PhoneEventListener {
   private Vector _currentCallsCache = (Vector)(new Object(2));
   private Object[] _listeners;
   private LiveCall _currentCall;
   private Application _app;
   private Object _callCacheLock = new Object();
   private boolean _handledAnswerDropCurrent;
   private int _lastFailedCallId = 0;
   private int _lastDisconnectedCallId = 0;
   private static final long GUID;
   public static final long CURRENT_CALLS_REGISTRY_KEY;
   private static final int MAX_CONNECTED_CALLS;
   private static CallManager _instance;
   private static final int CALL_FAILURE_TIMER_DELAY;
   private static final int SPECIAL_CALL_FAILURE_TIMER_DELAY;
   private static final int CALL_SUCCESS_TIMER_DELAY;
   private static final int ENDED_BY_USER_TIMER_DELAY;
   private static final int CONFERENCE_CALL_INDEX;
   private static final int NON_CONFERENCE_CALL_INDEX;

   public CallManager() {
      this._currentCall = null;
   }

   public static final void initialize(Object context) {
      CallManager cm = getInstance();
      if (cm != null) {
         cm._listeners = null;
         cm.register(context);
      }
   }

   final void register(Object context) {
      VoiceServices.addPhoneEventListener(this);
      this._app = (Application)ContextObject.get(context, -5475650791114535957L);
      int numCalls = this._currentCallsCache.size();
      if (numCalls > 0) {
         for (int i = 0; i < numCalls; i++) {
            LiveCall call = (LiveCall)this._currentCallsCache.elementAt(i);
            call.startListeningForPhoneEvents();
         }
      }
   }

   public static final CallManager getInstance() {
      if (_instance == null) {
         ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
         synchronized (reg) {
            _instance = (CallManager)reg.get(-3601742633092761704L);
            if (_instance == null) {
               _instance = new CallManager();
               reg.put(-3601742633092761704L, _instance);
            }
         }
      }

      return _instance;
   }

   public final void addListener(CallManager$Listener listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   private final void notifyListeners(int eventId) {
      this.notifyListeners(eventId, null, 0);
   }

   private final void notifyListeners(int eventId, LiveCall affectedCall, int flags) {
      this.notifyListeners(eventId, affectedCall, flags, null);
   }

   private final void notifyListeners(int eventId, LiveCall affectedCall, int flags, Object context) {
      if (this._listeners != null) {
         int size = this._listeners.length;
         Vector calls = this.getCallCacheCopy();

         for (int i = 0; i < size; i++) {
            CallManager$Listener listener = (CallManager$Listener)this._listeners[i];
            listener.onCallManagerEvent(eventId, calls, affectedCall, flags, context);
         }
      }
   }

   private final Vector getCallCacheCopy() {
      Vector copy = (Vector)(new Object(2));
      synchronized (this.getCallCacheLock()) {
         int size = this.getNumCalls();

         for (int i = 0; i < size; i++) {
            Object call = this._currentCallsCache.elementAt(i);
            if (call != null) {
               copy.addElement(call);
            }
         }

         return copy;
      }
   }

   final int getNumCalls() {
      synchronized (this.getCallCacheLock()) {
         return this._currentCallsCache.size();
      }
   }

   private final void addCall(int callId, LiveCall call, Object context) {
      this.addCall(callId, call, context, false);
   }

   private final void addCall(int callId, LiveCall call, Object context, boolean answerDropCurrent) {
      if (call != null) {
         synchronized (this.getCallCacheLock()) {
            int originalCount = this._currentCallsCache.size();
            if (originalCount == 0 && !answerDropCurrent) {
               int volume = PhoneOptions.getOptions().getDefaultCallVolume();
               AudioRouter router = AudioRouter.getInstance();
               if (volume == 0) {
                  router.updateMasterVolume();
               } else {
                  router.setMasterVolume(volume, false, 0);
               }
            }

            for (int idx = this.getNumCalls() - 1; idx >= 0; idx--) {
               LiveCall oldCall = (LiveCall)this._currentCallsCache.elementAt(idx);
               if (oldCall.getCallId() == callId) {
                  this._currentCallsCache.removeElementAt(idx);
               }
            }

            this._currentCallsCache.insertElementAt(call, this._currentCallsCache.size());
            ContextObject co = ContextObject.castOrCreate(context);
            Object cidi = call.getCallerIDInfo();
            if (cidi != null) {
               co.put(5898398779440734986L, cidi);
            }

            VoiceServices.broadcastEvent(2000, callId, co);
         }
      }
   }

   private final void removeCall(LiveCall call) {
      synchronized (this.getCallCacheLock()) {
         this._currentCallsCache.removeElement(call);
      }
   }

   final LiveCall findCallByCallId(int callId) {
      synchronized (this.getCallCacheLock()) {
         for (int i = this.getNumCalls() - 1; i >= 0; i--) {
            LiveCall call = (LiveCall)this._currentCallsCache.elementAt(i);
            if (call.getCallId() == callId) {
               return call;
            }
         }

         return null;
      }
   }

   private final ConferenceCall findConferenceCall() {
      int size = this.getNumCalls();
      if (size >= 1) {
         LiveCall call = (LiveCall)this._currentCallsCache.elementAt(0);
         if (call instanceof ConferenceCall) {
            return (ConferenceCall)call;
         }
      }

      return null;
   }

   private final LiveCall findCurrentCall() {
      LiveCall currentCall = null;
      ConferenceCall conferenceCall = this.findConferenceCall();
      int size = this.getNumCalls();

      try {
         if (conferenceCall != null) {
            if (size == 1) {
               currentCall = conferenceCall;
            } else {
               LiveCall nonConferencedCall = (LiveCall)this._currentCallsCache.elementAt(1);
               if (nonConferencedCall != null) {
                  if (nonConferencedCall.isActive()) {
                     currentCall = nonConferencedCall;
                  } else {
                     currentCall = conferenceCall;
                  }
               }
            }
         } else {
            LiveCall activeCall = null;
            LiveCall heldCall = null;

            for (int i = size - 1; i >= 0; i--) {
               LiveCall call = (LiveCall)this._currentCallsCache.elementAt(i);
               if (this.isActiveCall(call)) {
                  activeCall = call;
               } else if (this.isHeldCall(call)) {
                  heldCall = call;
               }
            }

            currentCall = activeCall != null ? activeCall : heldCall;
         }

         return (LiveCall)(currentCall == null && size > 0 ? this._currentCallsCache.elementAt(0) : currentCall);
      } finally {
         ;
      }
   }

   public final Object getCurrentCall() {
      if (this._currentCall != null && !this._currentCall.getFlag(65536)) {
         if (this.getNumCalls() == 0) {
            this._currentCall = null;
         }

         return this._currentCall;
      } else {
         this._currentCall = this.findCurrentCall();
         return this._currentCall != null ? this._currentCall : null;
      }
   }

   public final Object getIncomingCall() {
      return RIMPhone.getInstance().getIncomingCall();
   }

   public final Object getInactiveCall() {
      Object currentCall = this.getCurrentCall();
      int numCalls = this.getNumCalls();
      if (numCalls <= 1) {
         return null;
      }

      ConferenceCall conferenceCall = this.getConferenceCall();
      if (conferenceCall != null && conferenceCall != currentCall && !conferenceCall.getFlag(4)) {
         return conferenceCall;
      }

      for (int idx = 0; idx < numCalls; idx++) {
         LiveCall call = (LiveCall)this._currentCallsCache.elementAt(idx);
         if (call != currentCall && !call.getFlag(4)) {
            return call;
         }
      }

      return null;
   }

   public final ConferenceCall getConferenceCall() {
      return this.findConferenceCall();
   }

   private final boolean isActiveCall(LiveCall call) {
      return !call.getFlag(65536) && (call.isActive() || call.isConnecting() || call.isIncoming());
   }

   private final boolean isHeldCall(LiveCall call) {
      return call.isHeld();
   }

   private final void removeStaleCalls() {
      for (int i = this._currentCallsCache.size() - 1; i >= 0; i--) {
         LiveCall call = (LiveCall)this._currentCallsCache.elementAt(i);
         if (call != null && call.getFlag(4)) {
            if (call instanceof ConferenceCall) {
               VoiceServices.removePhoneEventListener(call);
               VoiceServices.removeCallTimerListener(call);
            }

            this.removeCall(call);
         }
      }
   }

   private final void handleCallConnected(int callId) {
      int origCallCount = this.getNumCalls();
      LiveCall incomingCall = (LiveCall)this.getIncomingCall();
      if (incomingCall != null && incomingCall.getCallId() == callId) {
         boolean answerDropCurrent = this._handledAnswerDropCurrent;
         if (this._handledAnswerDropCurrent) {
            this._handledAnswerDropCurrent = false;
            this.removeStaleCalls();
         }

         this.addCall(callId, incomingCall, null, answerDropCurrent);
         this._currentCall = incomingCall;
         RIMPhone.getInstance().clearIncomingCall();
         if (origCallCount == 0 && !answerDropCurrent) {
            this.notifyListeners(20);
         } else {
            this.notifyListeners(30);
         }
      } else {
         this._currentCall = this.findCallByCallId(callId);
         if (this._currentCall != null && this._currentCall.isOutgoing()) {
            String connectedNumber = null;
            boolean debugRedirectedNumber = PhoneUtilities.getDebugFlag(705071609734938371L);
            if (debugRedirectedNumber) {
               connectedNumber = "1234567890";
            }

            if (connectedNumber == null) {
               connectedNumber = VoiceServices.getCallPhoneNumber(callId, false);
            }

            if (connectedNumber != null && connectedNumber.length() > 0 && (this._currentCall.isRedirected(connectedNumber) || debugRedirectedNumber)) {
               Out.p(((StringBuffer)(new Object("PHONE-redirection("))).append(connectedNumber).append(')').toString());
               this._currentCall.setRedirected(connectedNumber);
               ContextObject contextObject = (ContextObject)(new Object());
               PhoneUtilities.setPrivateFlag(contextObject, 57);
               VoiceServices.broadcastEvent(2220, callId, contextObject);
            }
         }
      }
   }

   private final void handleCallDisconnected(int callId) {
      switch (RadioInfo.getNetworkType()) {
         case 3:
         case 7:
            if (callId == this._lastDisconnectedCallId && callId != 32769) {
               this._lastDisconnectedCallId = 0;
               return;
            } else {
               this._lastDisconnectedCallId = callId;
            }
         default:
            LiveCall disconnectedCall = this.findCallByCallId(callId);
            if (disconnectedCall == null && this.getConferenceCall() != null) {
               this.handleCallRemovedFromConference(callId);
               disconnectedCall = this.findCallByCallId(callId);
            }

            if (disconnectedCall == null) {
               PhoneLogger.log("calldisc-no-call");
               if (this.getNumCalls() == 0) {
                  this.notifyListeners(10);
               }

               this._lastDisconnectedCallId = 0;
            } else {
               boolean endedByUser = disconnectedCall.getFlag(65536);
               this.handleCallDisconnected(disconnectedCall, endedByUser);
            }
      }
   }

   private final void handleCallDisconnected(LiveCall call, boolean endedByUser) {
      if (call != null) {
         call.updateCallMeters();
         boolean answerDropCurrent = call.getFlag(16384);
         boolean droppedOutOfConferenceCall = call.wasConferenceMember() && call != this.getCurrentCall();
         if (endedByUser) {
            this.removeCallAndNotify(call, 16);
            return;
         }

         if (!endedByUser && !answerDropCurrent) {
            PhoneLogger.log("CM no endbyuser/ansdropcurr");
            if (droppedOutOfConferenceCall) {
               this.removeCallAndNotify(call, 32);
               return;
            }

            this.removeCallAndNotify(call, 16);
            return;
         }

         if (answerDropCurrent) {
            this._handledAnswerDropCurrent = true;
            this.removeCall(call);
            this.notifyListeners(30);
            return;
         }

         this.removeCallAndNotify(call, 0);
      }
   }

   private final void removeCallAndNotify(LiveCall call, int flags) {
      int origCallCount = this.getNumCalls();
      this.removeCall(call);
      if (origCallCount == 1) {
         this.notifyListeners(10, call, flags);
      } else {
         this.notifyListeners(30, call, flags);
      }

      this.currentCallSanityCheck(call.getCallId());
   }

   private static final boolean isSpecialFailureReason(int reason) {
      switch (reason) {
         case 1:
         case 2:
         case 8:
         case 30:
         case 31:
            return true;
         default:
            return false;
      }
   }

   private static final boolean isImmediateFailureReason(int reason) {
      switch (reason) {
         case 28:
            return true;
         default:
            return false;
      }
   }

   private final void handleCallManipulateFailed(int reason) {
      Dialog.inform(PhoneResources.getString(6055));
   }

   private final void handleCallFailed(int callId, int reason, Object context) {
      switch (RadioInfo.getNetworkType()) {
         case 3:
         case 7:
            if (callId == this._lastFailedCallId) {
               return;
            }

            this._lastFailedCallId = callId;
      }

      LiveCall failedCall = this.findCallByCallId(callId);
      if (failedCall == null && this.getConferenceCall() != null) {
         this.handleCallRemovedFromConference(callId);
         failedCall = this.findCallByCallId(callId);
      }

      if (failedCall == null) {
         PhoneLogger.log("callfail-no-call");
         if (this.getNumCalls() == 0) {
            this.notifyListeners(10);
         }
      } else {
         CallManager$DelayedCallRemover remover = new CallManager$DelayedCallRemover(this, callId, reason, 1200);
         String message = PhoneUtilities.getCallFailureErrorString(reason);
         PhoneOptions.getOptions().logPhoneError("PH", reason, message);
         if ((failedCall.isOutgoing() || failedCall.getFlag(2)) && !isImmediateFailureReason(reason) && failedCall.shouldShowCallFailedDialog(reason)) {
            if (isSpecialFailureReason(reason)) {
               remover.alert(message, callId, true);
               remover.removeLater(this._app, 10000);
            } else {
               remover.alert(message, callId, false);
               remover.removeLater(this._app, 6000);
            }
         } else {
            ContextObject contextObject = ContextObject.castOrCreate(context);
            PhoneUtilities.setPrivateFlag(contextObject, 53);
            remover.setContext(contextObject);
            remover.removeNow();
         }
      }
   }

   private final void handleCallResumed(int callId, Object context) {
      ConferenceCall conference = this.getConferenceCall();
      LiveCall call = null;
      if (conference != null && conference.isMemberId(callId)) {
         this._currentCall = conference;
      } else {
         call = this.findCallByCallId(callId);
         if (call != null) {
            this._currentCall = call;
         }
      }

      if (this._currentCall != null) {
         ContextObject contextObject = ContextObject.castOrCreate(context);
         contextObject.put(-6075010664073451177L, this._currentCall);
      }
   }

   private final LiveCall createCall(int callId, boolean incoming, int flags, CallerIDInfo callerIDInfo, Object context) {
      byte type = (byte)(incoming ? 0 : 1);
      PhoneCallInitialData data = (PhoneCallInitialData)(new Object(callId, type, flags, callerIDInfo, context));
      return LiveCallFactoryRegistry.getRegistry().createLiveCall(data, context);
   }

   private final void handleCallInitiated(int callId, Object context) {
      LiveCall call = null;
      String calledNumber = null;
      if (this.findCallByCallId(callId) == null) {
         if (!(context instanceof Object)) {
            call = (LiveCall)ContextObject.get(context, -6075010664073451177L);
            if (call == null) {
               CallerIDInfo callerIDInfo = null;
               ContextObject callCreationContext = ContextObject.castOrCreate(context);
               Object var12;
               if (context instanceof Object) {
                  callerIDInfo = PhoneUtilities.createCallerIDInfo(context);
                  var12 = context;
               } else if (context instanceof Object) {
                  callerIDInfo = (CallerIDInfo)ContextObject.get(context, 5898398779440734986L);
                  var12 = ContextObject.get(context, 247);
               } else {
                  var12 = VoiceServices.getCallPhoneNumber(callId);
                  if (var12 != null) {
                     callerIDInfo = PhoneUtilities.createCallerIDInfo(var12);
                  } else {
                     callerIDInfo = PhoneUtilities.getCallDisplayInfo(callId, 24, context);
                  }
               }

               if (callerIDInfo != null) {
                  if (var12 != null && ((String)var12).length() > 0) {
                     PhoneNumberConverter.convertForTransmission((StringBuffer)(new Object()), ((String)var12).toCharArray(), callCreationContext);
                  }

                  String friendlyName = callerIDInfo.getFriendlyName();
                  if (friendlyName == null || friendlyName.length() == 0) {
                     label78:
                     try {
                        friendlyName = Phone.getInstance().getCallName(callId);
                        if (friendlyName != null) {
                           callerIDInfo.setFriendlyName(friendlyName);
                        }
                     } finally {
                        break label78;
                     }
                  }

                  call = this.createCall(callId, false, 0, callerIDInfo, callCreationContext);
               }
            }
         } else {
            call = (LiveCall)context;
         }

         if (call != null) {
            int origCount = this.getNumCalls();
            this.addCall(callId, call, context);
            this._currentCall = call;
            int event = origCount == 0 ? 20 : 30;
            this.notifyListeners(20, null, 0, context);
         }
      }
   }

   private final void handleAnswerCallAborted(int callId, Object context) {
      this.removeStaleCalls();
   }

   private final void handleCallAddedToConference(int callId) {
      ConferenceCall conferenceCall = this.findConferenceCall();
      if (conferenceCall == null) {
         conferenceCall = new ConferenceCall(this._currentCallsCache);
         this._currentCallsCache.removeAllElements();
         this._currentCallsCache.insertElementAt(conferenceCall, 0);
         this._currentCall = conferenceCall;
         VoiceServices.broadcastEvent(3001, 0, null);
      } else {
         if (this._currentCallsCache.size() > 1) {
            LiveCall addedCall = (LiveCall)this._currentCallsCache.elementAt(1);
            if (addedCall != null) {
               conferenceCall.addMember(addedCall);
               this._currentCallsCache.removeElementAt(1);
            }
         }

         this._currentCall = conferenceCall;
      }

      this.notifyListeners(30);
   }

   private final void handleConferenceCallDisconnected() {
      ConferenceCall conferenceCall = this.findConferenceCall();
      if (conferenceCall != null) {
         if (conferenceCall.getFlag(16384)) {
            this._handledAnswerDropCurrent = true;
         } else {
            int origCount = this.getNumCalls();
            this._currentCallsCache.removeElementAt(0);
            if (origCount == 1) {
               this.notifyListeners(10, conferenceCall, 0);
            } else {
               this.notifyListeners(30, conferenceCall, 0);
            }
         }

         if (this._currentCall == conferenceCall) {
            this._currentCall = this.findCurrentCall();
         }

         conferenceCall.disconnect();
         conferenceCall.updateCallMeters();
      }
   }

   private final void handleCallRemovedFromConference(int callId) {
      ConferenceCall conferenceCall = this.findConferenceCall();
      if (conferenceCall != null) {
         if (!conferenceCall.isMemberId(callId)) {
            return;
         }

         if (conferenceCall.memberCount() == 2) {
            if (!conferenceCall.getFlag(16384)) {
               this._currentCallsCache.removeElementAt(0);
               conferenceCall.transferMembers(this._currentCallsCache);
               conferenceCall.disconnect();
               if (this._currentCall == conferenceCall) {
                  this._currentCall = null;
               }

               this.notifyListeners(30);
               return;
            }
         } else {
            LiveCall removedCall = conferenceCall.removeMember(callId);
            if (removedCall != null) {
               boolean answerDropCurrent = removedCall.getFlag(16384);
               if (!answerDropCurrent) {
                  this._currentCallsCache.addElement(removedCall);
                  this.notifyListeners(30);
               }
            }
         }
      }
   }

   private final void handleOTAStatusChange(int callId, int status) {
      Application.getApplication().invokeLater(new CallManager$1(this, status));
   }

   private final void handleCallStarted(int callId, Object context) {
      LiveCall newCall = null;
      if (!(context instanceof Object)) {
         if (context instanceof Object) {
            newCall = (LiveCall)ContextObject.get(context, -6075010664073451177L);
         }
      } else {
         newCall = (LiveCall)context;
      }

      if (newCall != null) {
         int origCallCount = this.getNumCalls();
         this.addCall(callId, newCall, context);
         this._currentCall = newCall;
         if (origCallCount == 0) {
            this.notifyListeners(20);
            return;
         }

         this.notifyListeners(30);
      }
   }

   public final synchronized Object getCallCacheLock() {
      return this._callCacheLock;
   }

   public final Vector getCurrentCalls() {
      return this._currentCallsCache;
   }

   @Override
   public final void phoneEventNotify(int eventId, int callId, Object context) {
      String logString = null;
      switch (eventId) {
         case 1000:
            System.out.println(((StringBuffer)(new Object("EV_CALL_INCOMING("))).append(callId).append(')').toString());
            this.phonePulseOn();
            return;
         case 1001:
            logString = ((StringBuffer)(new Object("EV_CALL_CONNECTED("))).append(callId).append(")").toString();
            System.out.println(logString);
            PhoneLogger.log(logString);
            this.handleCallConnected(callId);
            return;
         case 1002:
            logString = ((StringBuffer)(new Object("EV_CALL_DISCONNECTED("))).append(callId).append(")").toString();
            System.out.println(logString);
            PhoneLogger.log(logString);
            this.handleCallDisconnected(callId);
            return;
         case 1003:
            System.out.println(((StringBuffer)(new Object("EV_CALL_HELD("))).append(callId).append(')').toString());
            return;
         case 1004:
            System.out.println(((StringBuffer)(new Object("EV_CALL_RESUMED("))).append(callId).append(')').toString());
            this.handleCallResumed(callId, context);
            return;
         case 1005:
            System.out.println(((StringBuffer)(new Object("EV_CALL_WAITING("))).append(callId).append(')').toString());
            return;
         case 1006:
            if (context instanceof Object) {
               int reason = context;
               logString = ((StringBuffer)(new Object("EV_CALL_FAILED("))).append(callId).append(",").append(reason).append(")").toString();
               System.out.println(logString);
               PhoneLogger.log(logString);
               this.handleCallFailed(callId, reason, context);
               return;
            }
            break;
         case 1007:
            System.out.println(((StringBuffer)(new Object("EV_CALL_ADDED("))).append(callId).append(')').toString());
            this.handleCallAddedToConference(callId);
            return;
         case 1008:
            System.out.println(((StringBuffer)(new Object("EV_CALL_REMOVED("))).append(callId).append(')').toString());
            this.handleCallRemovedFromConference(callId);
            return;
         case 1009:
            if (context instanceof Object) {
               int manipulateFailedReason = context;
               this.handleCallManipulateFailed(manipulateFailedReason);
               System.out
                  .println(
                     ((StringBuffer)(new Object("EV_CALL_MANIPULATE_FAILED(")))
                        .append(callId)
                        .append(',')
                        .append(manipulateFailedReason)
                        .append(')')
                        .toString()
                  );
               return;
            }
            break;
         case 1100:
            System.out.println(((StringBuffer)(new Object("EV_CALL_INITIATED("))).append(callId).append(')').toString());
            int theCallId = callId;
            Object theContext = context;
            synchronized (this._app.getAppEventLock()) {
               this.handleCallInitiated(theCallId, theContext);
            }

            this.phonePulseOn();
            return;
         case 1105:
            this.handleCallStarted(callId, context);
            return;
         case 1110:
            System.out.println(((StringBuffer)(new Object("EV_CALL_ANSWERED("))).append(callId).append(')').toString());
            return;
         case 1140:
            this.handleAnswerCallAborted(callId, context);
            return;
         case 3003:
            this.handleConferenceCallDisconnected();
            return;
         case 3004:
            this.handleCallDisconnected(callId);
            return;
         case 3006:
            System.out.println("EV_CALL_FLASH_BY_USER");
            return;
         case 150060:
            if (context instanceof Object) {
               boolean state = context;
               System.out.println(((StringBuffer)(new Object("EV_PRIVACY_STATE_CHANGE("))).append(callId).append(", ").append(state).append(')').toString());
               return;
            }
            break;
         case 150070:
            if (context instanceof Object) {
               int status = context;
               System.out.println(((StringBuffer)(new Object("EV_OTA_STATUS_CHANGE("))).append(callId).append(", ").append(status).append(')').toString());
               this.handleOTAStatusChange(callId, status);
               return;
            }
            break;
         case 180000:
            synchronized (this.getCallCacheLock()) {
               for (int i = this.getNumCalls() - 1; i >= 0; i--) {
                  LiveCall call = (LiveCall)this._currentCallsCache.elementAt(i);
                  VoiceServices.broadcastEvent(1002, call.getCallId(), null);
               }
            }

            this._lastFailedCallId = 0;
            this._lastDisconnectedCallId = 0;
            break;
         case 201000:
            this.phonePulseOn();
            return;
         case 201010:
            this.handleCallDisconnected(callId);
            return;
      }
   }

   private final void currentCallSanityCheck(int terminatedCallId) {
      if (this._currentCall == null || this._currentCall.getCallId() == terminatedCallId) {
         this._currentCall = this.findCurrentCall();
      }
   }

   private final void phonePulseOn() {
      PhonePulse phonePulse = PhonePulse.getInstance();
      if (phonePulse == null) {
         PhonePulse.begin();
      }
   }
}
