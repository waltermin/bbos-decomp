package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.api.quickcontact.QuickContactUtil;
import net.rim.device.apps.internal.phone.api.AddressBookDependentObject;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;

public final class HotlistItem
   implements PhoneListItem,
   ConversionProvider,
   KeyProvider,
   DefaultProvider,
   WritableSet,
   ReadableList,
   SyncObject,
   EncryptableProvider {
   private PersistableRIMModel _address;
   private int _hitCount = 1;
   private long _timeOfLastCall;
   private long _atbc;
   private long _sortValue;
   protected int _uid;
   protected char _speedDialKey;
   private static final long DEFAULT_ATBC = 86400000L;
   private static final byte[] phoneCallRecordId = new byte[]{104};
   private static ContextObjectWR _hotlistSyncContextWR = (ContextObjectWR)(new Object(20, 19));

   public final boolean addressBookUpdated(int updateType, Object o) {
      return !(this._address instanceof AddressBookDependentObject) ? false : ((AddressBookDependentObject)this._address).addressBookUpdated(updateType, o);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      Verb defaultVerb = null;
      ContextObject.setFlag(context, 34);
      PhoneUtilities.setPrivateFlag(context, 9);
      PhoneUtilities.setPrivateFlag(context, 38);
      if (this._address instanceof Object) {
         VerbProvider verbProvider = (VerbProvider)this._address;
         defaultVerb = verbProvider.getVerbs(context, verbs);
      }

      return defaultVerb;
   }

   public final void setLastCallTime(long time) {
      this._timeOfLastCall = time;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   public final long getTimeSinceLastCall(long currentTime) {
      return currentTime == this._timeOfLastCall ? 1 : currentTime - this._timeOfLastCall;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 20) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         syncBuffer.addBytes(1, phoneCallRecordId);
         syncBuffer.addInt(2, this._hitCount, 4);
         syncBuffer.addLong(3, this._timeOfLastCall);
         syncBuffer.addLong(4, this._atbc);
         syncBuffer.addLong(5, this._sortValue);
         syncBuffer.addInt(6, this._uid, 4);
         return syncBuffer.addSubmembers(this, _hotlistSyncContextWR.getContextObject());
      } else {
         return true;
      }
   }

   public final void updateATBC(long callEventTime) {
      this._atbc = (this._atbc * this._hitCount + this.getTimeSinceLastCall(callEventTime)) / (this._hitCount + 1);
   }

   public final long getATBC() {
      return this._atbc;
   }

   public final void setATBC(long atbc) {
      this._atbc = atbc;
   }

   public final void setSortValue(long value) {
      this._sortValue = value;
   }

   public final long getFrequencySortValue() {
      return this._sortValue;
   }

   public final RIMModel getAddress() {
      return this._address;
   }

   public final long getLastCallTime() {
      return this._timeOfLastCall;
   }

   public final Object getNumber() {
      return !(this._address instanceof CallerIDInfo) ? null : ((CallerIDInfo)this._address).getNumber();
   }

   final boolean hasAddressCard() {
      if (!(this._address instanceof CallerIDInfo)) {
         return false;
      }

      CallerIDInfo info = (CallerIDInfo)this._address;
      return info.getAddress() != null;
   }

   public final void setAddress(Object address) {
      this._address = (PersistableRIMModel)address;
      if (this._address instanceof Object) {
         EncryptableProvider encryptable = (EncryptableProvider)this._address;
         if (!encryptable.checkCrypt(true, true)) {
            Object newObject = encryptable.reCrypt(true, true);
            if (newObject instanceof Object) {
               this._address = (PersistableRIMModel)newObject;
            }
         }
      }
   }

   public final int getHitCount() {
      return this._hitCount;
   }

   public final void setHitCount(int count) {
      this._hitCount = count;
   }

   public final void incrementHitCount(long callEventTime) {
      this._hitCount++;
   }

   @Override
   public final CallerIDInfo getCallerIDInfo() {
      return this._address instanceof CallerIDInfo ? (CallerIDInfo)this.getAddress() : null;
   }

   @Override
   public final boolean isLongRunningDelete() {
      return false;
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      if (!(this._address instanceof Object)) {
         return 0;
      }

      KeyProvider keyProvider = (KeyProvider)this._address;
      return keyProvider.getKeys(context, keyArray, index, keyRequested);
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      if (!(this._address instanceof Object)) {
         return 0;
      }

      KeyProvider keyProvider = (KeyProvider)this._address;
      return keyProvider.getKeys(context, keyArray, index, keyRequested);
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      if (!(this._address instanceof Object)) {
         return 0;
      }

      KeyProvider keyProvider = (KeyProvider)this._address;
      return keyProvider.getKeys(context, keyArray, index, keyRequested);
   }

   @Override
   public final Object getDefault(Object current, Object context) {
      RIMModel address = this.getAddress();
      if (!(address instanceof Object)) {
         return null;
      }

      DefaultProvider defaultProvider = (DefaultProvider)address;
      return defaultProvider.getDefault(current, context);
   }

   @Override
   public final Object updateDefault(Object newdefault, Object context) {
      return null;
   }

   @Override
   public final char getSpeedDialKey() {
      Object number = ((CallerIDInfo)this.getAddress()).getNumber();
      return QuickContactList.getInstance().getQuickContactKey(number);
   }

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context, PhoneListView phoneListView, int index) {
      width -= QuickContactUtil.paintHotkey(this.getSpeedDialKey(), g, x, y, width, height, 5, context);
      if (this._address instanceof Object) {
         Font font = g.getFont();
         if (height > font.getHeight()) {
            y += height - font.getHeight() >> 1;
         }

         PaintProvider paintProvider = (PaintProvider)this._address;
         paintProvider.paint(g, 0, y, width, height, context);
      }

      return width;
   }

   @Override
   public final Field getHintField() {
      return null;
   }

   @Override
   public final boolean canSpeedDial() {
      Object num = this.getNumber();
      return !(num instanceof Object) ? false : ((AbstractPhoneNumberModel)num).canSpeedDial();
   }

   @Override
   public final void add(Object submember) {
      if (submember instanceof CallerIDInfo) {
         this._address = (PersistableRIMModel)submember;
      }
   }

   @Override
   public final boolean contains(Object element) {
      return false;
   }

   @Override
   public final void remove(Object submember) {
   }

   @Override
   public final void removeAll() {
   }

   @Override
   public final int size() {
      return 1;
   }

   @Override
   public final Object getAt(int index) {
      return this._address;
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return 0;
   }

   @Override
   public final int getIndex(Object element) {
      return 0;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return !(this._address instanceof Object) ? true : ((EncryptableProvider)this._address).checkCrypt(compress, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      if (this._address instanceof Object) {
         ((EncryptableProvider)this._address).reCrypt(compress, encrypt);
      }

      return null;
   }

   public HotlistItem(Object address, long creationTime, Object context) {
      this._timeOfLastCall = creationTime;
      this._atbc = 86400000;
      this._address = (PersistableRIMModel)address;
      this._uid = UIDGenerator.getUID();
   }

   @Override
   public final String toString() {
      if (!(this._address instanceof CallerIDInfo)) {
         return this._address instanceof SpecialAddressCard ? this._address.toString() : null;
      } else {
         return ((CallerIDInfo)this._address).getDisplayString();
      }
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof HotlistItem)) {
         return false;
      }

      HotlistItem item = (HotlistItem)o;
      CallerIDInfo thisAddress = (CallerIDInfo)this._address;
      CallerIDInfo thatAddress = (CallerIDInfo)item.getAddress();
      return CallerIDInfo.callerIDEqual(thisAddress, thatAddress);
   }

   public HotlistItem(Object address) {
      this._timeOfLastCall = 0;
      this._address = (PersistableRIMModel)address;
      this._uid = UIDGenerator.getUID();
   }

   public HotlistItem() {
      this._timeOfLastCall = 0;
      this._address = null;
      this._uid = 0;
   }
}
