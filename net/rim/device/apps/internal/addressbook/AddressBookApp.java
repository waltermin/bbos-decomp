package net.rim.device.apps.internal.addressbook;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.search.GlobalSearchRegistry;
import net.rim.device.apps.internal.addressbook.ui.PackageManager;

public final class AddressBookApp extends UiApplication {
   private static final long ADDRESS_BOOK_SEARCHABLE_ID;

   private AddressBookApp(String arg) {
      this.invokeLater(new AddressBookApp$1(this, arg));
   }

   public static final void main(String[] args) {
      if (args != null && args.length == 1) {
         if (args[0].equals("init")) {
            Application.setAcceptEventsForProcess(false);
            PackageManager.registerOnceOnSystemStart();
            net.rim.device.apps.internal.addressbook.addresscard.PackageManager.registerOnceOnSystemStart();
            net.rim.device.apps.internal.addressbook.mailingaddress.PackageManager.registerOnceOnSystemStart();
            net.rim.device.apps.internal.addressbook.userfields.PackageManager.registerOnceOnSystemStart();
            net.rim.device.apps.internal.addressbook.eventmodel.PackageManager.registerOnceOnSystemStart();
            BlackBerryAddressBook.getAddressBook();
            net.rim.device.apps.internal.addressbook.lookup.PackageManager.registerOnceOnSystemStart();
            GlobalSearchRegistry.register(-992593092633980135L, new AddressBookSearchable());
            return;
         }

         if (args[0].equals("compose")) {
            new AddressBookApp(args[0]).enterEventDispatcher();
            return;
         }
      } else {
         new AddressBookApp(null).enterEventDispatcher();
      }
   }
}
