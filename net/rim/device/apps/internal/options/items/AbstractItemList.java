package net.rim.device.apps.internal.options.items;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.collection.util.PrefixKeywordFilterList;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;

public class AbstractItemList implements ListFieldCallback, ReadableList {
   protected KeywordFilteredScreen _screen;
   private PrefixKeywordFilterList _keywordList;
   private boolean _listLoaded = false;
   private boolean _showAll;
   private Object[] _list;
   private String _cachedEmptyString;

   protected void paintElement(Object _1, Graphics _2, int _3, int _4) {
      throw null;
   }

   protected void refresh() {
      throw null;
   }

   protected KeywordIndexerHelper getKeywordIndexer() {
      throw null;
   }

   protected String getEmptyString() {
      throw null;
   }

   protected void setList(Object[] newList) {
      this._list = newList;
   }

   protected KeywordFilterList getKeywords() {
      if (this._keywordList == null) {
         this._keywordList = new PrefixKeywordFilterList(this, this.getKeywordIndexer());
      }

      return this._keywordList;
   }

   protected void listUpdated() {
      this._listLoaded = true;
      if (this._keywordList != null) {
         this._keywordList.reset(this);
      }

      this._screen.invalidate();
   }

   protected boolean isShowAll() {
      return this._showAll;
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      CollectionListField collectionListField = (CollectionListField)listField;
      Object element = collectionListField.getElementAt(index);
      if (element != null) {
         this.paintElement(element, graphics, y, width);
      } else {
         if (index == 0 && this._listLoaded) {
            if (this._cachedEmptyString == null) {
               this._cachedEmptyString = this.getEmptyString();
            }

            graphics.drawText(this._cachedEmptyString, 0, y, 4, width);
         }
      }
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public Object get(ListField listField, int index) {
      CollectionListField collectionListField = (CollectionListField)listField;
      return collectionListField.getElementAt(index);
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public int size() {
      return this._list != null ? this._list.length : 0;
   }

   @Override
   public Object getAt(int index) {
      return this._list[index];
   }

   @Override
   public int getIndex(Object o) {
      return -1;
   }

   @Override
   public int getAt(int index, int count, Object[] elements, int destIndex) {
      int x = 0;

      for (int i = index; x < count && i < this._list.length; x++) {
         elements[x] = this._list[i];
         i++;
      }

      return x;
   }

   AbstractItemList(boolean showAll, KeywordFilteredScreen screen) {
      this._screen = screen;
      this._showAll = showAll;
      this._screen.setEmptyString("", 0);
      this.refresh();
   }
}
