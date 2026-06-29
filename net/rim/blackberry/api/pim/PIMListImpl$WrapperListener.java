package net.rim.blackberry.api.pim;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;

class PIMListImpl$WrapperListener implements CollectionListener {
   private PIMListListener _listener;
   private final PIMListImpl this$0;

   public PIMListImpl$WrapperListener(PIMListImpl _1, PIMListListener listener) {
      this.this$0 = _1;
      this._listener = listener;
   }

   @Override
   public void reset(Collection collection) {
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      PIMItem item = this.this$0.getPIMItemFor(element);
      this._listener.itemAdded(item);
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      PIMItem oldItem = this.this$0.getPIMItemFor(oldElement);
      PIMItem newItem = this.this$0.getPIMItemFor(newElement);
      this._listener.itemUpdated(oldItem, newItem);
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      PIMItem item = this.this$0.getPIMItemFor(element);
      this._listener.itemRemoved(item);
   }
}
