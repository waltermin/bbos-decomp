package net.rim.device.apps.internal.browser.html;

import java.util.Vector;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.stack.FormData;
import net.rim.device.apps.internal.browser.ui.BrowserLinkBitmapField;
import org.w3c.dom.html2.HTMLInputElement;

public final class HTMLImageInputField extends BrowserLinkBitmapField implements SubmitButton {
   private boolean _selected;
   private HTMLInput _controller;

   public HTMLImageInputField(BrowserContentImpl browserField, HTMLInputElement controller, Bitmap bitmap, String imageUrl, long style) {
      super(browserField, bitmap, imageUrl, style, null, null, -1, -1, 1, null, null);
      this._controller = (HTMLInput)controller;
   }

   @Override
   public final Object getCookieWithFocus() {
      HTMLForm form = (HTMLForm)this._controller.getForm();
      if (form == null) {
         return null;
      }

      this._selected = true;
      String url = form.getURL();
      FormData postData = form.getPostData();
      this._selected = false;
      ContextObject context = new ContextObject();
      Vector formSubmission = new Vector(4);
      formSubmission.addElement(url);
      formSubmission.addElement(postData);
      formSubmission.addElement(form);
      formSubmission.addElement(this._controller);
      context.put(249, formSubmission);
      context.put(-442409970680484936L, this._controller.getOwnerDocument());
      return FactoryUtil.createInstance(5019899335844518230L, context);
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      this._controller.eventOccurred(5);
      return true;
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      if ((status & 1) == 0) {
         this._controller.eventOccurred(5);
         return true;
      } else {
         return super.trackwheelClick(status, time);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         this._controller.eventOccurred(5);
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final void onFocus(int direction) {
      super.onFocus(direction);
      this._controller.eventOccurred(3);
   }

   @Override
   protected final void onUnfocus() {
      super.onUnfocus();
      this._controller.eventOccurred(6);
   }

   public final void clickInternal() {
      this.trackwheelClick(0, 0);
   }

   @Override
   public final boolean getSelected() {
      boolean value = this._selected;
      this._selected = false;
      return value;
   }

   @Override
   public final void setSelected() {
      this._selected = true;
   }
}
