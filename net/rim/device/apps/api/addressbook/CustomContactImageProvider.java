package net.rim.device.apps.api.addressbook;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;

public class CustomContactImageProvider {
   CustomContactImageProvider$ContactImageListener _listener;
   protected static final long APPLICATION_REGISTRY_NAME;
   private static CustomContactImageProvider _instance;

   private CustomContactImageProvider() {
   }

   public static void addListener(CustomContactImageProvider$ContactImageListener listener) {
      _instance._listener = listener;
   }

   public static void removeListener(CustomContactImageProvider$ContactImageListener listener) {
      _instance._listener = null;
   }

   public static Bitmap getDisplayIcon(Object pnm) {
      return _instance._listener != null ? _instance._listener.getDisplayIcon(pnm) : null;
   }

   public static DisplayPictureModel getContactPicture(AddressCardModel acm) {
      return _instance._listener != null ? _instance._listener.getContactPicture(acm) : null;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (CustomContactImageProvider)ar.getOrWaitFor(-1567470371864338835L);
      if (_instance == null) {
         _instance = new CustomContactImageProvider();
         ar.put(-1567470371864338835L, _instance);
      }
   }
}
