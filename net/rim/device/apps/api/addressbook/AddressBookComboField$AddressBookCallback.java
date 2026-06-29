package net.rim.device.apps.api.addressbook;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

public class AddressBookComboField$AddressBookCallback implements ListFieldCallback {
   private final AddressBookComboField this$0;

   public AddressBookComboField$AddressBookCallback(AddressBookComboField _1) {
      this.this$0 = _1;
   }

   public void update() {
      Collection collection = AddressBookServices.getAddressBook().getAddressBookCollection();
      if (collection != null) {
         synchronized (collection) {
            synchronized (this.this$0._filteredList) {
               int resultsSize = this.this$0._filteredList.size();
               ListField list = this.this$0.getList();
               int selection = list.getSelectedIndex();
               if (list.isMuddy() && selection >= 0 && selection < resultsSize) {
                  list.setSize(resultsSize, selection);
               } else {
                  list.setSize(resultsSize);
               }

               if (resultsSize > 0) {
                  this.this$0.showDropList();
               } else {
                  this.this$0.hideDropList();
               }
            }
         }
      }
   }

   @Override
   public void drawListRow(ListField listField, Graphics g, int index, int y, int width) {
      Object data = this.get(listField, index);
      if (data != null) {
         String dataString;
         if (data instanceof AddressCardModel) {
            dataString = this.getAddressCardRepresentation((AddressCardModel)data);
         } else {
            dataString = data.toString();
         }

         y = listField.getAdjustedY(g.getFont(), dataString, y);
         g.drawText(dataString, 0, Integer.MAX_VALUE, 0, y, 64, width);
      }
   }

   private String getAddressCardRepresentation(AddressCardModel addressCard) {
      String rep = addressCard.toString();
      if (addressCard.getCompanyInfo() != null) {
         String companyString = addressCard.getCompanyInfo().toString();
         if (companyString != null && companyString.length() > 0) {
            return rep + ", " + companyString;
         }
      }

      return rep;
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return 0;
   }

   @Override
   public Object get(ListField listField, int index) {
      synchronized (this.this$0._filteredList) {
         return index >= 0 && index < this.this$0._filteredList.size() ? this.this$0._filteredList.getAt(index) : null;
      }
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }
}
