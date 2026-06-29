package net.rim.wica.common.metadata.component;

public interface DefaultValueDef {
   boolean hasDefaultValue(int var1);

   Object getObjectDefaultValue(int var1);

   int getIntDefaultValue(int var1);

   boolean getBooleanDefaultValue(int var1);

   double getDoubleDefaultValue(int var1);

   long getLongDefaultValue(int var1);
}
