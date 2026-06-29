package net.rim.device.apps.internal.security;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.container.FullScreen;

final class SecurityApp$LockedScreen extends FullScreen {
   private final SecurityApp this$0;

   SecurityApp$LockedScreen(SecurityApp _1) {
      this.this$0 = _1;
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         Boolean lockAction = (Boolean)ApplicationRegistry.getApplicationRegistry().get(386954390916129487L);
         if (lockAction != null && lockAction) {
            this.this$0._turnBacklightOff = true;
            this.getApplication().invokeLater(new SecurityApp$LockedScreen$1(this), 800, false);
         }

         ApplicationRegistry.getApplicationRegistry().replace(386954390916129487L, Boolean.FALSE);
      }
   }

   @Override
   public final boolean navigationClick(int status, int time) {
      this.this$0._turnBacklightOff = false;
      this.this$0.displayPromptDialog(true, '\u0000');
      return true;
   }

   @Override
   public final boolean navigationMovement(int dx, int dy, int status, int time) {
      this.this$0.displayPromptDialog(true, '\u0000');
      return true;
   }

   @Override
   protected final boolean ignoreBacklightOffKeyEvent(int event, char key, int keycode, int time) {
      return false;
   }
}
