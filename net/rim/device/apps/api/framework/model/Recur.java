package net.rim.device.apps.api.framework.model;

public interface Recur {
   int MAX_RECURRENCE_PERIOD_DAILY = 999;
   int MAX_RECURRENCE_PERIOD_WEEKLY = 99;
   int MAX_RECURRENCE_PERIOD_MONTHLY = 99;
   int MAX_RECURRENCE_PERIOD_YEARLY = 1;
   byte RECUR_NONE = 0;
   byte RECUR_DAILY = 1;
   byte RECUR_WEEKLY = 2;
   byte RECUR_MONTHLY = 3;
   byte RECUR_YEARLY = 4;
   int MODIFIER_BYDOW = 1;
   int MODIFIER_BYMONTH = 2;
   int MODIFIER_BYMONTHBYDAY = 3;
   int FIRST_MODIFIER = 1;
   int LAST_MODIFIER = 3;

   void setFirstDayOfWeek(int var1);

   int getFirstDayOfWeek();

   void setRecurType(byte var1);

   byte getRecurType();

   void setRecurPeriod(int var1);

   int getRecurPeriod();

   void setAsFinite(boolean var1);

   boolean isFinite();

   void setEndDate(long var1);

   long getEndDate();

   void clearAllModifiers();

   void addModificationValue(int var1, Recur$Modifier var2);

   int numModifierValues(int var1);

   boolean hasModifier(int var1, int var2);

   void getModifierAt(int var1, int var2, Recur$Modifier var3);

   void removeModifier(int var1, int var2);

   long[] getDeletedExclusions();

   void addExclusion(long var1);

   void removeExclusion(long var1);

   void removeAllExclusions();

   int getExclusionCount();

   long getExclusion(int var1);

   long[] getExclusions(long[] var1);

   void addInclusion(long var1);

   void removeInclusion(long var1);

   void removeAllInclusions();

   int getInclusionCount();

   long getInclusion(int var1);

   long[] getInclusions(long[] var1);

   void addChildDate(long var1);

   void removeChildDate(long var1);

   void removeAllChildDates();

   int getChildDateCount();

   long getChildDate(int var1);

   long[] getChildDates(long[] var1);
}
