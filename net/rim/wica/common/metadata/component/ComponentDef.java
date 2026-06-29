package net.rim.wica.common.metadata.component;

public interface ComponentDef {
   int getId();

   int getFieldHandle(String var1);

   int getAccessType(int var1);

   int getNumFields();

   int getFieldType(int var1);

   int getFieldReferenceType(int var1);

   DefaultValueDef getDefaultValues();

   boolean isPersistable();
}
