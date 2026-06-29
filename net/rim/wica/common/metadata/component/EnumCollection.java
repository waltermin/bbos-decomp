package net.rim.wica.common.metadata.component;

public interface EnumCollection {
   String[] getEnum(int var1);

   boolean isValidEnumValue(int var1, String var2);

   int getEnumValueAsInt(int var1, String var2);
}
