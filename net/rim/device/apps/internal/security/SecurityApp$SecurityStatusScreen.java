package net.rim.device.apps.internal.security;

import net.rim.device.api.ui.Screen;

final class SecurityApp$SecurityStatusScreen extends Screen {
   public SecurityApp$SecurityStatusScreen() {
      super(new SecurityApp$SecurityStatusScreen$1(0), 0);
      this.setAcceptsInput(false);
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.setPosition(0, 0);
      this.setExtent(0, 0);
   }
}
