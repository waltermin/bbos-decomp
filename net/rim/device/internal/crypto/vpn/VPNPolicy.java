package net.rim.device.internal.crypto.vpn;

import net.rim.device.api.i18n.ResourceBundle;

public interface VPNPolicy {
   int BOOLEAN_TYPE;
   int STRING_TYPE;
   int INT_TYPE;
   int ENUM_TYPE;
   int IP_TYPE;

   ResourceBundle getResourceBundle();

   int resolveIDToResourceBundleIndex(int var1);

   String resolveGatewayTypeName(int var1);

   int[] getGatewayTypeIDs();

   int getPropertyLabel(int var1);

   int getPropertyType(int var1);

   int[] getEnumeratedValues(int var1);

   int[] getRequiredProperties(int var1);

   int[] getAllProperties(int var1);

   void setGateway(int var1);

   int getGateway();

   void setProperty(int var1, Object var2);

   void setProperty(int var1, boolean var2);

   void setProperty(int var1, int var2);

   void clearProperty(int var1);

   boolean isSet(int var1);

   Object getObjectProperty(int var1);

   boolean getBooleanProperty(int var1);

   int getIntegerProperty(int var1);
}
