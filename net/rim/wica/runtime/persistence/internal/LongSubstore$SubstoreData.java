package net.rim.wica.runtime.persistence.internal;

import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.util.LongIntHashtable;
import net.rim.device.api.util.Persistable;

final class LongSubstore$SubstoreData implements Persistable {
   public BigVector _objects = (BigVector)(new Object());
   public LongIntHashtable _index = (LongIntHashtable)(new Object());

   private LongSubstore$SubstoreData() {
   }

   LongSubstore$SubstoreData(LongSubstore$1 x0) {
      this();
   }
}
