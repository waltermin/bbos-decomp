package net.rim.device.api.ui.component;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.PrefixKeywordFilterList;
import net.rim.device.api.system.UnsupportedOperationException;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class KeywordFilteredField extends VerticalFieldManager {
   private KeywordFilteredListFinder _finder;
   private KeywordFilterCollectionListField _listField;
   private KeywordFilteredField$ListFieldCallbackImpl _listFieldCallback;
   private KeywordProvider _keywordProvider;

   public KeywordFilteredField(ReadableList list, KeywordProvider provider, String findLabel) {
      this._keywordProvider = provider;
      this._listFieldCallback = new KeywordFilteredField$ListFieldCallbackImpl();
      this._finder = new KeywordFilteredListFinder(null, findLabel, false);
      this._listField = new KeywordFilterCollectionListField(
         new PrefixKeywordFilterList(list, new KeywordFilteredField$KeywordIndexerHelperImpl(this)), this._listFieldCallback
      );
      this.add(this._finder);
      this._finder.linkToField(this._listField);
      this.add(this._listField);
      this._finder.resetSearch();
   }

   @Override
   public void delete(Field field) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void deleteRange(int start, int count) {
      throw new UnsupportedOperationException();
   }
}
