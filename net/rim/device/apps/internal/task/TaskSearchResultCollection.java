package net.rim.device.apps.internal.task;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.search.SearchResultCollection;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.internal.task.resources.TaskResources;

final class TaskSearchResultCollection extends SearchResultCollection implements ListFieldCallback, FieldProvider {
   Object _cachedContext;

   public TaskSearchResultCollection(Object criteria) {
      super((Object[])criteria, new TaskComparator(), true, false);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      TaskModelImpl task = null;
      VariableRowHeightProxy.addHeightAdjusterToContext(this._cachedContext, listField);

      label32:
      try {
         task = (TaskModelImpl)this.getAt(index);
      } finally {
         break label32;
      }

      if (task instanceof Object) {
         task.paint(graphics, 0, y, width, 100, this._cachedContext);
      } else {
         if (index == 0) {
            graphics.drawText(TaskResources.getString(37), 0, y, 4, width);
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
      return new TaskSearchResultField(this, this);
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
