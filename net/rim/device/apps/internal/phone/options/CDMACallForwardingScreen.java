package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class CDMACallForwardingScreen extends AppsMainScreen implements ListFieldCallback {
   private ListField _listField;
   private int[] _flags;
   private int[] _provisionedTypes;

   CDMACallForwardingScreen(int[] flags, int[] provisionedTypes) {
      super(0);
      this._flags = flags;
      this._provisionedTypes = provisionedTypes;
      this._listField = (ListField)(new Object(provisionedTypes.length));
      this._listField.setCallback(this);
      this.add(this._listField);
   }

   private final void openSelectedForwardingType() {
      int selectedIndex = this._listField.getSelectedIndex();
      int type = this._provisionedTypes[selectedIndex];
      int[] flags = new int[this._flags.length];
      flags[type] = this._flags[type];
      CallForwardingScreen screen = new CallForwardingScreen(flags);
      screen.setTitle(PhoneResources.getString(191));
      UiApplication.getUiApplication().pushModalScreen(screen);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            this.openSelectedForwardingType();
            return true;
         case '\u001b':
            this.close();
            return true;
         default:
            return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this.openSelectedForwardingType();
      return true;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      String text = ForwardingTypes.FORWARDING_NAMES[this._provisionedTypes[index]].trim();
      if (text.charAt(text.length() - 1) == ':') {
         text = text.substring(0, text.length() - 1);
      }

      graphics.drawText(text, 0, y, 64, width);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return null;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }
}
