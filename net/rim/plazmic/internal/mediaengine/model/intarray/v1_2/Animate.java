package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.mediaengine.MediaListener;

public interface Animate extends TimingBehavior {
   int DISCRETE = 0;
   int LINEAR = 1;
   int REPLACE = 0;
   int SUM = 1;
   int NONE = 0;
   int BOOL_ARRAY = 0;
   int INT_ARRAY = 1;
   int OBJECT_ARRAY = 2;

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
