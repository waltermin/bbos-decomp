package net.rim.wica.runtime.persistence;

import net.rim.device.api.collection.util.BigIntVector;
import net.rim.device.api.collection.util.BigLongVector;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.util.Persistable;

public class PersDataCollectionStruct implements Persistable, Recryptable {
   private BigIntVector _dataFields;
   private BigLongVector _longData;
   private BigVector _refObjects;
   private int _idSource;

   public BigIntVector getDataFields() {
      return this._dataFields;
   }

   public void setDataFields(BigIntVector fields) {
      this._dataFields = fields;
   }

   public int getIdSource() {
      return this._idSource;
   }

   public void setIdSource(int source) {
      this._idSource = source;
   }

   public BigLongVector getLongData() {
      return this._longData;
   }

   public void setLongData(BigLongVector data) {
      this._longData = data;
   }

   public BigVector getRefObjects() {
      if (this._refObjects == null) {
         return this._refObjects;
      }

      int len = this._refObjects.size();
      BigVector decoded = new BigVector(len);

      for (int i = 0; i < len; i++) {
         decoded.addElement(PersisProtectionHelper.decode(this._refObjects.elementAt(i)));
      }

      return decoded;
   }

   public void setRefObjects(BigVector objects) {
      if (objects != null && PersisProtectionHelper.needCoding()) {
         int len = objects.size();
         this._refObjects = new BigVector(len);

         for (int i = 0; i < len; i++) {
            this._refObjects.addElement(PersisProtectionHelper.encode(objects.elementAt(i)));
         }
      } else {
         this._refObjects = objects;
      }
   }

   @Override
   public void recrypt() {
      if (this._refObjects != null) {
         int len = this._refObjects.size();

         for (int i = 0; i < len; i++) {
            this._refObjects.setElementAt(PersisProtectionHelper.reEncode(this._refObjects.elementAt(i)), i);
         }
      }
   }
}
