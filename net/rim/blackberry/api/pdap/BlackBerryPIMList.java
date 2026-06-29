package net.rim.blackberry.api.pdap;

import javax.microedition.pim.PIMList;

public interface BlackBerryPIMList extends PIMList {
   void addListener(PIMListListener var1);

   void removeListener(PIMListListener var1);

   void setFieldLabel(int var1, String var2);

   boolean isFieldLabelSettable(int var1);
}
