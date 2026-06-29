package net.rim.device.apps.internal.options;

import net.rim.device.api.ui.autotext.AutoText;
import net.rim.device.api.ui.text.TextFilter;

class CustomDictUnitEditor$CustomDictEditorScreen$1 extends TextFilter {
   private final CustomDictUnitEditor$CustomDictEditorScreen this$0;

   CustomDictUnitEditor$CustomDictEditorScreen$1(CustomDictUnitEditor$CustomDictEditorScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public boolean validate(char character) {
      return character != ',' && !AutoText.getAutoText().isSentenceTerminator(character);
   }

   @Override
   public char convert(char character, int status) {
      return character;
   }
}
