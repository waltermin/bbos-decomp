package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

public interface Property {
   int BOOL = 0;
   int INT = 1;
   int OBJECT = 2;
   int OBJECT_ARRAY = 3;
   int INT_ARRAY = 4;

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
