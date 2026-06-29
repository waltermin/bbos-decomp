package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.ui.component.ActiveRichTextField;

public class CursorProviderActiveRichTextField extends ActiveRichTextField implements CursorProviderField {
   public CursorProviderActiveRichTextField(String text) {
   }

   @Override
   public int getCurrentCursorPosition(Object context) {
      return this.getCursorPosition();
   }

   @Override
   public void setCursorPosition(int pos, Object context) {
      this.setCursorPosition(pos);
   }

   @Override
   public int getCursorCount(Object context) {
      return this.getTextLength() + 1;
   }
}
