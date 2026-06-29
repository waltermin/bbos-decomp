package net.rim.device.apps.internal.browser.webfeed;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.ui.CommonResources;

final class WebFeedDateField extends Field {
   private String _formattedDate;
   private static final Tag TAG = Tag.create("webfeeddate");

   public WebFeedDateField(String date) {
      super(18014398509481984L);
      this.setTag(TAG);
      this._formattedDate = date;
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(width, this.getFont().getHeight());
   }

   @Override
   protected final void paint(Graphics g) {
      g.drawText(this._formattedDate, 0, 0, 4, this.getContentWidth());
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      menu.add(new WebFeedDateField$1(this, CommonResources.getResourceBundle(), 1300, 611984, 0));
   }
}
