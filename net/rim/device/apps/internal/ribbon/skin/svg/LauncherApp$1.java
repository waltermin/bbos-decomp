package net.rim.device.apps.internal.ribbon.skin.svg;

import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.theme.Theme$LayoutFactory;

class LauncherApp$1 implements Theme$LayoutFactory {
   @Override
   public Manager getLayout(String layout, Object context) {
      return MediaLayout.create(context, layout);
   }
}
