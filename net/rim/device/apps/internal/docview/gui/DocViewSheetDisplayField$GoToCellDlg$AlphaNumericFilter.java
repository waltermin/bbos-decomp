package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.CharacterUtilities;

final class DocViewSheetDisplayField$GoToCellDlg$AlphaNumericFilter extends TextFilter {
   @Override
   public final char convert(char character, int status) {
      return character;
   }

   @Override
   public final boolean validate(char character) {
      return CharacterUtilities.isDigit(character) || CharacterUtilities.isLetter(character);
   }

   @Override
   public final long getPreferredInputStyle() {
      return 1073741824;
   }
}
