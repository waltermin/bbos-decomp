package net.rim.blackberry.api.pdap;

import java.util.Enumeration;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIMItem;

public interface BlackBerryContactList extends ContactList, BlackBerryPIMList {
   int SORT_ORDER_FIRST_NAME = 0;
   int SORT_ORDER_LAST_NAME = 1;
   int SORT_ORDER_COMPANY = 2;
   int SEARCH_CONTACTS = 1;
   int SEARCH_GROUPS = 2;

   void lookup(String var1, RemoteLookupListener var2);

   void lookup(Contact var1, RemoteLookupListener var2);

   Contact choose(Contact var1, int var2, boolean var3);

   PIMItem choose();

   Enumeration itemsByName(String var1);

   Enumeration itemsByName(Contact var1);

   int getSortOrder();

   Enumeration items(int var1);

   Enumeration items(String var1, int var2);

   Enumeration itemsByName(String var1, int var2);

   void addListener(PIMListListener var1, boolean var2);
}
