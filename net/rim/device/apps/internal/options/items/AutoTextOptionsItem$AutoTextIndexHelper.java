package net.rim.device.apps.internal.options.items;

import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.ui.autotext.AutoText;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

final class AutoTextOptionsItem$AutoTextIndexHelper implements KeywordIndexerHelper {
   private AutoText _autoTextEngine = AutoText.getAutoText();

   public AutoTextOptionsItem$AutoTextIndexHelper() {
   }

   @Override
   public final int getKeywords(Object element, String[] keywords) {
      keywords[0] = this._autoTextEngine.getReplacedString(element);
      Array.resize(keywords, 1);
      return 1;
   }

   @Override
   public final boolean checkForMatch(Object element, String[] words) {
      if (words.length != 1) {
         return false;
      }

      String replacedString = this._autoTextEngine.getReplacedString(element);
      return replacedString != null ? StringUtilities.startsWithIgnoreCaseAndAccents(replacedString, words[0]) : false;
   }
}
