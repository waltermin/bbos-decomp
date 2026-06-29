package net.rim.blackberry.api.pdap;

import javax.microedition.pim.PIMItem;

public interface PIMListListener {
   void itemAdded(PIMItem var1);

   void itemUpdated(PIMItem var1, PIMItem var2);

   void itemRemoved(PIMItem var1);
}
