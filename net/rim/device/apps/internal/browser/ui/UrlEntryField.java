package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ComboField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.apps.internal.browser.util.VisitedURLStore;

public final class UrlEntryField extends ComboField {
   private VisitedURLStore _urlStore;
   private int[] _indices = new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, -804651007, 5, -804651005, 5, 6, 7};
   private String _firstEntryText;
   public static final int FIELD_CHANGE_ACTION = 132388;

   public UrlEntryField(Font font) {
      ListField listField = (ListField)(new Object());
      listField.setFont(font);
      this.setList(listField);
      BasicEditField edit = new BrowserUrlEditField(null, "http://www.", 2048, 2801795200L, "http://www.");
      edit.setFont(font);
      this.setEditable(edit);
      this._urlStore = VisitedURLStore.getInstance();
      this.setController(new UrlEntryField$UrlEntryController(this));
      UrlEntryField$UrlEntryCallback callback = new UrlEntryField$UrlEntryCallback(this);
      listField.setCallback(callback);
      listField.setFocusListener(callback);
   }

   @Override
   public final void setText(String text) {
      super.setText(text);
      Manager manager = this.getManager();
      if (manager != null && manager.isStyle(1125899906842624L)) {
         XYRect focusRect = (XYRect)(new Object());
         this.getEditable().getFocusRect(focusRect);
         int hScroll = focusRect.x + focusRect.width - manager.getWidth();
         manager.setHorizontalScroll(hScroll < 0 ? 0 : hScroll);
      }
   }

   @Override
   public final void onDisplay() {
      super.onDisplay();
      BasicEditField field = this.getEditable();
      field.setCursorPosition(field.getTextLength());
   }

   public final String getUrlToLoad() {
      String url = this.getText();
      this._urlStore.add(url);
      return url;
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      this.fieldChangeNotify(132388);
      return super.navigationClick(status, time);
   }

   @Override
   protected final boolean keyChar(char character, int status, int time) {
      if (character == '\n') {
         this.fieldChangeNotify(132388);
         return true;
      } else {
         return super.keyChar(character, status, time);
      }
   }

   static final void access$300(UrlEntryField x0, int x1) {
      x0.fieldChangeNotify(x1);
   }
}
