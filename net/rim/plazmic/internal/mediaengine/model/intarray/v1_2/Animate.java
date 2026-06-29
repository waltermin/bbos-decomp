package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.mediaengine.MediaListener;

public interface Animate extends TimingBehavior {
   int DISCRETE;
   int LINEAR;
   int REPLACE;
   int SUM;
   int NONE;
   int BOOL_ARRAY;
   int INT_ARRAY;
   int OBJECT_ARRAY;

   int[] getKeyTimes();

   int getKeyTimesOffset();

   int getNumKeyTimes();

   void getKeyTimes(int[] var1, int var2);

   Object getKeyValues();

   int getKeyValueDataType();

   int getKeyValuesOffset();

   int getNumKeyValues();

   int getKeyValueSize();

   void getKeyValues(Object var1, int var2);

   int getCalcMode();

   void setCalcMode(int var1);

   int getAdditive();

   void setAdditive(int var1);

   int getAccumulate();

   void setAccumulate(int var1);

   Property getTargetProperty();

   void addEventListener(int var1, MediaListener var2);

   void removeEventListener(int var1, MediaListener var2);
}
