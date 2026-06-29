package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.AudioHeadsetListener;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneContexts;

final class RIMPhone$HookswitchHandler implements AudioHeadsetListener, Runnable {
   private Application _app;
   private int _timeout;
   private int _timerID;
   private int _clickTime;
   private final RIMPhone this$0;
   private static final int DEFAULT_TIMEOUT;
   private static final int INVALID_TIMER_ID;

   protected RIMPhone$HookswitchHandler(RIMPhone _1, Application application) {
      this(_1, application, 500);
   }

   protected RIMPhone$HookswitchHandler(RIMPhone _1, Application application, int timeoutMillis) {
      this.this$0 = _1;
      this._timerID = -1;
      this._app = application;
      this._timeout = timeoutMillis;
   }

   private final void handleClick() {
      this.perform(this.getHeadsetButtonVerb(30));
   }

   private final void handleLongClick() {
      this.perform(this.getHeadsetButtonVerb(52));
      VoiceServices.broadcastEvent(100402);
   }

   private final void perform(Verb verbToInvoke) {
      int state = VoiceServices.getPhoneState();
      boolean isActive = state != 0 && state != 1;
      if (verbToInvoke != null && isActive || verbToInvoke != null && this.this$0._incomingCallScreen != null) {
         this._app.invokeLater(new VerbRunner(verbToInvoke));
      }
   }

   private final Verb getHeadsetButtonVerb(int flag) {
      VerbFactory factory = this.this$0.getIncomingCallVerbFactory();
      Verb[] verbs = null;
      Object context = null;
      Verb retVerb = null;
      if (this.this$0._incomingCallContext != null) {
         context = this.this$0._incomingCallContext;
      } else {
         context = PhoneContexts.GET_VERBS_CONTEXT_WR.getContextObject();
         ((ContextObject)context).reset();
      }

      boolean oldFlagValue = PhoneUtilities.getPrivateFlag(context, flag);
      PhoneUtilities.setPrivateFlag(context, flag);
      if (factory != null) {
         verbs = factory.getVerbs(context);
      }

      if (verbs != null && verbs.length > 0) {
         retVerb = verbs[0];
      }

      if (!oldFlagValue) {
         PhoneUtilities.clearPrivateFlag(context, flag);
      }

      return retVerb;
   }

   @Override
   public final synchronized void headsetButtonClick(int button, int time) {
      if (button != 0 && button != 1) {
         if (button == 2 || button == 3) {
            int state = VoiceServices.getPhoneState();
            boolean isActive = state != 0 && state != 1;
            AudioRouter audioRouter = AudioRouter.getInstance();
            if (isActive && audioRouter.canChangeMasterVolume()) {
               audioRouter.incrementMasterVolume(button == 2 ? 10 : -10);
            }
         }
      } else {
         if (this._timerID != -1) {
            this.cancelTimer();
         }

         this._clickTime = time;
         this.startTimer();
      }
   }

   @Override
   public final synchronized void headsetButtonUnclick(int button, int time) {
      if (button == 0 || button == 1) {
         if (this._timerID != -1) {
            this.cancelTimer();
            if (time - this._clickTime < 500) {
               this.handleClick();
               return;
            }

            this.handleLongClick();
         }
      }
   }

   @Override
   public final void headsetInserted(int type) {
   }

   @Override
   public final void headsetRemoved() {
   }

   @Override
   public final synchronized void run() {
      this._timerID = -1;
      this.handleLongClick();
   }

   private final void startTimer() {
      this._timerID = this._app.invokeLater(this, this._timeout, false);
   }

   private final void cancelTimer() {
      this._app.cancelInvokeLater(this._timerID);
      this._timerID = -1;
   }
}
