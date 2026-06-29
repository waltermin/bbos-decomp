package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.event.NavigationEvent;

public class BrowserUrlEditField extends BasicEditField {
   private String _clearValue;
   public static final String STRING_HTTP = "http://";
   public static final String STRING_HTTPS = "https://";
   public static final String STRING_HTTP_WWW = "http://www.";

   public BrowserUrlEditField(String label, String initialValue, int maxNumChars, long style, String clearValue) {
      super(label, initialValue, maxNumChars, style);
      this._clearValue = clearValue;
   }

   @Override
   protected boolean backspace() {
      return "http://www.".equals(this.getText()) ? super.backspace(4) > 0 : super.backspace();
   }

   @Override
   public void dispatchEvent(Event rEvent) {
      super.dispatchEvent(rEvent);
      if (!rEvent.isConsumed() && rEvent instanceof NavigationEvent) {
         NavigationEvent event = (NavigationEvent)rEvent;
         int dx = event.getDxMagnitude();
         if (dx != 0) {
            Manager manager = this.getManager();
            if (manager instanceof UrlEntryField) {
               UrlEntryField urlEntry = (UrlEntryField)manager;
               urlEntry.hideDropList();
               int newCursorPosition = this.getCursorPosition() + dx;
               if (newCursorPosition < 0) {
                  newCursorPosition = 0;
               } else if (newCursorPosition > this.getTextLength()) {
                  newCursorPosition = this.getTextLength();
               }

               this.setCursorPosition(newCursorPosition);
               rEvent.consume();
            }
         }
      }
   }

   @Override
   public boolean paste(Clipboard cb) {
      String pasteValue = cb.toString();
      if (!this.validate(pasteValue.trim())) {
         Status.show(CommonResource.getString(10024));
         return false;
      } else {
         this.clearDefaultTextOnPaste(pasteValue);
         return super.paste(cb);
      }
   }

   private void clearDefaultTextOnPaste(String textToPaste) {
      if (textToPaste != null && textToPaste.length() >= 7 && textToPaste.charAt(0) == 'h') {
         String currentText = this.getText();
         int length = currentText != null ? currentText.length() : -1;
         if ((length == 11 || length == 7 || length == 8)
            && (textToPaste.startsWith("http://") || textToPaste.startsWith("https://"))
            && ("http://www.".equals(currentText) || "http://".equals(currentText) || "https://".equals(currentText))) {
            this.setText("");
         }
      }
   }

   @Override
   public int insert(String text, int context, boolean stripInvalid, boolean validateText) {
      if (stripInvalid) {
         this.clearDefaultTextOnPaste(text);
      }

      return super.insert(text, context, stripInvalid, validateText);
   }

   @Override
   public void clear(int context) {
      this.setText(this._clearValue);
      this.setFocus();
   }

   @Override
   public void setFocus() {
      this.getScreen().ensureRegionVisible(this, 0, 0, 0, 0);
      super.setFocus();
   }

   @Override
   protected boolean isClearMenuItemAllowed() {
      return !this.getText().equals(this._clearValue);
   }
}
