package net.rim.blackberry.api.pim;

import java.util.Vector;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.apps.internal.addressbook.lookup.Request;

class ContactListImpl$RequestListener implements CollectionListener {
   Request _request;
   RemoteLookupListener _listener;
   private final ContactListImpl this$0;

   public ContactListImpl$RequestListener(ContactListImpl _1, Request request, RemoteLookupListener listener) {
      this.this$0 = _1;
      this._request = request;
      this._listener = listener;
   }

   @Override
   public void reset(Collection collection) {
      int size = this._request.size();
      Object[] results = new Object[size];
      this._request.getAt(0, size, results, 0);
      Vector resultsVector = new Vector(size);

      for (int i = 0; i < size; i++) {
         Contact newContact = new ContactImpl(results[i], this.this$0);
         resultsVector.addElement(newContact);
      }

      this._listener.items(resultsVector.elements());
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
   }
}
