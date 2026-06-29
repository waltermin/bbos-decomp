package net.rim.blackberry.api.stringpattern;

import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.component.ActiveFieldContext;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;

final class ExternalActiveFieldCookieFactory implements Factory {
   private LongHashtable _cookies = new LongHashtable();
   private int _scope = UIDGenerator.getUniqueScopingValue();
   static final long ID = 2591848032186709274L;
   private static ExternalActiveFieldCookieFactory _factory;

   final long getNewID() {
      long luid;
      do {
         int uid = UIDGenerator.getUID(this._scope);
         luid = UIDGenerator.makeLUID(this._scope, uid);
      } while (ApplicationRegistry.getApplicationRegistry().get(luid) != null);

      return luid;
   }

   final long[] removeCookies(ApplicationDescriptor application) {
      long[] ids = new long[0];
      LongEnumeration e = this._cookies.keys();

      while (e.hasMoreElements()) {
         long id = e.nextElement();
         ExternalActiveFieldCookie cookie = (ExternalActiveFieldCookie)this._cookies.get(id);
         if (cookie.getApplication().equals(application)) {
            this._cookies.remove(id);
            Arrays.add(ids, id);
         }
      }

      return ids;
   }

   final void addCookie(long id, ExternalActiveFieldCookie cookie) {
      this._cookies.put(id, cookie);
      ApplicationRegistry.getApplicationRegistry().put(id, _factory);
   }

   @Override
   public final Object createInstance(Object initialData) {
      if (!(initialData instanceof ActiveFieldContext)) {
         return null;
      }

      ActiveFieldContext context = (ActiveFieldContext)initialData;
      ExternalActiveFieldCookie cookie = (ExternalActiveFieldCookie)this._cookies.get(context.getID());
      if (cookie != null) {
         cookie.initialize(context.getData());
      }

      return cookie;
   }

   private ExternalActiveFieldCookieFactory() {
   }

   static final ExternalActiveFieldCookieFactory getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _factory = (ExternalActiveFieldCookieFactory)ar.getOrWaitFor(2591848032186709274L);
      if (_factory == null) {
         _factory = new ExternalActiveFieldCookieFactory();
         ar.put(2591848032186709274L, _factory);
      }

      return _factory;
   }
}
