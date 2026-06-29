package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.autotext.AutoText;
import net.rim.device.api.util.Comparator;

final class AutoTextOptionsItem$AutoTextComparator implements Comparator {
   private AutoText _autoTextEngine = AutoText.getAutoText();

   @Override
   public final int compare(Object o1, Object o2) {
      return this._autoTextEngine.getReplacedString(o1).compareTo(this._autoTextEngine.getReplacedString(o2));
   }
}
