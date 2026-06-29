package net.rim.device.apps.api.options;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;

public final class OptionsContext {
   private static final long CONTEXT_OBJECT_GUID = -1925593203060784091L;
   public static final long OPTIONS_CONTEXT_KEY = -1925593203060784091L;
   public static final long OPTIONS_CONTEXT_CHANGE = -1925593203060784091L;
   private static ContextObject _contextObject;

   public static final ContextObject getContextObject() {
      return _contextObject;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _contextObject = (ContextObject)ar.getOrWaitFor(-1925593203060784091L);
      if (_contextObject == null) {
         _contextObject = (ContextObject)(new Object());
         ar.put(-1925593203060784091L, _contextObject);
      }
   }
}
