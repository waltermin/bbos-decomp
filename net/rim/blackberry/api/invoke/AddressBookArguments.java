package net.rim.blackberry.api.invoke;

import javax.microedition.pim.Contact;

public final class AddressBookArguments extends ApplicationArguments {
   private Contact _contact;
   public static final String ARG_COMPOSE = "compose";
   public static final String ARG_NEW = "new";

   public AddressBookArguments() {
   }

   public AddressBookArguments(String arg) {
      if (arg == null || !arg.equals("compose") && !arg.equals("new")) {
         throw new IllegalArgumentException("Invalid argument. Please use one of the AddressBookArguments Class members.");
      }

      super._args = new String[1];
      super._args[0] = arg;
   }

   public AddressBookArguments(String arg, Contact contact) {
      if (arg != null && arg.equals("new") && contact != null) {
         super._args = new String[1];
         super._args[0] = arg;
         this._contact = contact;
      } else {
         throw new IllegalArgumentException("Invalid argument.");
      }
   }

   protected final Contact getContactArg() {
      return this._contact;
   }
}
