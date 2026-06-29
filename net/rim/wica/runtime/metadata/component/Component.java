package net.rim.wica.runtime.metadata.component;

import net.rim.wica.common.metadata.component.ComponentDef;

public interface Component {
   ComponentDef getDef();

   Object getFieldValueAsObject(int var1);

   void setFieldValueFromObject(int var1, Object var2);

   void setObjectFieldValue(int var1, Object var2);

   void setIntFieldValue(int var1, int var2);

   void setBooleanFieldValue(int var1, boolean var2);

   void setDoubleFieldValue(int var1, double var2);

   void setLongFieldValue(int var1, long var2);

   void setReferenceField(int var1, long var2);

   long getReferenceField(int var1);

   long getExistingReferenceField(int var1);

   long getReferenceFieldAsIs(int var1);

   Object getObjectFieldValue(int var1);

   int getIntFieldValue(int var1);

   boolean getBooleanFieldValue(int var1);

   long getLongFieldValue(int var1);

   double getDoubleFieldValue(int var1);
}
