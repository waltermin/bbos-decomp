package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.ui.Screen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceApplication;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.UnhandledGlobalKeyListenerImpl;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.resource.PhoneContexts;

final class RIMPhone$PhoneUnhandledGlobalKeyListenerImpl extends UnhandledGlobalKeyListenerImpl {
   private final boolean _canRequestForeground;
   private final boolean _canInvokePhoneButtonVerb;
   private final RIMPhone this$0;

   public RIMPhone$PhoneUnhandledGlobalKeyListenerImpl(RIMPhone _1) {
      super(_1._app);
      this.this$0 = _1;
      this._canRequestForeground = !DirectConnect.isSupported();
      this._canInvokePhoneButtonVerb = !DirectConnect.isSupported();
   }

   @Override
   protected final void handleApplicationKeyPress() {
      boolean isForeground = this.this$0.isForegroundPending() || this.this$0._app.isForeground();
      boolean isSystemLocked = ApplicationManager.getApplicationManager().isSystemLocked();
      boolean isInHolster = DeviceInfo.isInHolster();
      if (!isForeground && !isSystemLocked && !isInHolster) {
         if (this._canRequestForeground) {
            this.this$0.requestVoiceApplicationForeground();
         }
      } else if (this._canInvokePhoneButtonVerb) {
         Verb verbToInvoke = this.getPhoneButtonVerb(41);
         if (verbToInvoke != null) {
            this.this$0._app.invokeLater(new VerbRunner(verbToInvoke));
            return;
         }

         if (!isInHolster) {
            Backlight.enable(true);
            return;
         }
      }
   }

   @Override
   protected final void handleApplicationLongKeyPress() {
   }

   @Override
   protected final void handleSendKeyPress() {
      if (!ApplicationManager.getApplicationManager().isSystemLocked() && this.this$0._incomingCallScreen != null) {
         this.this$0._incomingCallScreen.sendKeyPressed();
      }

      if (!this.this$0._app.isForeground() && !ApplicationManager.getApplicationManager().isSystemLocked()) {
         this.this$0.requestVoiceApplicationForeground(new Object(119));
      }
   }

   @Override
   protected final void handleSendKeyPressedAndHeld() {
      if (!this.this$0._app.isForeground() && !ApplicationManager.getApplicationManager().isSystemLocked()) {
         ContextObject context = (ContextObject)(new Object(119));
         PhoneUtilities.setPrivateFlag(context, 79);
         this.this$0.requestVoiceApplicationForeground(context);
      }
   }

   @Override
   protected final void handleEndKeyPress() {
      if (!ApplicationManager.getApplicationManager().isSystemLocked() && this.this$0._incomingCallScreen != null) {
         this.this$0._incomingCallScreen.endKeyPressed();
      } else {
         LiveCall currentCall = (LiveCall)CallManager.getInstance().getCurrentCall();
         if (currentCall == null) {
            currentCall = this.this$0._incomingCall;
         }

         if (currentCall != null) {
            if (PhoneUtilities.idenTypeNetwork()) {
               Runnable endCallRunner = new RIMPhone$PhoneUnhandledGlobalKeyListenerImpl$1EndCallRunnable(this, currentCall);
               Application.getApplication().invokeLater(endCallRunner, 250, false);
            } else {
               this.endCallByUser(currentCall);
            }
         } else {
            RibbonLauncher rl = RibbonLauncher.getInstance();
            if (rl != null) {
               rl.showRootFolder();
            }

            Screen activeScreen = this.this$0._app.getActiveScreen();
            if (activeScreen instanceof Object) {
               this.this$0._app.popScreen(activeScreen);
            }
         }
      }
   }

   private final void endCallByUser(LiveCall currentCall) {
      currentCall.endByUser();
      if (!this.this$0._app.isForeground()) {
         ContextObject context = (ContextObject)(new Object());
         PhoneUtilities.setPrivateFlag(context, 71);
         ((VoiceApplication)this.this$0._app).requestForeground(null, context);
      }
   }

   @Override
   protected final void handleToggleSpeakerPhone() {
   }

   @Override
   protected final void handleToggleMute() {
      Verb verbToInvoke = this.getPhoneButtonVerb(82);
      if (verbToInvoke != null) {
         this.this$0._app.invokeLater(new VerbRunner(verbToInvoke));
      }
   }

   private final Verb getPhoneButtonVerb(int flag) {
      VerbFactory factory = this.this$0.getIncomingCallVerbFactory();
      Verb[] verbs = null;
      Object context = PhoneContexts.GET_VERBS_CONTEXT_WR.getContextObject();
      ((ContextObject)context).reset();
      PhoneUtilities.setPrivateFlag(context, flag);
      if (factory != null) {
         verbs = factory.getVerbs(context);
      }

      return verbs != null && verbs.length > 0 ? verbs[0] : null;
   }
}
