package net.rim.device.apps.internal.bis.api.ui;

import java.util.Vector;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.data.Filter;

public final class FilterList extends VerticalFieldManager implements ListFieldCallback {
   private Vector _filters;
   private FilterList$FilterListField _listField;
   private ListColumnPainter _columnPainter;
   private NotificationMenuItemListener _menuItemListener;
   private ThemeAttributeSet _themeAttributesHeader;
   private int _themeGeneration;
   public static final int INSTANCE_FILTER_LIST_SELECTED;
   public static final String PARAM_FILTER_ID;
   public static final String PARAM_FILTER_NAME;
   private static final Tag TAG_HEADER = Tag.create("header");

   public final void setMenuListener(NotificationMenuItemListener menuItemListener) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final Filter getItem(ListField lisField, int index) {
      return (Filter)this._filters.elementAt(index);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return 10;
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this.getItem(listField, index);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      Object item = this.getItem(listField, index);
      if (item != null && item instanceof Filter) {
         this._columnPainter.drawText(0, ((Filter)item).getName(), y, false);
         if (((Filter)item).getSendAlert()) {
            this._columnPainter.drawText(1, ApplicationResources.getString(279), y, false);
            return;
         }

         this._columnPainter.drawText(1, ApplicationResources.getString(280), y, false);
      }
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   public FilterList(Vector filters) {
      this._filters = filters;
      this._columnPainter = new ListColumnPainter(2);
      this.add(new FilterList$HeaderField(this, ApplicationResources.getString(282), ApplicationResources.getString(310)));
      if (null != this._filters && filters.size() != 0) {
         this._listField = new FilterList$FilterListField(this, this._filters.size(), 2);
         this._listField.setId("filters");
         this._listField.setCallback(this);
         this.add(this._listField);
      } else {
         this.add(new FilterList$EmptyFilterField(this));
      }

      this.add(new FilterList$HeaderField(this, ""));
   }
}
