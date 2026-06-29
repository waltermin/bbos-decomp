package net.rim.device.apps.internal.addressbook;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.search.SearchResultCollection;
import net.rim.device.apps.api.ui.VariableRowHeightProxy;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

final class AddressBookSearchResultCollection extends SearchResultCollection implements ListFieldCallback, FieldProvider {
   private ContextObject _paintContext = (ContextObject)(new Object(78, 4));

   public AddressBookSearchResultCollection(Object criteria) {
      super(
         (Object[])criteria, AddressBookServices.getAddressBook().getComparator(null, AddressBookServices.getAddressBookOptions().getSortOrder()), true, false
      );
      this._paintContext.put(614335798810617774L, new Object(AddressBookServices.getAddressBookOptions().getSortOrder()));
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      Object addressCard = null;

      label32:
      try {
         addressCard = this.getAt(index);
      } finally {
         break label32;
      }

      if (addressCard instanceof Object) {
         VariableRowHeightProxy.addHeightAdjusterToContext(this._paintContext, listField);
         ((PaintProvider)addressCard).paint(graphics, 0, y, width, 100, this._paintContext);
      } else {
         if (index == 0) {
            graphics.drawText(AddressBookResources.getString(305), 0, y, 4, width);
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
      return new AddressBookSearchResultField(this, this);
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
