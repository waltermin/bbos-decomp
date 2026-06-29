package net.rim.device.apps.internal.secureemail.cache;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.secureemail.VerticalExtentIndicatorFieldManager;

public class CachedMissingPrivateKeyField extends CachedManager {
   @Override
   protected Manager createManager(Object context) {
      if (ContextObject.getFlag(context, 101)) {
         return super.createManager(context);
      }

      int extentIndicatorRGB = Graphics.isColor() ? 16711680 : 0;
      return new VerticalExtentIndicatorFieldManager(extentIndicatorRGB, 1152921504606846976L);
   }

   @Override
   public boolean hasHeaderFields() {
      return true;
   }
}
