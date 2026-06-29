package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

public interface Property {
   int BOOL;
   int INT;
   int OBJECT;
   int OBJECT_ARRAY;
   int INT_ARRAY;

   int getDataType();

   void setValue(boolean var1);

   void setValue(int var1);

   void setValue(Object var1);

   boolean getBooleanValue();

   int getIntValue();

   Object getObjectValue();

   int getValuesOffset();

   int getNumValues();

   void getValue(Object var1, int var2);
}
