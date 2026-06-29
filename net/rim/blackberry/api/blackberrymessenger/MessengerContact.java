package net.rim.blackberry.api.blackberrymessenger;

import net.rim.blackberry.api.pdap.BlackBerryContact;

public interface MessengerContact {
   int getContactId();

   String getDisplayName();

   BlackBerryContact getBlackBerryContact();

   Session getSession();
}
