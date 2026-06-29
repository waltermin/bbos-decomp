package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.api.verbs.OutgoingCallConnector;
import net.rim.device.apps.internal.phone.api.verbs.VoiceMailVerb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.vm.Array;

public final class PhoneVerbManager implements VerbFactory {
   private boolean _registered = false;
   static final long GUID;
   private static PhoneVerbManager _instance;

   final void register() {
      VerbFactoryRepository.addFactory(8522643724050848398L, this);
      VerbFactoryRepository.addFactory(-689504907596508088L, this);
      VerbFactoryRepository.addFactory(6344899092283249610L, this);
      this._registered = true;
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      Verb[] verbCache = new Object[0];
      Verb defaultVerb = null;
      ContextObject newContext = ContextObject.clone(context);
      CallManager callMgr = CallManager.getInstance();
      Object currentCall = this.getActiveLiveCall();
      ConferenceCall conference = callMgr.getConferenceCall();
      RIMModel callModel = (RIMModel)currentCall;
      RIMModel callerIDInfo = null;
      if (!ContextObject.getFlag(newContext, 90)) {
         if (PhoneUtilities.callIncoming(newContext) || PhoneUtilities.callWaiting(newContext)) {
            this.setPhoneStateVerbContext(newContext);
            RIMModel var13 = ContextObject.get(newContext, 5898398779440734986L);
            if (var13 instanceof Object) {
               VerbProvider verbProvider = (VerbProvider)var13;
               defaultVerb = verbProvider.getVerbs(newContext, verbCache);
            }
         } else if (!PhoneUtilities.getPrivateFlag(newContext, 30)
            && !PhoneUtilities.getPrivateFlag(newContext, 82)
            && !PhoneUtilities.getPrivateFlag(newContext, 41)) {
            if (PhoneUtilities.getPrivateFlag(newContext, 52)) {
               if (callModel instanceof Object) {
                  VerbProvider verbProvider = (VerbProvider)callModel;
                  defaultVerb = verbProvider.getVerbs(newContext, verbCache);
               }
            } else if (PhoneUtilities.getPrivateFlag(newContext, 31)) {
               if (callModel instanceof Object) {
                  VerbProvider verbProvider = (VerbProvider)callModel;
                  defaultVerb = verbProvider.getVerbs(newContext, verbCache);
               }
            } else if (PhoneUtilities.getPrivateFlag(newContext, 27) && callModel instanceof Object) {
               VerbProvider verbProvider = (VerbProvider)callModel;
               defaultVerb = verbProvider.getVerbs(newContext, verbCache);
            }
         } else if (callModel instanceof Object) {
            VerbProvider verbProvider = (VerbProvider)callModel;
            defaultVerb = verbProvider.getVerbs(newContext, verbCache);
            this.addVerbToCache(verbCache, defaultVerb);
         }
      } else {
         if (PhoneUtilities.getPrivateFlag(newContext, 51)) {
            return verbCache;
         }

         if (callModel != null) {
            LiveCall call = (LiveCall)callModel;
            if (call.getFlag(65536)) {
               return verbCache;
            }

            this.setPhoneStateVerbContext(newContext);
            if (conference != null && conference.memberCount() == PhoneUtilities.getMaxConferenceCallMembers()) {
               PhoneUtilities.setPrivateFlag(newContext, 34);
            }

            if (callModel instanceof Object) {
               VerbProvider verbProvider = (VerbProvider)callModel;
               defaultVerb = verbProvider.getVerbs(newContext, verbCache);
            }

            if (!PhoneUtilities.getPrivateFlag(newContext, 43)) {
               this.addVerbToCache(verbCache, (Verb)(new Object()));
            }

            UiApplication voiceApp = VoiceServices.getUiApplication();
            if (!voiceApp.isForeground() && Keypad.hasSendEndKeys()) {
               Verb showPhoneVerb = new PhoneVerbManager$1(this, 77840, PhoneResources.getResourceBundle(), 6285);
               this.addVerbToCache(verbCache, showPhoneVerb);
            }
         }

         if (!PhoneUtilities.getPrivateFlag(context, 69) && OutgoingCallConnector.outgoingCallPermitted() && VoicemailIconManager.getInstance().isIndicatorOn()
            )
          {
            VoiceMailVerb vmailVerb = (VoiceMailVerb)(new Object());
            this.addVerbToCache(verbCache, vmailVerb);
            if (ContextObject.getFlag(context, 92) && ContextObject.getFlag(context, 37)) {
               vmailVerb.setPopScreenOnInvoke(true);
            }

            if (ContextObject.getFlag(context, 91)) {
               defaultVerb = vmailVerb;
            }
         }
      }

      if ((ContextObject.getFlag(context, 20) || ContextObject.getFlag(context, 91)) && defaultVerb != null) {
         ((LongHashtable)context).put(-3185095355580406181L, defaultVerb);
      }

      return verbCache;
   }

   final boolean isRegistered() {
      return this._registered;
   }

   public PhoneVerbManager(Object context) {
   }

   public static final void initialize() {
      PhoneVerbManager verbMgr = getInstance();
      if (verbMgr != null && !verbMgr.isRegistered()) {
         verbMgr.register();
      }
   }

   private final void setPhoneStateVerbContext(Object context) {
      byte state = VoiceServices.getPhoneState();
      switch (state) {
         case 2:
         default:
            ContextObject.setPrivateFlag(context, 4936088360624690805L, 18);
            return;
         case 3:
            ContextObject.setPrivateFlag(context, 4936088360624690805L, 19);
            return;
         case 5:
            ContextObject.setPrivateFlag(context, 4936088360624690805L, 20);
            return;
         case 7:
            ContextObject.setPrivateFlag(context, 4936088360624690805L, 17);
         case 1:
         case 4:
         case 6:
            return;
         case 8:
            ContextObject.setPrivateFlag(context, 4936088360624690805L, 21);
      }
   }

   private final LiveCall getActiveLiveCall() {
      LiveCall call = RIMPhone.getInstance().getIncomingCall();
      if (call == null) {
         CallManager callMgr = CallManager.getInstance();
         call = (LiveCall)callMgr.getCurrentCall();
      }

      return call;
   }

   static final synchronized PhoneVerbManager getInstance() {
      if (_instance == null) {
         ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
         _instance = (PhoneVerbManager)reg.get(-5742267290719053151L);
         if (_instance == null) {
            _instance = new PhoneVerbManager(null);
            synchronized (reg) {
               reg.put(-5742267290719053151L, _instance);
            }
         }
      }

      return _instance;
   }

   private final void addVerbToCache(Verb[] verbCache, Verb verb) {
      int len = verbCache.length;
      Array.resize(verbCache, len + 1);
      verbCache[len] = verb;
   }
}
