package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.autotext.AutoText;
import net.rim.device.api.ui.text.TextFilter;

final class CustomDictUnitEditor$CustomDictEditorScreen$1 extends TextFilter {
   private final CustomDictUnitEditor val$this$0;
   private final CustomDictUnitEditor$CustomDictEditorScreen this$1;

   CustomDictUnitEditor$CustomDictEditorScreen$1(CustomDictUnitEditor$CustomDictEditorScreen _1, CustomDictUnitEditor _2) {
      this.this$1 = _1;
      this.val$this$0 = _2;
   }

   @Override
   public final boolean validate(char character) {
      AutoText at = AutoText.getAutoText();
      return !at.isClauseSeparator(character) && !at.isSentenceTerminator(character);
   }

   @Override
   public final char convert(char character, int status) {
      return character;
   }
}
