package net.rim.blackberry.api.mail;

import net.rim.blackberry.api.mail.event.DefaultSessionListener;
import net.rim.blackberry.api.mail.event.ViewListener;
import net.rim.device.internal.applicationcontrol.ApplicationControl;

public final class Session {
   private DefaultService _service;

   private static final void assertPermission() {
      ApplicationControl.assertEmailAllowed(true);
   }

   private Session(DefaultService ds) {
      assertPermission();
      this._service = ds;
   }

   public static final Session getDefaultInstance() {
      try {
         return new Session(DefaultService.getInstance());
      } catch (NoSuchServiceException e) {
         return null;
      }
   }

   public static final Session getInstance(ServiceConfiguration sc) {
      try {
         return new Session(DefaultService.getInstance(sc));
      } catch (NoSuchServiceException e) {
         return null;
      }
   }

   public static final Session waitForDefaultSession() {
      return new Session(DefaultService.waitForDefaultService());
   }

   public static final Session getDefaultInstance(ServiceConfiguration sc) {
      try {
         DefaultService ds = DefaultService.getInstance();
         if (ds.getServiceConfiguration().equals(sc)) {
            return new Session(ds);
         }
      } catch (NoSuchServiceException var2) {
      }

      return null;
   }

   public final ServiceConfiguration getServiceConfiguration() {
      assertPermission();
      return this._service.getServiceConfiguration();
   }

   public final Store getStore() {
      assertPermission();
      return this._service.getStore();
   }

   public final Transport getTransport() {
      assertPermission();
      return this._service.getTransport();
   }

   public static final void addViewListener(ViewListener vl) {
      if (vl == null) {
         throw new NullPointerException();
      }

      assertPermission();
      ListenerManager.getInstance().addViewListener(vl);
   }

   public static final void removeViewListener(ViewListener vl) {
      ListenerManager.getInstance().removeViewListener(vl);
   }

   public static final void addDefaultSessionListener(DefaultSessionListener dl) {
      assertPermission();
      if (dl == null) {
         throw new NullPointerException("DefaultSessionListener is null");
      }

      ListenerManager.getInstance().addDefaultSessionListener(dl);
   }

   public static final void removeDefaultSessionListener(DefaultSessionListener dl) {
      ListenerManager.getInstance().removeDefaultSessionListener(dl);
   }
}
