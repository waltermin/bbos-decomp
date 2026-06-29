package net.rim.device.apps.internal.browser.webfeed;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.internal.ui.Border;

final class WebFeedPopup extends Screen {
   private RichTextField _text;
   private int _x;
   private int _y;
   private int _fieldHeight;
   private UiApplication _app;
   private WebFeedPopup$Listener _listener;
   private static final Tag TAG = Tag.create("webfeedtooltip");

   public WebFeedPopup() {
      super((Manager)(new Object()));
      this.setTag(TAG);
      this.setBorder((Border)(new Object(1, 1, 1, 1)));
      this._text = (RichTextField)(new Object("", 36028797018963968L));
      this.getDelegate().add(this._text);
      this._listener = new WebFeedPopup$Listener(this);
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this._app.addUserInputEventListener(this._listener);
      } else {
         this._app.removeUserInputEventListener(this._listener);
      }
   }

   public final void show(UiApplication app, String title, int x, int y, int fieldHeight) {
      this._x = x;
      this._y = y;
      this._app = app;
      this._fieldHeight = fieldHeight;
      this._text.setText(title);
      this._app.pushScreen(this);
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
      XYRect fmExtent = this.getDelegate().getExtent();
      if (this._y + fmExtent.height + this._fieldHeight > Display.getHeight()) {
         if (this._y - fmExtent.height >= 0) {
            this.setPosition(this._x, this._y - fmExtent.height);
         } else {
            int heightBelow = Display.getHeight() - this._y - this._fieldHeight;
            int heightAbove = this._y - 5;
            int remainingHeight;
            int yPos;
            if (heightAbove > heightBelow) {
               yPos = 5;
               remainingHeight = heightAbove;
            } else {
               yPos = this._y + this._fieldHeight;
               remainingHeight = heightBelow;
            }

            this.layoutDelegate(width, remainingHeight);
            this.setPosition(this._x, yPos);
            fmExtent = this.getDelegate().getExtent();
         }
      } else {
         this.setPosition(this._x, this._y + this._fieldHeight);
      }

      this.setExtent(fmExtent.width, fmExtent.height);
   }
}
