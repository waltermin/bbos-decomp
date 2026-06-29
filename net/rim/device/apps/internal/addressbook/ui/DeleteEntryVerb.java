package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.AddressBookUtilities;
import net.rim.device.apps.internal.addressbook.lookup.ALPConfiguration;
import net.rim.device.apps.internal.addressbook.lookup.ALPManager;
import net.rim.device.apps.internal.addressbook.lookup.Request;
import net.rim.device.apps.internal.addressbook.lookup.RequestModel;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.internal.i18n.CommonResource;

public final class DeleteEntryVerb extends Verb {
   private Object _record;
   private boolean _forceTerminalResult;
   private boolean _forceDeleteConfirmation;

   public DeleteEntryVerb(Object record, boolean forceTerminalResult) {
      super(
         record instanceof Object ? 414080 : 611584,
         !(record instanceof Object) && !(record instanceof Object) ? CommonResource.getBundle() : AddressBookResources.getResourceBundleFamily(),
         record instanceof Object ? 805 : (record instanceof Object ? 1704 : 17)
      );
      this._record = record;
      this._forceTerminalResult = forceTerminalResult;
   }

   public DeleteEntryVerb(Object record) {
      this(record, false);
   }

   public DeleteEntryVerb(Object record, char keyPressed) {
      this(record, false);
      this._forceDeleteConfirmation = keyPressed == '\b';
   }

   @Override
   public final Object invoke(Object parameter) {
      Object result = null;
      if (AddressBookUtilities.confirmDelete(this._record, this._forceDeleteConfirmation)) {
         if (!(this._record instanceof Object)) {
            AddressBookServices.getAddressBook().removeAddressCard(this._record);
         } else {
            RequestModel rm = (RequestModel)this._record;
            ALPManager manager = ALPConfiguration.getManager();
            Request r = manager.fetchRequestFromModel(rm);
            if (r != null) {
               manager.deleteRequest(r);
            }
         }

         if (this._forceTerminalResult || !ContextObject.getFlag(parameter, 5)) {
            result = new Object(39, 40);
         }
      }

      return result;
   }
}
