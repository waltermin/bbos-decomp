package net.rim.device.apps.internal.ribbon.components;

import java.lang.ref.WeakReference;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;

public final class ImageFactory implements Factory {
   Bitmap _backgroundBitmap;
   WeakReference[] _listeners = new Object[0];

   final void init() {
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("Image", this);
   }

   final void addBackgroundListener(ImageFactory$ImageComponent listener) {
      synchronized (this._listeners) {
         Arrays.add(this._listeners, new Object(listener));
      }
   }

   public final void setBackgroundImage(Bitmap bitmap) {
      this._backgroundBitmap = bitmap;
      WeakReference[] listeners = this._listeners;
      synchronized (listeners) {
         for (int i = listeners.length - 1; i >= 0; i--) {
            ImageFactory$ImageComponent component = (ImageFactory$ImageComponent)listeners[i].get();
            if (component != null) {
               component.setBitmap(bitmap);
            } else {
               Arrays.removeAt(listeners, i);
            }
         }
      }
   }

   @Override
   public final Object createInstance(Object initialData) {
      return new ImageFactory$ImageComponent();
   }

   public static final ImageFactory getInstance() {
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      return (ImageFactory)repos.getFactory("Image");
   }
}
