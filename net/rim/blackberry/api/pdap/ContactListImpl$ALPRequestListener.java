package net.rim.blackberry.api.pdap;

import java.util.Vector;
import javax.microedition.pim.Contact;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.apps.internal.addressbook.lookup.ALPConfiguration;
import net.rim.device.apps.internal.addressbook.lookup.Request;

class ContactListImpl$ALPRequestListener implements CollectionListener {
   Request _request;
   RemoteLookupListener _listener;
   private final ContactListImpl this$0;

   ContactListImpl$ALPRequestListener(ContactListImpl _1, Request request, RemoteLookupListener listener) {
      this.this$0 = _1;
      this._request = request;
      this._listener = listener;
   }

   @Override
   public void reset(Collection collection) {
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (oldElement.equals(this._request)) {
         int size = this._request.size();
         Object[] results = new Object[size];
         this._request.getAt(0, size, results, 0);
         Vector resultsVector = (Vector)(new Object(size));

         for (int i = 0; i < size; i++) {
            Contact newContact = new ContactImpl(results[i], this.this$0);
            resultsVector.addElement(newContact);
         }

         this._listener.items(resultsVector.elements());
         ALPConfiguration.getManager().removeCollectionListener(this);
      }
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
   }
}
