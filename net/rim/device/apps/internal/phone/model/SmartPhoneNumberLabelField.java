package net.rim.device.apps.internal.phone.model;

import net.rim.device.api.ui.component.LabelField;

public final class SmartPhoneNumberLabelField extends LabelField {
   public SmartPhoneNumberLabelField(Object text, long flags) {
      super(text, flags);
   }

   @Override
   public final void setText(Object text, int offset, int length) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   static final Object convertCharacters(Object text) {
      Object newText = text;
      if (text instanceof byte[]) {
         byte[] bytes = (byte[])text;
         char[] newChars = new char[bytes.length];

         for (int i = 0; i < bytes.length; i++) {
            char c = byteToChar(bytes[i]);
            newChars[i] = PhoneNumberServices.convertCharToDisplay(c);
         }

         newText = newChars;
      }

      return newText;
   }

   private static final char byteToChar(byte b) {
      return (char)(b & 0xFF);
   }
}
