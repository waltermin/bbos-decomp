package net.rim.device.apps.internal.addressbook;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

public final class AddressBookUtilities {
   private AddressBookUtilities() {
   }

   public static final boolean confirmDelete(Object itemToDelete) {
      return confirmDelete(itemToDelete, false);
   }

   public static final boolean confirmDelete(Object itemToDelete, boolean ignoreOptionSetting) {
      if (!AddressBookServices.getAddressBookOptions().getConfirmDelete() && !ignoreOptionSetting) {
         return true;
      }

      String prompt;
      if (!(itemToDelete instanceof VerbDescriptionProvider)) {
         prompt = itemToDelete.toString();
      } else {
         VerbDescriptionProvider descriptor = (VerbDescriptionProvider)itemToDelete;
         prompt = descriptor.getVerbDescription(null);
      }

      if (prompt == null || prompt.length() == 0) {
         prompt = AddressBookResources.getString(1001);
      }

      String pattern = AddressBookResources.getString(1023);
      String formattedString = MessageFormat.format(pattern, new String[]{prompt});
      int retVal = Dialog.ask(2, formattedString, -1);
      return retVal == 3;
   }
}
