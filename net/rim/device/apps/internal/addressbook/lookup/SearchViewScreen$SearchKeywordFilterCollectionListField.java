package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.ui.component.KeywordFilterCollectionListField;
import net.rim.device.api.ui.component.ListFieldCallback;

final class SearchViewScreen$SearchKeywordFilterCollectionListField extends KeywordFilterCollectionListField {
   private SearchViewScreen _searchViewScreen;

   public SearchViewScreen$SearchKeywordFilterCollectionListField(ReadableList list, ListFieldCallback listCallback) {
   }

   @Override
   protected final void doUpdateList() {
      super.doUpdateList();
      if (this._searchViewScreen != null) {
         this._searchViewScreen.updateTitle();
      }
   }
}
