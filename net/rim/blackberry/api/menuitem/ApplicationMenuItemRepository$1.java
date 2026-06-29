package net.rim.blackberry.api.menuitem;

import net.rim.blackberry.api.maps.MapView;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.sms.SMSModel;

class ApplicationMenuItemRepository$1 implements ApplicationSpecificProcessing {
   private final ApplicationMenuItemRepository this$0;

   ApplicationMenuItemRepository$1(ApplicationMenuItemRepository _1) {
      this.this$0 = _1;
   }

   @Override
   public Object lookForAppropriateObject(long menuitemid, Object c) {
      if (menuitemid == -5544721730296222436L) {
         Object o = ContextObject.get(c, 252);
         if (o != null) {
            return o;
         }
      } else if (menuitemid == 4804476335504286437L) {
         if (c instanceof ContextObject) {
            return new MapView((ContextObject)c);
         }
      } else {
         if (menuitemid == 9096799525298506811L || menuitemid == 4101976187669332923L) {
            Object obj = ContextObject.get(c, 250);
            if (!(obj instanceof SMSModel)) {
               obj = ContextObject.get(c, 245);
            }

            return obj;
         }

         if (menuitemid == 5529224403653746205L || menuitemid == 2946406880720845997L) {
            return ContextObject.get(c, -7651695713744129224L);
         }
      }

      return null;
   }
}
