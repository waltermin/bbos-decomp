package net.rim.blackberry.api.pim;

import java.util.Enumeration;

public interface PIMList {
   String UNCATEGORIZED;

   String getName();

   void close();

   Enumeration items();

   Enumeration items(PIMItem var1);

   Enumeration items(String var1);

   String[] getCategories();

   boolean isCategory(String var1);

   void addCategory(String var1);

   void deleteCategory(String var1, boolean var2);

   int maxCategories();

   boolean isSupportedField(int var1);

   int[] getSupportedFields();

   String getFieldLabel(int var1);

   String getArrayElementLabel(int var1, int var2);

   String getAttributeLabel(int var1);

   int getFieldDataType(int var1);

   boolean isSupportedAttribute(int var1, int var2);

   int[] getSupportedAttributes(int var1);

   int maxValues(int var1);

   int[] getSupportedArrayElements(int var1);

   boolean isSupportedArrayElement(int var1, int var2);

   Enumeration itemsByCategory(String var1);

   void renameCategory(String var1, String var2);

   int stringArraySize(int var1);
}
