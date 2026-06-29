package net.rim.device.apps.internal.memo;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.search.SearchResultCollection;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.internal.memo.resources.MemoResources;

final class MemoSearchResultCollection extends SearchResultCollection implements ListFieldCallback, FieldProvider {
   private Object _cachedContext;

   public MemoSearchResultCollection(Object criteria) {
      super((SearchCriterion[])criteria, new MemoCollectionImpl$MemoComparator(), true, false);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      MemoModelImpl memo = null;

      label32:
      try {
         memo = (MemoModelImpl)this.getAt(index);
      } finally {
         break label32;
      }

      if (memo instanceof PaintProvider) {
         this._cachedContext = VariableRowHeightProxy.addHeightAdjusterToContext(this._cachedContext, listField);
         memo.paint(graphics, 0, y, width, 100, this._cachedContext);
      } else {
         if (index == 0) {
            graphics.drawText(MemoResources.getString(180), 0, y, 4, width);
         }
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this.getAt(index);
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final Field getField(Object context) {
      return new MemoSearchResultField(this, this);
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
}
