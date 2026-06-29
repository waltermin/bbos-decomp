package net.rim.wica.runtime.persistence.internal;

import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.util.LongIntHashtable;
import net.rim.device.api.util.Persistable;

final class LongSubstore$SubstoreData implements Persistable {
   public BigVector _objects = new BigVector();
   public LongIntHashtable _index = new LongIntHashtable();

   private LongSubstore$SubstoreData() {
   }

   LongSubstore$SubstoreData(LongSubstore$1 x0) {
      this();
   }
}
