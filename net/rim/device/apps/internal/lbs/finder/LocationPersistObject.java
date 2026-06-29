package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.internal.lbs.Location;
import net.rim.device.apps.internal.lbs.LocationSyncable;
import net.rim.device.apps.internal.lbs.content.SingleLocationDocumentConverter;
import net.rim.device.internal.system.InternalServices;

final class LocationPersistObject implements LocationSyncable, Persistable {
   String _label;
   int _latitude;
   int _longitude;
   long _lastAccessed;
   int _uid;
   private byte[] _data;

   final void setDataInternal(String label, byte[] data) {
      this._label = label;
      this._data = data;
   }

   @Override
   public final boolean load(DataBuffer buffer, int version) {
      try {
         if (ConverterUtilities.findType(buffer, 3)) {
            this._data = ConverterUtilities.readByteArray(buffer);
            SingleLocationDocumentConverter conv = SingleLocationDocumentConverter.getInstance();
            Location loc = conv.getLocation(this);
            this._label = loc._label;
            this._latitude = loc._latitude;
            this._longitude = loc._longitude;
            return true;
         }
      } finally {
         return false;
      }

      return false;
   }

   @Override
   public final boolean save(DataBuffer data, int version) {
      ConverterUtilities.writeByteArray(data, 3, this._data);
      return true;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final String getLabel() {
      return this._label;
   }

   @Override
   public final void setData(String label, byte[] data) {
   }

   @Override
   public final byte[] getData() {
      return this._data;
   }

   @Override
   public final SimpleFolder getFolderHeirarchies() {
      return null;
   }

   @Override
   public final int getType() {
      return 3;
   }

   LocationPersistObject(Location loc, byte[] data) {
      this._label = loc._label;
      this._latitude = loc._latitude;
      this._longitude = loc._longitude;
      this._data = data;
      this._uid = loc._uid;
      if (!InternalServices.isDeviceSecure() && this._uid == 0) {
         throw new Object("FinderHistory, LocationPersistObject UID is zero!");
      }

      this._lastAccessed = System.currentTimeMillis();
   }

   LocationPersistObject(int uid) {
      this._uid = uid;
   }

   @Override
   public final boolean equals(Object obj) {
      if (!(obj instanceof Location)) {
         return obj instanceof LocationSyncable ? this._uid == ((LocationSyncable)obj).getUID() : super.equals(obj);
      }

      Location loc = (Location)obj;
      return this._latitude == loc._latitude && this._longitude == loc._longitude && this._label.equals(loc._label);
   }

   @Override
   public final String toString() {
      return this._label;
   }
}
