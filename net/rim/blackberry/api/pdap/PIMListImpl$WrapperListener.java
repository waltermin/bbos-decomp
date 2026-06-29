package net.rim.blackberry.api.pdap;

import javax.microedition.pim.PIMItem;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;

class PIMListImpl$WrapperListener implements CollectionListener {
   private PIMListListener _listener;
   private boolean _listenForGroupAddressCards;
   private final PIMListImpl this$0;

   public PIMListImpl$WrapperListener(PIMListImpl _1, PIMListListener listener, boolean listenForGroupAddressCards) {
      this.this$0 = _1;
      this._listener = listener;
      this._listenForGroupAddressCards = listenForGroupAddressCards;
   }

   @Override
   public void reset(Collection collection) {
      if (this._listener instanceof PIMListListener2) {
         PIMListListener2 p2 = (PIMListListener2)this._listener;
         p2.batchOperation(this.this$0);
      }
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      if (this._listenForGroupAddressCards || !(element instanceof Object)) {
         PIMItem item = this.this$0.getPIMItemFor(element);
         this._listener.itemAdded(item);
      }
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (this._listenForGroupAddressCards || !(newElement instanceof Object)) {
         PIMItem oldItem = this.this$0.getPIMItemFor(oldElement);
         PIMItem newItem = this.this$0.getPIMItemFor(newElement);
         this._listener.itemUpdated(oldItem, newItem);
      }
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      if (this._listenForGroupAddressCards || !(element instanceof Object)) {
         PIMItem item = this.this$0.getPIMItemFor(element);
         this._listener.itemRemoved(item);
      }
   }
}
