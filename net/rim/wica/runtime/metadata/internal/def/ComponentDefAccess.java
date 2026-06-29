package net.rim.wica.runtime.metadata.internal.def;

public interface ComponentDefAccess {
   boolean hasDefinition(int var1);

   int getFieldHandle(int var1, String var2);

   int getNumFields(int var1);

   int getFieldType(int var1, int var2);

   boolean hasDefaultValue(int var1, int var2);

   Object getObjectDefaultValue(int var1, int var2);

   int getIntDefaultValue(int var1, int var2);

   boolean getBooleanDefaultValue(int var1, int var2);

   long getLongDefaultValue(int var1, int var2);

   double getDoubleDefaultValue(int var1, int var2);

   int getFieldReferenceType(int var1, int var2);
}
