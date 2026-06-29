package net.rim.device.apps.internal.options.items;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.SortedReadableList;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.autotext.AutoText;
import net.rim.device.api.util.Comparator;

final class AutoTextOptionsItem$AutoTextSortedReadableList extends SortedReadableList {
   private AutoText _autoTextEngine = AutoText.getAutoText();
   private int _currentLocaleCode = Locale.getDefaultInputForSystem().getCode();

   public AutoTextOptionsItem$AutoTextSortedReadableList(CollectionEventSource sourceCollection, Comparator comparator) {
      super(sourceCollection, comparator);
   }

   @Override
   protected final void doAdd(Object element) {
      boolean performAdd = false;
      int elementLocaleCode = this._autoTextEngine.getLocaleCode(element);
      if (elementLocaleCode == this._currentLocaleCode || elementLocaleCode == 0) {
         performAdd = true;
      } else if ((this._currentLocaleCode & 65535) != 0 && elementLocaleCode == (this._currentLocaleCode & -65536)) {
         performAdd = true;
      }

      if (performAdd) {
         super.doAdd(element);
      }
   }
}
