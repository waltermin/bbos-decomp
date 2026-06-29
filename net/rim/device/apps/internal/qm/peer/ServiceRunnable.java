package net.rim.device.apps.internal.qm.peer;

import net.rim.blackberry.api.blackberrymessenger.MessengerContact;
import net.rim.blackberry.api.blackberrymessenger.Service;
import net.rim.device.api.system.ApplicationDescriptor;

final class ServiceRunnable extends ApplicationStartupRunnable {
   private Service _service;
   private MessengerContact _contact;
   private String _name;

   ServiceRunnable(Service service, String name, ApplicationDescriptor app) {
      super(app);
      this._service = service;
      this._name = name;
   }

   final void setContact(MessengerContact contact) {
      this._contact = contact;
   }

   @Override
   public final void run() {
      this._service.start(this._contact);
   }

   final String getName() {
      return this._name;
   }

   final Service getService() {
      return this._service;
   }
}
