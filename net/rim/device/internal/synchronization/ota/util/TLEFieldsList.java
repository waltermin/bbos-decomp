package net.rim.device.internal.synchronization.ota.util;

import java.util.Enumeration;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;

public final class TLEFieldsList extends IntHashtable {
   public static final void writeToWithOrder(TLEFieldsList aTLEFieldsList, DataBuffer dout) {
      if (aTLEFieldsList.size() != 0) {
         TLEField[] xTLEFieldsList = new TLEField[256];
         Enumeration xList = aTLEFieldsList.getFields();
         int xMaxTag = 0;

         while (xList.hasMoreElements()) {
            TLEField xTLEField = (TLEField)xList.nextElement();
            int xTag = xTLEField.getTag();
            xTLEFieldsList[xTag] = xTLEField;
            if (xTag > xMaxTag) {
               xMaxTag = xTag;
            }
         }

         for (int xIndex = 0; xIndex <= xMaxTag; xIndex++) {
            TLEField xTLEField = xTLEFieldsList[xIndex];
            if (xTLEField != null) {
               xTLEField.writeTo(dout);
            }
         }
      }
   }

   public TLEFieldsList() {
   }

   public TLEFieldsList(byte[] bytes) {
      try {
         this.readFrom((DataBuffer)(new Object(bytes, 0, bytes.length, true)));
      } finally {
         throw new Object();
      }
   }

   public TLEFieldsList(DataBuffer dins) {
      this();
      this.readFrom(dins);
   }

   public TLEFieldsList(TLEFieldsList aTLEFieldsList) {
      this();
      Enumeration e = aTLEFieldsList.elements();

      while (e.hasMoreElements()) {
         TLEField aTleField = new TLEField((TLEField)e.nextElement());
         this.add(aTleField);
      }
   }

   public final void add(TLEField aTLEField) {
      this.put(aTLEField.getTag(), aTLEField);
   }

   public final boolean containsTag(int aTag) {
      return this.containsKey(aTag);
   }

   public final int[] getTags(int[] anArray) {
      this.keysToArray(anArray);
      return anArray;
   }

   public final void writeTo(DataBuffer dout) {
      if (!this.isEmpty()) {
         Enumeration e = this.elements();

         while (e.hasMoreElements()) {
            TLEField xTLEField = (TLEField)e.nextElement();
            xTLEField.writeTo(dout);
         }
      }
   }

   public final void readFrom(DataBuffer din) {
      while (din.available() > 0) {
         this.add(new TLEField(din));
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final byte[] toByteArray() {
      try {
         return this.toDataBuffer().getArray();
      } catch (Throwable var3) {
         throw new Object(e.getMessage());
      }
   }

   public final DataBuffer toDataBuffer() {
      DataBuffer xDataBuffer = (DataBuffer)(new Object(true));
      this.writeTo(xDataBuffer);
      xDataBuffer.trim();
      xDataBuffer.rewind();
      return xDataBuffer;
   }

   public final TLEField getField(int aTag) {
      return (TLEField)this.get(aTag);
   }

   public final Enumeration getFields() {
      return this.elements();
   }

   public final TLEFieldsList intersectWith(TLEFieldsList bTLEFieldsList, boolean inPlace) {
      TLEFieldsList xTLEFieldsList = !inPlace ? new TLEFieldsList() : this;
      Enumeration xFieldsList = this.elements();

      while (xFieldsList.hasMoreElements()) {
         TLEField aTLEField = (TLEField)xFieldsList.nextElement();
         int xTag = aTLEField.getTag();
         if (bTLEFieldsList.containsKey(xTag)) {
            if (!inPlace) {
               xTLEFieldsList.add(aTLEField);
            }
         } else if (inPlace) {
            this.remove(xTag);
         }
      }

      return xTLEFieldsList;
   }

   public final TLEFieldsList diffWith(TLEFieldsList bTLEFieldsList, boolean inPlace) {
      TLEFieldsList xTLEFieldsList = !inPlace ? new TLEFieldsList(bTLEFieldsList) : bTLEFieldsList;
      IntEnumeration xFieldsTagList = this.keys();

      while (xFieldsTagList.hasMoreElements()) {
         int xTag = xFieldsTagList.nextElement();
         if (!bTLEFieldsList.containsKey(xTag)) {
            TLEField aTLEField = (TLEField)this.get(xTag);
            if (!inPlace) {
               aTLEField = new TLEField(xTag);
            }

            aTLEField.setValue(null);
            xTLEFieldsList.add(aTLEField);
         } else {
            TLEField aTLEField = (TLEField)this.get(xTag);
            TLEField bTLEField = xTLEFieldsList.getField(xTag);
            if (aTLEField.equals(bTLEField)) {
               xTLEFieldsList.remove(xTag);
            }
         }
      }

      return xTLEFieldsList;
   }

   public final TLEFieldsList mergeInto(TLEFieldsList aTLEFieldsList, boolean inPlace) {
      TLEFieldsList xTLEFieldsList = !inPlace ? new TLEFieldsList(this) : this;
      IntEnumeration xFieldsList = aTLEFieldsList.keys();

      while (xFieldsList.hasMoreElements()) {
         int xTag = xFieldsList.nextElement();
         if (!this.containsKey(xTag)) {
            xTLEFieldsList.add(aTLEFieldsList.getField(xTag));
         }
      }

      return xTLEFieldsList;
   }

   public final TLEFieldsList subtractFrom(TLEFieldsList aTLEFieldsList, boolean inPlace) {
      TLEFieldsList xTLEFieldsList = !inPlace ? new TLEFieldsList(aTLEFieldsList) : aTLEFieldsList;
      IntEnumeration xFieldsList = this.keys();

      while (xFieldsList.hasMoreElements()) {
         int xTag = xFieldsList.nextElement();
         if (aTLEFieldsList.containsKey(xTag)) {
            xTLEFieldsList.remove(xTag);
         }
      }

      return xTLEFieldsList;
   }

   public final void reset() {
      this.clear();
   }

   @Override
   public final int hashCode() {
      int xHashCode = 0;
      Enumeration xFieldsList = this.elements();

      while (xFieldsList.hasMoreElements()) {
         TLEField xTLEField = (TLEField)xFieldsList.nextElement();
         xHashCode ^= xTLEField.hashCode();
      }

      return xHashCode;
   }
}
