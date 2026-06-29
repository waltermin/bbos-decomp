package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.PhoneControlListener;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.api.Out;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;

final class SSQueryVerb extends Verb implements PhoneControlListener, Runnable {
   private int _queryType;
   private SSQueryListener _listener;
   private boolean _timedOut;
   private Application _app;
   private int _timeoutTimerId;
   private static final long TIMEOUT = 30000L;

   public SSQueryVerb(int queryType, SSQueryListener listener, Application app) {
      super(268491007);
      this._listener = listener;
      this._queryType = queryType;
      this._timedOut = false;
      this._app = app;
      this._app.addRadioListener(this);
      this._timeoutTimerId = this._app.invokeLater(this, 30000, false);
   }

   @Override
   public final String toString() {
      return "Query";
   }

   private final void cancelTimeoutTimer() {
      this._app.cancelInvokeLater(this._timeoutTimerId);
   }

   private final void stopListening() {
      this._app.removeRadioListener(this);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object invoke(Object parameter) {
      boolean var4 = false /* VF: Semaphore variable */;

      label24:
      try {
         var4 = true;
         Phone.getInstance().querySSOption(this._queryType);
         Out.p(((StringBuffer)(new Object("SSQryScrn.query("))).append(this._queryType).append(')').toString());
         var4 = false;
      } finally {
         if (var4) {
            System.out.println("SS QUERY RADIO EXCEPTION");
            break label24;
         }
      }

      if (this._listener != null) {
         this._listener.queryStarted();
      }

      return null;
   }

   @Override
   public final void run() {
      Status.show("Query timed out.");
      this._timedOut = true;
      if (this._listener != null) {
         this._listener.queryTimedOut();
      }

      this.stopListening();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void ssRequestSucceeded(int ss, int action, int result, int bearerService, boolean isUSSDCmd, boolean forwardingNumberAvailable) {
      if (!this._timedOut) {
         boolean var13 = false /* VF: Semaphore variable */;

         label46:
         try {
            var13 = true;
            Phone e = Phone.getInstance();
            int bearer = 0;
            switch (PhoneUtilities.getCurrentLineId()) {
               case 0:
                  bearer = 0;
                  break;
               case 1:
               default:
                  bearer = 2;
                  break;
               case 2:
                  bearer = 11;
            }

            int x = e.querySSOptionResult(this._queryType, bearer);
            boolean provisioned = (x & 1) != 0;
            boolean active = (x & 2) != 0;
            if (this._listener != null) {
               this._listener.queryFinished(provisioned, active);
               Out.p(((StringBuffer)(new Object("SSQryScrn.queried("))).append(provisioned).append(',').append(active).append(')').toString());
               var13 = false;
            } else {
               var13 = false;
            }
         } finally {
            if (var13) {
               System.out.println("SS QUERY RADIO EXCEPTION");
               break label46;
            }
         }
      }

      this.stopListening();
      this.cancelTimeoutTimer();
   }

   @Override
   public final void ssRequestFailed(int reason, int bearerService, boolean isUSSDCmd) {
      Status.show("Query Failed");
      if (this._listener != null) {
         this._listener.queryFinished(false, false);
      }

      this.stopListening();
      this.cancelTimeoutTimer();
   }

   @Override
   public final void ssRequestRejected(boolean isUSSDCmd) {
      Status.show("Query Rejected");
      if (this._listener != null) {
         this._listener.queryFinished(false, false);
      }

      this.stopListening();
      this.cancelTimeoutTimer();
   }

   @Override
   public final void ssRequestReleased(boolean isUSSDCmd) {
   }

   @Override
   public final void ssRequestInvalidPassword() {
   }

   @Override
   public final void ssPasswordRequested(int requestType) {
   }

   @Override
   public final void ssUpdated(int ssOption, int state) {
   }

   @Override
   public final void ssNotification(int ssOption) {
   }

   @Override
   public final void ssUssDisplay(byte[] data, int messageCoding, boolean collectInput) {
   }

   @Override
   public final void featureReady() {
   }

   @Override
   public final void responseEnableFDN(int status) {
   }

   @Override
   public final void voiceLineChanged(int line) {
   }

   @Override
   public final void alternateLinesUpdated() {
   }

   @Override
   public final void voicemailCountUpdated(int line, int count) {
   }
}
