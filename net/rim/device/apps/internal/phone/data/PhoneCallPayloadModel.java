package net.rim.device.apps.internal.phone.data;

import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.EditableProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;

public class PhoneCallPayloadModel implements PersistableRIMModel, EditableProvider, ReadableList, WritableSet, EncryptableProvider {
   private Vector _submembers;
   long _timeStamp;
   int _elapsedTime;
   byte _type = 0;
   public static final int TIMESTAMP_INDEX = 0;
   public static final int DURATION_INDEX = 1;
   public static final int MEMO_INDEX = 2;
   public static final int CALLER_ID_INDEX = 3;
   private static final int MIN_SUBMEMBERS = 2;
   private static final int NON_RIMMODEL_SUBMEMBER_COUNT = 2;
   private static TimeModel _timeStampModel = new TimeModel();
   private static TimeModel _durationModel = new TimeModel();

   public PhoneCallPayloadModel() {
      this._timeStamp = 0;
      this._elapsedTime = 0;
      this._submembers = (Vector)(new Object(2));
   }

   public PhoneCallPayloadModel(PhoneCallInitialData data) {
      this._timeStamp = data._timestamp;
      this._submembers = (Vector)(new Object());
      this.createMemo(data._context);
      if (data._callerIDInfo != null) {
         this.addCallerIDInfo(data._callerIDInfo);
      }
   }

   private void createMemo(Object context) {
      String initialText = (String)ContextObject.get(context, -3212039960712815826L);
      if (initialText == null) {
         initialText = "";
      }

      ContextObject memoContext = ContextObject.castOrCreate(context);
      Object oldBodyText = memoContext.get(-8478555129720928586L);
      memoContext.put(-8478555129720928586L, initialText);
      Object memo = FactoryUtil.createInstance(2096811533660483L, memoContext);
      if (oldBodyText != null) {
         memoContext.put(-8478555129720928586L, oldBodyText);
      }

      if (memo != null) {
         this.add(memo);
      }
   }

   public void addCallerIDInfo(Object callerIDInfo) {
      if (callerIDInfo instanceof CallerIDInfo) {
         this.add(new CallerIDInfo((CallerIDInfo)callerIDInfo, true));
      }
   }

   public void updateCallerIDInfo(Object callerIDInfo) {
      if (callerIDInfo instanceof CallerIDInfo) {
         int size = this._submembers.size();

         for (int i = 0; i < size; i++) {
            Object sm = this._submembers.elementAt(i);
            if (sm instanceof CallerIDInfo) {
               this._submembers.removeElementAt(i);
               this._submembers.insertElementAt(callerIDInfo, i);
               return;
            }
         }
      }
   }

   void setType(byte type) {
      this._type = type;
   }

   public int getElapsedTime() {
      return this._elapsedTime;
   }

   public boolean setElapsedTime(int elapsedTime) {
      if (this._elapsedTime == elapsedTime) {
         return false;
      }

      this._elapsedTime = elapsedTime;
      return true;
   }

   public long getTimeStamp() {
      return this._timeStamp;
   }

   public boolean setTimeStamp(long timeStamp) {
      if (this._timeStamp == timeStamp) {
         return false;
      }

      this._timeStamp = timeStamp;
      return true;
   }

   private boolean isMemoObject(Object o) {
      return o instanceof Object;
   }

   private TimeModel getTimeModel(long time, int format) {
      if (format == 1) {
         _timeStampModel.setTime(time);
         _timeStampModel.setFormat(format);
         return _timeStampModel;
      } else if (format == 2) {
         _durationModel.setTime(time);
         _durationModel.setFormat(format);
         return _durationModel;
      } else {
         return null;
      }
   }

   private Object getConferenceCallSubmember(int index) {
      Object obj = null;
      int indexAdjustedForNonRIMModels = index - 2;
      switch (index) {
         case -1:
            return this._submembers.elementAt(indexAdjustedForNonRIMModels);
         case 0:
         default:
            return this.getTimeModel(this._timeStamp, 1);
         case 1:
            return this.getTimeModel(this._elapsedTime, 2);
      }
   }

   @Override
   public Object getAt(int index) {
      if (this._type == 4) {
         return this.getConferenceCallSubmember(index);
      }

      Object obj = null;
      int indexAdjustedForNonRIMModels = index - 2;
      switch (index) {
         case -1:
            if (indexAdjustedForNonRIMModels >= 0 && indexAdjustedForNonRIMModels < this._submembers.size()) {
               obj = this._submembers.elementAt(indexAdjustedForNonRIMModels);
            }

            return obj;
         case 0:
         default:
            return this.getTimeModel(this._timeStamp, 1);
         case 1:
            return this.getTimeModel(this._elapsedTime, 2);
      }
   }

   @Override
   public int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public int getIndex(Object element) {
      return ReadableListUtil.getIndex(element, this);
   }

   @Override
   public int size() {
      return this._submembers.size() + 2;
   }

   @Override
   public void add(Object submember) {
      if (this.isMemoObject(submember)) {
         this._submembers.insertElementAt(submember, 0);
      } else {
         this._submembers.addElement(submember);
      }
   }

   @Override
   public boolean contains(Object element) {
      return false;
   }

   @Override
   public void remove(Object submember) {
      this._submembers.removeElement(submember);
   }

   @Override
   public void removeAll() {
   }

   @Override
   public Object makeReadOnly() {
      ObjectGroup.createGroupIgnoreTooBig(this);
      return this;
   }

   @Override
   public Object makeReadWrite() {
      return ObjectGroup.isInGroup(this) ? ObjectGroup.expandGroup(this) : this;
   }

   @Override
   public boolean isReadOnly() {
      return ObjectGroup.isInGroup(this);
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      int numSubmembers = this._submembers.size();

      for (int i = 0; i < numSubmembers; i++) {
         Object object = this._submembers.elementAt(i);
         if (object instanceof Object) {
            EncryptableProvider encryptable = (EncryptableProvider)object;
            if (!encryptable.checkCrypt(compress, encrypt)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      PhoneCallPayloadModel newModel = (PhoneCallPayloadModel)this.makeReadWrite();
      int numSubmembers = newModel._submembers.size();

      for (int i = 0; i < numSubmembers; i++) {
         Object object = newModel._submembers.elementAt(i);
         if (object instanceof Object) {
            EncryptableProvider encryptable = (EncryptableProvider)object;
            Object newObject = encryptable.reCrypt(compress, encrypt);
            if (newObject != null) {
               newModel._submembers.setElementAt(newObject, i);
            }
         }
      }

      return newModel.makeReadOnly();
   }
}
