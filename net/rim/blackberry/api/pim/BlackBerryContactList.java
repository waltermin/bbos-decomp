package net.rim.blackberry.api.pim;

public interface BlackBerryContactList extends ContactList {
   void lookup(String var1, RemoteLookupListener var2);

   void lookup(Contact var1, RemoteLookupListener var2);

   Contact choose(Contact var1, int var2, boolean var3);
}
