package net.rim.device.apps.internal.commonmodels.pim;

import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Modifier;
import net.rim.vm.Array;

public class RecurImpl implements Recur, Persistable, FieldProvider, CloneProvider {
   private byte _recurType = 0;
   private int _period = 1;
   private boolean _finite;
   private long _end;
   IntHashtable _modifiers;
   private long[] _exclusions;
   private long[] _inclusions;
   private long[] _childDates;
   private int _firstDayOfWeek = 1;

   public boolean compareModifiers(RecurImpl otherRecur) {
      int otherSize = otherRecur._modifiers != null ? otherRecur._modifiers.size() : 0;
      int size = this._modifiers != null ? this._modifiers.size() : 0;
      boolean result = otherSize == size;
      if (result && size > 0) {
         IntEnumeration keys = this._modifiers.keys();

         while (keys.hasMoreElements() && result) {
            int key = keys.nextElement();
            Vector vectorOfMods = (Vector)this._modifiers.get(key);
            Vector otherVectorOfMods = (Vector)otherRecur._modifiers.get(key);
            int otherModSize = vectorOfMods != null ? vectorOfMods.size() : 0;
            int modSize = otherVectorOfMods != null ? otherVectorOfMods.size() : 0;
            result = modSize == otherModSize;

            for (int index = 0; index < vectorOfMods.size() && result; index++) {
               Recur$Modifier mod = (Recur$Modifier)vectorOfMods.elementAt(index);
               Recur$Modifier otherMod = (Recur$Modifier)otherVectorOfMods.elementAt(index);
               result = result && mod.parm1 == otherMod.parm1 && mod.parm2 == otherMod.parm2;
            }
         }
      }

      return result;
   }

   @Override
   public int getFirstDayOfWeek() {
      return this._firstDayOfWeek;
   }

   @Override
   public void setRecurType(byte recurType) {
      this._recurType = recurType;
      if (this._recurType < 0 || this._recurType > 4) {
         this._recurType = 0;
      }

      if (this._recurType == 0) {
         this._modifiers = null;
      }
   }

   @Override
   public byte getRecurType() {
      return this._recurType;
   }

   @Override
   public void setRecurPeriod(int period) {
      this._period = period;
      if (this._period < 1) {
         this._period = 1;
      }
   }

   @Override
   public int getRecurPeriod() {
      return this._period;
   }

   @Override
   public Object clone(Object context) {
      return this.copy(null);
   }

   @Override
   public boolean isFinite() {
      return this._finite;
   }

   @Override
   public void setEndDate(long endBy) {
      this.setAsFinite(true);
      this._end = endBy;
   }

   @Override
   public long getEndDate() {
      return this._end;
   }

   @Override
   public void clearAllModifiers() {
      this._modifiers = null;
   }

   @Override
   public void addModificationValue(int modifierID, Recur$Modifier modifier) {
      Vector vectorOfMods = null;
      Recur$Modifier modToAdd = new Recur$Modifier();
      if (this._modifiers == null) {
         this._modifiers = new IntHashtable();
      }

      vectorOfMods = (Vector)this._modifiers.get(modifierID);
      if (vectorOfMods == null) {
         vectorOfMods = new Vector();
         this._modifiers.put(modifierID, vectorOfMods);
      }

      modToAdd.parm1 = modifier.parm1;
      modToAdd.parm2 = modifier.parm2;
      vectorOfMods.addElement(modToAdd);
   }

   @Override
   public int numModifierValues(int modifierID) {
      Vector vectorOfMods = null;
      if (this._modifiers == null) {
         return 0;
      }

      vectorOfMods = (Vector)this._modifiers.get(modifierID);
      return vectorOfMods == null ? 0 : vectorOfMods.size();
   }

   @Override
   public boolean hasModifier(int modifierID, int offset) {
      Vector vectorOfMods = null;
      if (this._modifiers == null) {
         return false;
      }

      vectorOfMods = (Vector)this._modifiers.get(modifierID);
      return vectorOfMods != null && offset < vectorOfMods.size();
   }

   @Override
   public void getModifierAt(int modifierID, int offset, Recur$Modifier modifier) {
      Vector vectorOfMods = null;
      if (this._modifiers != null) {
         vectorOfMods = (Vector)this._modifiers.get(modifierID);
         if (vectorOfMods != null && offset < vectorOfMods.size()) {
            Recur$Modifier temp = (Recur$Modifier)vectorOfMods.elementAt(offset);
            modifier.parm1 = temp.parm1;
            modifier.parm2 = temp.parm2;
         }
      }
   }

   @Override
   public void removeModifier(int modifierID, int offset) {
      Vector vectorOfMods = null;
      if (this._modifiers != null) {
         vectorOfMods = (Vector)this._modifiers.get(modifierID);
         if (vectorOfMods != null && offset < vectorOfMods.size()) {
            vectorOfMods.removeElementAt(offset);
            if (vectorOfMods.size() <= 0) {
               this._modifiers.remove(modifierID);
            }

            if (this._modifiers.size() <= 0) {
               this._modifiers = null;
            }
         }
      }
   }

   @Override
   public void setFirstDayOfWeek(int fdow) {
      if (fdow < 1) {
         this._firstDayOfWeek = 1;
      } else if (fdow > 7) {
         this._firstDayOfWeek = 7;
      } else {
         this._firstDayOfWeek = fdow;
      }
   }

   @Override
   public void setAsFinite(boolean finite) {
      this._finite = finite;
   }

   @Override
   public void addExclusion(long timeOfExclusion) {
      int offsetToInsert = 0;
      if (this._exclusions == null) {
         this._exclusions = new long[1];
         this._exclusions[0] = timeOfExclusion;
      } else {
         offsetToInsert = Arrays.binarySearch(this._exclusions, timeOfExclusion, 0, this._exclusions.length);
         if (offsetToInsert < 0) {
            offsetToInsert = -offsetToInsert - 1;
            Array.resize(this._exclusions, this._exclusions.length + 1);
            System.arraycopy(this._exclusions, offsetToInsert, this._exclusions, offsetToInsert + 1, this._exclusions.length - offsetToInsert - 1);
            this._exclusions[offsetToInsert] = timeOfExclusion;
         }
      }
   }

   @Override
   public void removeExclusion(long timeOfExclusion) {
      long[] local = this._exclusions;
      int offsetToRemove = Arrays.binarySearch(this._exclusions, timeOfExclusion, 0, this._exclusions.length);
      if (offsetToRemove >= 0) {
         if (offsetToRemove + 1 < local.length) {
            System.arraycopy(local, offsetToRemove + 1, local, offsetToRemove, local.length - (offsetToRemove + 1));
         }

         Array.resize(local, local.length - 1);
         if (local.length <= 0) {
            this._exclusions = null;
         }
      }
   }

   @Override
   public int getExclusionCount() {
      int count = 0;
      if (this._exclusions != null) {
         count = this._exclusions.length;
      }

      return count;
   }

   @Override
   public long getExclusion(int i) {
      return this._exclusions != null && this._exclusions.length != 0 ? this._exclusions[i] : -1;
   }

   @Override
   public long[] getExclusions(long[] excludedDates) {
      long[] local = this._exclusions;
      if (this._exclusions != null) {
         if (excludedDates == null) {
            excludedDates = new long[local.length];
         } else {
            Array.resize(excludedDates, local.length);
         }

         System.arraycopy(local, 0, excludedDates, 0, local.length);
         return excludedDates;
      } else {
         if (excludedDates == null) {
            return new long[0];
         }

         Array.resize(excludedDates, 0);
         return excludedDates;
      }
   }

   @Override
   public void removeAllExclusions() {
      this._exclusions = null;
   }

   @Override
   public void addInclusion(long inclusionTime) {
      int offsetToInsert = 0;
      if (this._inclusions == null) {
         this._inclusions = new long[1];
         this._inclusions[0] = inclusionTime;
      } else {
         offsetToInsert = Arrays.binarySearch(this._inclusions, inclusionTime, 0, this._inclusions.length);
         if (offsetToInsert < 0) {
            offsetToInsert = -offsetToInsert - 1;
            Array.resize(this._inclusions, this._inclusions.length + 1);
            System.arraycopy(this._inclusions, offsetToInsert, this._inclusions, offsetToInsert + 1, this._inclusions.length - offsetToInsert - 1);
            this._inclusions[offsetToInsert] = inclusionTime;
         }
      }
   }

   @Override
   public void removeInclusion(long inclusionTime) {
      long[] local = this._inclusions;
      int offsetToRemove = Arrays.binarySearch(this._inclusions, inclusionTime, 0, this._inclusions.length);
      if (offsetToRemove >= 0) {
         if (offsetToRemove + 1 < local.length) {
            System.arraycopy(local, offsetToRemove + 1, local, offsetToRemove, local.length - (offsetToRemove + 1));
         }

         Array.resize(local, local.length - 1);
         if (local.length <= 0) {
            this._inclusions = null;
         }
      }
   }

   @Override
   public long[] getInclusions(long[] includedDates) {
      long[] local = this._inclusions;
      if (this._inclusions != null) {
         if (includedDates == null) {
            includedDates = new long[local.length];
         } else {
            Array.resize(includedDates, local.length);
         }

         System.arraycopy(local, 0, includedDates, 0, local.length);
         return includedDates;
      } else {
         if (includedDates == null) {
            return new long[0];
         }

         Array.resize(includedDates, 0);
         return includedDates;
      }
   }

   @Override
   public void removeAllInclusions() {
      this._inclusions = null;
   }

   @Override
   public int getInclusionCount() {
      return this._inclusions == null ? 0 : this._inclusions.length;
   }

   @Override
   public long getInclusion(int i) {
      return this._inclusions != null && this._inclusions.length != 0 ? this._inclusions[i] : -1;
   }

   @Override
   public void addChildDate(long timeOfChild) {
      int offsetToInsert = 0;
      if (this._childDates == null) {
         this._childDates = new long[1];
         this._childDates[0] = timeOfChild;
      } else {
         offsetToInsert = Arrays.binarySearch(this._childDates, timeOfChild, 0, this._childDates.length);
         if (offsetToInsert < 0) {
            offsetToInsert = -offsetToInsert - 1;
            Array.resize(this._childDates, this._childDates.length + 1);
            System.arraycopy(this._childDates, offsetToInsert, this._childDates, offsetToInsert + 1, this._childDates.length - offsetToInsert - 1);
            this._childDates[offsetToInsert] = timeOfChild;
         }
      }
   }

   @Override
   public void removeChildDate(long timeOfChild) {
      long[] local = this._childDates;
      int offsetToRemove = Arrays.binarySearch(this._childDates, timeOfChild, 0, this._childDates.length);
      if (offsetToRemove >= 0) {
         if (offsetToRemove + 1 < local.length) {
            System.arraycopy(local, offsetToRemove + 1, local, offsetToRemove, local.length - (offsetToRemove + 1));
         }

         Array.resize(local, local.length - 1);
         if (local.length <= 0) {
            this._childDates = null;
         }
      }
   }

   @Override
   public int getChildDateCount() {
      int count = 0;
      if (this._childDates != null) {
         count = this._childDates.length;
      }

      return count;
   }

   @Override
   public long getChildDate(int i) {
      return this._childDates != null && this._childDates.length != 0 ? this._childDates[i] : -1;
   }

   @Override
   public long[] getChildDates(long[] childDates) {
      long[] local = this._childDates;
      if (this._childDates != null) {
         if (childDates == null) {
            childDates = new long[local.length];
         } else {
            Array.resize(childDates, local.length);
         }

         System.arraycopy(local, 0, childDates, 0, local.length);
         return childDates;
      } else {
         if (childDates == null) {
            return new long[0];
         }

         Array.resize(childDates, 0);
         return childDates;
      }
   }

   @Override
   public void removeAllChildDates() {
      this._childDates = null;
   }

   @Override
   public long[] getDeletedExclusions() {
      long[] deletedExclusions = new long[0];
      int exclusionCount = this._exclusions != null ? this._exclusions.length : 0;
      int childDatesCount = this._childDates != null ? this._childDates.length : 0;

      for (int i = 0; i < exclusionCount; i++) {
         if (Arrays.binarySearch(this._childDates, this._exclusions[i], 0, childDatesCount) < 0) {
            Array.resize(deletedExclusions, deletedExclusions.length + 1);
            deletedExclusions[deletedExclusions.length - 1] = this._exclusions[i];
         }
      }

      return deletedExclusions;
   }

   @Override
   public int getOrder(Object context) {
      return 0;
   }

   @Override
   public Field getField(Object context) {
      ContextObject co = null;
      if (context instanceof ContextObject) {
         co = (ContextObject)context;
      }

      return new RecurrenceField((Recur)this.clone(null), co);
   }

   @Override
   public boolean grabDataFromField(Field field, Object context) {
      if (field instanceof RecurrenceField) {
         RecurrenceField recurField = (RecurrenceField)field;
         RecurImpl copy = (RecurImpl)recurField.getRecurrenceInfo();
         this.copy(copy);
      }

      return true;
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   private Object copy(RecurImpl copy) {
      Recur$Modifier mod = new Recur$Modifier();
      RecurImpl temp;
      if (copy == null) {
         temp = new RecurImpl();
         copy = this;
      } else {
         temp = this;
      }

      temp._recurType = copy._recurType;
      temp._period = copy._period;
      temp._finite = copy._finite;
      temp._end = copy._end;
      temp._modifiers = new IntHashtable();

      for (int j = 1; j <= 3; j++) {
         int numMods = copy.numModifierValues(j);

         for (int i = 0; i < numMods; i++) {
            copy.getModifierAt(j, i, mod);
            temp.addModificationValue(j, mod);
         }
      }

      temp._exclusions = null;
      if (copy._exclusions != null) {
         temp._exclusions = new long[copy._exclusions.length];
         System.arraycopy(copy._exclusions, 0, temp._exclusions, 0, copy._exclusions.length);
      }

      temp._inclusions = null;
      if (copy._inclusions != null) {
         temp._inclusions = new long[copy._inclusions.length];
         System.arraycopy(copy._inclusions, 0, temp._inclusions, 0, copy._inclusions.length);
      }

      temp._childDates = null;
      if (copy._childDates != null) {
         temp._childDates = new long[copy._childDates.length];
         System.arraycopy(copy._childDates, 0, temp._childDates, 0, copy._childDates.length);
      }

      temp._firstDayOfWeek = copy._firstDayOfWeek;
      return temp;
   }
}
