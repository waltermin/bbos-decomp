package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.VariableHeightListField;
import net.rim.device.api.ui.component.VariableHeightListFieldCallback;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.messaging.messagelist.DateSortedSeparatedMessageArray;
import net.rim.device.apps.api.messaging.messagelist.MessageListColumnPainter;
import net.rim.device.apps.api.search.SearchResultCollection;

final class MessageSearchResultCollection extends SearchResultCollection implements FieldProvider, VariableHeightListFieldCallback {
   private MessageListColumnPainter _messageColumnPainter = (MessageListColumnPainter)(new Object());
   private MessageSearchResultField _listField;
   private DateSortedSeparatedMessageArray _sortedSeparatedItems = (DateSortedSeparatedMessageArray)(new Object(102, (LongKeyProviderAdaptor)(new Object())));

   MessageSearchResultCollection(Object criteria) {
      super((Object[])criteria, (Comparator)(new Object((LongKeyProviderAdaptor)(new Object()))), true, true);
      this.addCollectionListener(new Object(this._sortedSeparatedItems));
      this._listField = new MessageSearchResultField(this._sortedSeparatedItems, this, this);
   }

   @Override
   public final void loadFrom(Object collection) {
      super.loadFrom(collection);
      this._sortedSeparatedItems.loadFrom(this);
   }

   @Override
   public final Field getField(Object context) {
      return this._listField;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final int getOrder(Object context) {
      return 0;
   }

   @Override
   public final void drawListRow(VariableHeightListField listField, Graphics graphics, int index, int y, int width) {
      if (index < this._sortedSeparatedItems.size()) {
         this._messageColumnPainter.drawRow(this._listField, graphics, y, width, this._sortedSeparatedItems.getAt(index));
      }
   }

   @Override
   public final int getRowHeight(VariableHeightListField listField, int index) {
      if (index < this._sortedSeparatedItems.size()) {
         Object entry = this._sortedSeparatedItems.getAt(index);
         return this._messageColumnPainter.getRowHeight(entry);
      } else {
         return 0;
      }
   }

   @Override
   public final int getPreferredWidth(VariableHeightListField listField) {
      return Display.getWidth();
   }

   @Override
   public final Object get(VariableHeightListField listField, int index) {
      return this._sortedSeparatedItems.getAt(index);
   }

   @Override
   public final int indexOfList(VariableHeightListField listField, String prefix, int start) {
      return this._listField.getSelectedIndex();
   }
}
