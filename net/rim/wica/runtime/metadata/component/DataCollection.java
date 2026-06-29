package net.rim.wica.runtime.metadata.component;

import net.rim.wica.common.metadata.component.DataComponentDef;

public interface DataCollection {
   DataComponentDef getDef();

   long create();

   void remove(long var1);

   boolean contains(long var1);

   void save();

   boolean isModified();

   void copyFields(long var1, long var3, boolean var5);

   boolean equals(long var1, long var3);

   boolean equalsField(long var1, int var3, Object var4);

   void setObjectFieldValue(long var1, int var3, Object var4);

   void setIntFieldValue(long var1, int var3, int var4);

   void setBooleanFieldValue(long var1, int var3, boolean var4);

   void setDoubleFieldValue(long var1, int var3, double var4);

   void setLongFieldValue(long var1, int var3, long var4);

   void setReferenceField(long var1, int var3, long var4);

   long getReferenceField(long var1, int var3);

   long getExistingReferenceField(long var1, int var3);

   long getReferenceFieldAsIs(long var1, int var3);

   Object getObjectFieldValue(long var1, int var3);

   int getIntFieldValue(long var1, int var3);

   boolean getBooleanFieldValue(long var1, int var3);

   double getDoubleFieldValue(long var1, int var3);

   long getLongFieldValue(long var1, int var3);

   Object getFieldValueAsObject(long var1, int var3);

   void setFieldValueFromObject(long var1, int var3, Object var4);

   boolean isSupported(String var1);

   void restoreHandle(long var1);

   void addReference(long var1, boolean var3);

   void removeReference(long var1, boolean var3);

   boolean isPersistable(long var1);

   boolean isInnerVectorInitialized(long var1, int var3);
}
