package net.rim.device.api.collection;

public interface CollectionListener {
   void reset(Collection var1);

   void elementAdded(Collection var1, Object var2);

   void elementUpdated(Collection var1, Object var2, Object var3);

   void elementRemoved(Collection var1, Object var2);
}
