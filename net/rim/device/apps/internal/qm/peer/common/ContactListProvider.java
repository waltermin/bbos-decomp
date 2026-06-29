package net.rim.device.apps.internal.qm.peer.common;

import java.util.Enumeration;

public interface ContactListProvider {
   int getContactsCount();

   Contact getContactAt(int var1);

   int getRequestsCount();

   Request getRequestAt(int var1);

   Enumeration getContactLists();
}
