package net.rim.device.apps.internal.browser.img;

import net.rim.device.api.browser.plugin.BrowserPageContext;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.container.FlowFieldManager;

final class ImageRenderer$ImageManager extends FlowFieldManager implements BrowserPageContext {
   public ImageRenderer$ImageManager() {
      super(3458764513820540928L);
   }

   @Override
   public final boolean getPropertyWithBooleanValue(int id, boolean defaultValue) {
      return defaultValue;
   }

   @Override
   public final int getPropertyWithIntValue(int id, int defaultValue) {
      return id == 2 ? 2 : defaultValue;
   }

   @Override
   public final Object getPropertyWithObjectValue(int id, Object defaultValue) {
      return defaultValue;
   }

   @Override
   public final String getPropertyWithStringValue(int id, String defaultValue) {
      return defaultValue;
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (width > Display.getWidth()) {
         width = Display.getWidth();
      }

      if (height > Display.getHeight()) {
         height = Display.getHeight();
      }

      super.sublayout(width, height);
   }
}
