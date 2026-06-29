package net.rim.device.apps.internal.phone.options;

import net.rim.device.apps.api.ui.CommonResources;

final class CallBarringOption$CallBarringProfile extends AbstractSSOptionProfile {
   private int _type;
   private boolean _incoming;
   int _statusFlags;
   private final CallBarringOption this$0;
   private static final int SS_REQUEST_TIMEOUT;

   public CallBarringOption$CallBarringProfile(CallBarringOption _1, String name, int type, int statusFlags, boolean incoming) {
      super(name);
      this.this$0 = _1;
      this._type = type;
      this._incoming = incoming;
      this._statusFlags = statusFlags;
   }

   @Override
   public final void enable() {
      this.enableOrDisable(true);
   }

   @Override
   public final void disable() {
      this.enableOrDisable(false);
   }

   private final void enableOrDisable(boolean enable) {
      String password = SSManager.getCallBarringPasswordFromUser(CommonResources.getString(2012));
      if (password != null && password.length() > 0) {
         int type = this._type;
         if (!enable) {
            if (this._incoming) {
               if (CallBarringOption.isProvisioned(this.this$0._incomingStatusFlags[0])) {
                  type = 153;
               }
            } else if (CallBarringOption.isProvisioned(this.this$0._outgoingStatusFlags[0])) {
               type = 145;
            }
         }

         new EnableCallBarring(type, password, enable).start();
         this.this$0.closeScreen();
      }
   }

   @Override
   public final boolean isEnabled() {
      return (this._statusFlags & 2) != 0;
   }
}
