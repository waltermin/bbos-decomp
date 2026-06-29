package net.rim.device.apps.api.addressbook;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;

public class AddressBookAwareField extends VerticalFieldManager implements CollectionListener, Runnable {
   private Application _app;
   private RIMModel _model;
   private Object _addressCard;
   private boolean _ensureUpdateNotifications;
   static final int ELEMENT_ADDED = 0;
   static final int ELEMENT_REMOVED = 1;
   static final int ELEMENT_UPDATED = 2;
   static final int RESET = 3;

   protected AddressBookAwareField(RIMModel addressModel) {
      this(addressModel, false, null);
   }

   protected AddressBookAwareField(RIMModel addressModel, Object context) {
      this(addressModel, false, context);
   }

   protected AddressBookAwareField(RIMModel addressModel, boolean ensureUpdateNotifications, Object context) {
      this._model = addressModel;
      this._addressCard = ContextObject.get(context, 252);
      this._ensureUpdateNotifications = ensureUpdateNotifications;
      boolean doReverseLookup = !ContextObject.getFlag(context, 82);
      AddressBook ab = AddressBookServices.getAddressBook(false);
      if (ab != null) {
         if (this._addressCard == null && doReverseLookup) {
            this._addressCard = ab.reverseLookup(addressModel);
         }

         if (ContextObject.getFlag(context, 9)) {
            ab.addCollectionListener(new Object(this));
         }
      }

      this._app = Application.getApplication();
   }

   protected void addressBookUpdated() {
      throw null;
   }

   protected RIMModel getModel() {
      return this._model;
   }

   protected Object getAddressCard() {
      return this._addressCard;
   }

   private void handleAddressBookUpdatepdateOnCorrectThread(int updateType, Object affectedAddressCard) {
      Object oldAddressCard = this._addressCard;
      this._addressCard = AddressBookServices.reverseLookup(this._model, false);
      switch (updateType) {
         case -1:
            break;
         case 0:
         case 2:
         default:
            if (this._ensureUpdateNotifications || this._addressCard != null && this._addressCard.equals(affectedAddressCard)) {
               this._app.invokeLater(this);
               return;
            }
            break;
         case 1:
            if (this._ensureUpdateNotifications || oldAddressCard != null && oldAddressCard.equals(affectedAddressCard)) {
               this._app.invokeLater(this);
            }
      }
   }

   @Override
   public void reset(Collection collection) {
      this.handleAddressBookUpdatepdateOnCorrectThread(3, this._addressCard);
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.handleAddressBookUpdatepdateOnCorrectThread(0, element);
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.handleAddressBookUpdatepdateOnCorrectThread(2, newElement);
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      this.handleAddressBookUpdatepdateOnCorrectThread(1, element);
   }

   @Override
   public void run() {
      this.addressBookUpdated();
   }
}
