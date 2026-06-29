package net.rim.device.apps.internal.addressbook;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.FilterStatusListener;
import net.rim.device.api.collection.util.PatriciaKeywordFilterList;
import net.rim.device.api.util.StringUtilities;

final class AddressBookKeywordFilterList extends PatriciaKeywordFilterList implements CollectionListener {
   private AddressBookKeywordFilterList(AddressBookCollection collection, AddressBookOrderHelper helper) {
      super(collection, collection.getKeywordTree());
      collection.addCollectionListener(new Object(this));
   }

   static final AddressBookKeywordFilterList getInstance(AddressBookCollection collection, AddressBookOrderHelper helper, long order) {
      return new AddressBookKeywordFilterList(collection, helper);
   }

   @Override
   public final void reset(Collection collection) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.fireElementAdded(element);
      super.reset(collection);
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.fireElementUpdated(oldElement, newElement);
      super.reset(collection);
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this.fireElementRemoved(element);
      super.reset(collection);
   }

   @Override
   public final void setCriteria(Object criteria, FilterStatusListener listener) {
      if (criteria instanceof Object) {
         criteria = StringUtilities.toLowerCase((String)criteria, 1701707776);
      }

      super.setCriteria(criteria, listener);
   }

   @Override
   protected final int[] getIDsBySortOrder() {
      return ((AddressBookCollection)super._source).getIDsBySortOrder();
   }
}
