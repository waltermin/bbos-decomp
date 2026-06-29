package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.apps.api.ribbon.Action;

final class PowerOffAction extends Action {
   private Bitmap _icon;
   private ResourceBundleFamily _rbf;
   private static String POWER_OFF_BITMAP_NAME = "poweroff.gif";
   private static String POWER_OFF_RIBBON_ENTRY_NAME = "net_rim_PowerOff";

   PowerOffAction(ApplicationDescriptor applicationDescriptor) {
      super(applicationDescriptor, POWER_OFF_RIBBON_ENTRY_NAME, 255);
      this._icon = Bitmap.getBitmapResource(POWER_OFF_BITMAP_NAME);
      this._rbf = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
   }

   @Override
   protected final String getDescription() {
      return this._rbf.getString(3);
   }

   @Override
   public final String get(long propID, String defaultReturned) {
      return super.get(propID, defaultReturned);
   }

   @Override
   public final Object get(long propID, Object defaultReturned) {
      return propID == 5 ? this._icon : defaultReturned;
   }
}
