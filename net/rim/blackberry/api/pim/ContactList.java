package net.rim.blackberry.api.pim;

public interface ContactList extends PIMList {
   Contact createContact();

   Contact importContact(Contact var1);

   void removeContact(Contact var1);
}
