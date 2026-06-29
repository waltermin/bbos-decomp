package net.rim.blackberry.api.pim;

public interface PIMListListener {
   void itemAdded(PIMItem var1);

   void itemUpdated(PIMItem var1, PIMItem var2);

   void itemRemoved(PIMItem var1);
}
