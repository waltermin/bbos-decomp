package net.rim.wica.runtime.persistence.internal.backup;

import net.rim.device.api.collection.util.BigVector;

final class LongBigVectorPair {
   private long _long;
   private BigVector _bigVector;

   LongBigVectorPair() {
   }

   LongBigVectorPair(long longValue, BigVector bigVector) {
      this._long = longValue;
      this._bigVector = bigVector;
   }

   final void setBigVector(BigVector bigVector) {
      this._bigVector = bigVector;
   }

   final void setLong(long longValue) {
      this._long = longValue;
   }

   final long getLong() {
      return this._long;
   }

   final BigVector getBigVector() {
      return this._bigVector;
   }
}
