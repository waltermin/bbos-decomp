package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.api.browser.BrowserServices;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class LocationsListScreen$HTTPLink extends LabelField implements FocusChangeListener {
   String _url;
   int _colour;
   private final LocationsListScreen this$0;

   public LocationsListScreen$HTTPLink(LocationsListScreen this$0, String label, String url) {
      super(label, 18014398509481984L);
      this.this$0 = this$0;
      this._colour = 255;
      this._url = url;
      this.setFocusListener(this);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      Font font = this.getFont();
      font = font.derive(5);
      this.setFont(font);
   }

   @Override
   public final boolean navigationClick(int status, int time) {
      this.launchURL();
      return true;
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      menu.add(new LocationsListScreen$1(this, LBSResources.getString(492), 0, 0));
      super.makeMenu(menu, instance);
   }

   private final void launchURL() {
      System.out.println("POI loading URL: " + this._url);
      BrowserServices.loadUrl(this._url);
   }

   @Override
   protected final void paint(Graphics graphics) {
      int oldColour = graphics.getColor();
      graphics.setColor(this._colour);
      super.paint(graphics);
      graphics.setColor(oldColour);
   }

   @Override
   public final void focusChanged(Field field, int eventType) {
      if (eventType == 1) {
         this._colour = 16777215;
      } else {
         this._colour = 255;
      }

      this.invalidate();
   }
}
