package net.rim.device.apps.internal.bis.ui;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.apps.internal.bis.api.ui.FilterLabelField;
import net.rim.device.apps.internal.bis.api.ui.NotificationMenuItem;
import net.rim.device.apps.internal.bis.api.ui.NotificationMenuItemListener;

final class AbstractFilterScreen$DeleteNotificationMenuItemListener implements NotificationMenuItemListener {
   private FilterLabelField _filterField;
   private LabelField _separator;
   private final AbstractFilterScreen this$0;

   public AbstractFilterScreen$DeleteNotificationMenuItemListener(AbstractFilterScreen _1, FilterLabelField labelField) {
      this(_1, labelField, null);
   }

   public AbstractFilterScreen$DeleteNotificationMenuItemListener(AbstractFilterScreen _1, FilterLabelField labelField, LabelField separator) {
      this.this$0 = _1;
      this._filterField = labelField;
      this._separator = separator;
   }

   @Override
   public final void menuItemSelected(NotificationMenuItem menuItem) {
      FlowFieldManager manager = (FlowFieldManager)this._filterField.getManager();
      int index = this._filterField.getIndex();
      if (null != manager && index != -1) {
         if (index == 0 && manager.getFieldCount() > 1) {
            manager.delete(manager.getField(index + 1));
         }

         if (this._separator != null && this._separator.getIndex() != -1) {
            manager.delete(this._separator);
         }

         manager.delete(this._filterField);
      }

      this.this$0._filterValues.removeElement(this._filterField.getText());
   }
}
