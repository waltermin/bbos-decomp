package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.internal.phone.api.ui.ThemedBannerCache;

final class PhoneAppScreen$1 extends ThemedBannerCache {
   @Override
   public final Field createBanner() {
      RibbonBanner ribbon = RibbonBanner.getInstance();
      return ribbon == null ? null : ribbon.getStatusBanner(null, 3);
   }
}
