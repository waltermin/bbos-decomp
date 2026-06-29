package net.rim.device.apps.internal.lbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class MapScreen$22 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$22(MapScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      String year = "2006";
      String additionalTermsOfUse = "For additional terms of use:\r\nwww.blackberry.com/maps\r\n\r\n";
      boolean showPOI = LBSOptions.getBoolean(4717295063260546653L, false);
      String googleAttribution = "";
      if (showPOI) {
         googleAttribution = "\r\n\r\nGoogle Terms of Service (http://www.google.com/terms_of_service.html)\r\n\r\nGoogle Local Terms of Service (http://www.google.com/intl/en_us/help/terms_maps.html)\r\n\r\nLocal Legal Notices (http://www.google.com/intl/en_us/help/legalnotices_maps.html)\r\n\r\nGoogle Privacy Policy (http://www.google.com/privacypolicy.html)";
      }

      EncodedImage tempImage = ThemeManager.getActiveTheme().getImage("net_rim_bb_lbs", true);
      Bitmap bitMap = tempImage != null
         ? EncodedImage.createEncodedImage(tempImage.getData(), 0, -1).getBitmap()
         : Bitmap.getBitmapResource("net_rim_bb_lbs.png");
      Dialog about = (Dialog)(new Object(
         0,
         ((StringBuffer)(new Object()))
            .append(
               MessageFormat.format(
                  ((StringBuffer)(new Object())).append(additionalTermsOfUse).append(LBSResources.getString(125)).toString(), new String[]{"", "2006"}
               )
            )
            .append(googleAttribution)
            .toString(),
         0,
         bitMap,
         0
      ));
      about.doModal();
   }
}
