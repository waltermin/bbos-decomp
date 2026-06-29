package net.rim.device.apps.internal.qm.peer;

import net.rim.blackberry.api.blackberrymessenger.BlackBerryMessenger;
import net.rim.blackberry.api.blackberrymessenger.MessengerContact;
import net.rim.blackberry.api.blackberrymessenger.Service;
import net.rim.blackberry.api.blackberrymessenger.SessionRequestListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.applicationcontrol.ApplicationControl;

final class BlackBerryMessengerImpl extends BlackBerryMessenger {
   private PeerApplication _application;
   private ServiceRunnable[] _services = new ServiceRunnable[0];
   private static final long GUID = 4927202520515498096L;
   static final BlackBerryMessengerImpl$MyInfo _myInfo = new BlackBerryMessengerImpl$MyInfo();

   BlackBerryMessengerImpl(PeerApplication application) {
      this._application = application;
      ApplicationRegistry.getApplicationRegistry().replace(4927202520515498096L, this);
   }

   @Override
   public final MessengerContact chooseContact() {
      this.assertPermission();
      SelectContactDialog scd = new SelectContactDialog(((PeerContactListCollection)this._application._contactListCollection).getContacts(1));
      return Application.getApplication().isEventThread() && scd.doModal() ? scd.getSelection() : null;
   }

   @Override
   public final MessengerContact getMyContactInfo() {
      this.assertPermission();
      return _myInfo;
   }

   @Override
   public final void registerService(Service service, String name, ApplicationDescriptor application) {
      this.assertPermission();
      if (!Utils.isValidString(name)) {
         throw new IllegalArgumentException("String name must not be null or zero-length");
      }

      if (application != null) {
         if (application.getModuleHandle() != ApplicationDescriptor.currentApplicationDescriptor().getModuleHandle()) {
            throw new IllegalArgumentException("ApplicationDescriptor must describe the registering application");
         }

         if (service == null) {
            throw new IllegalArgumentException("service cannot be null");
         }

         synchronized (this._services) {
            if (this.serviceIndex(service) == -1) {
               Arrays.add(this._services, new ServiceRunnable(service, name, application));
            }
         }
      } else {
         throw new IllegalArgumentException("application cannot be null");
      }
   }

   @Override
   public final void deregisterService(Service service) {
      this.assertPermission();
      synchronized (this._services) {
         int index = this.serviceIndex(service);
         if (index >= 0) {
            Arrays.removeAt(this._services, index);
         }
      }
   }

   @Override
   public final void addSessionRequestListener(SessionRequestListener listener, ApplicationDescriptor application) {
      this.assertPermission();
      if (application != null) {
         if (application.getModuleHandle() != ApplicationDescriptor.currentApplicationDescriptor().getModuleHandle()) {
            throw new IllegalArgumentException("ApplicationDescriptor must describe the registering application");
         }

         if (listener != null) {
            SessionManager.getInstance().addRequestListener(listener, application);
         } else {
            throw new IllegalArgumentException("listener cannot be null");
         }
      } else {
         throw new IllegalArgumentException("application cannot be null");
      }
   }

   @Override
   public final void removeSessionRequestListener(SessionRequestListener listener) {
      this.assertPermission();
      SessionManager.getInstance().removeRequestListener(listener);
   }

   final ServiceRunnable[] getRegisteredServices() {
      return this._services;
   }

   final void invokeService(ServiceRunnable service, MessengerContact contact) {
      if (service != null) {
         service.setContact(contact);
         service.invokeLater();
      }
   }

   private final int serviceIndex(Service service) {
      int index = -1;
      synchronized (this._services) {
         for (int i = this._services.length - 1; i >= 0; i--) {
            if (this._services[i].getService() == service) {
               index = i;
               break;
            }
         }

         return index;
      }
   }

   private final void assertPermission() {
      ApplicationControl.assertEmailAllowed(true);
   }
}
