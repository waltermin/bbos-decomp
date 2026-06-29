package net.rim.device.apps.internal.addressbook.resources;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.internal.resource.AddressBookResource;

public class AddressBookResources implements AddressBookResource {
   private static ResourceBundleFamily _strings;

   public static String getString(int id) {
      return getResourceBundleFamily().getString(id);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static ResourceBundleFamily getResourceBundleFamily() {
      if (_strings == null) {
         boolean var2 = false /* VF: Semaphore variable */;

         try {
            var2 = true;
            _strings = ResourceBundle.getBundle(5390928610432442684L, "net.rim.device.apps.internal.resource.AddressBook");
            var2 = false;
         } finally {
            if (var2) {
               throw new Object("No addressbook resources");
            }
         }
      }

      return _strings;
   }
}
