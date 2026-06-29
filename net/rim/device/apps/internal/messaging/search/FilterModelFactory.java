package net.rim.device.apps.internal.messaging.search;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.RIMModelFactoryCache;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.internal.commonmodels.title.TitleModelFactory;

final class FilterModelFactory extends RIMModelFactory {
   private static FilterModelFactory _singletonInstance = new FilterModelFactory();
   private static RIMModelFactory[] _factoryCache = RIMModelFactoryCache.allocate();
   private static ContextObjectWR _decodeContextWR = new ContextObjectWR(22, 19);

   private FilterModelFactory() {
   }

   static final synchronized FilterModelFactory getInstance() {
      return _singletonInstance;
   }

   private static final RIMModelFactory[] getFactories() {
      RIMModelFactory[] factories = RIMModelFactoryRepository.getModelFactories(7820085525428081380L);
      int len = factories.length;
      RIMModelFactory[] adjusted = new RIMModelFactory[len + 2];
      System.arraycopy(factories, 0, adjusted, 0, len);
      adjusted[len] = ShortCutKeyModelFactory.getInstance();
      adjusted[len + 1] = TitleModelFactory.getInstance();
      return adjusted;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object createInstance(Object initialData) {
      if (initialData == null) {
         return new FilterModel();
      }

      if (ContextObject.getFlag(initialData, 22) && ContextObject.getFlag(initialData, 19)) {
         FilterModel model = new FilterModel();
         ContextObject decodeContext = _decodeContextWR.getContextObject();
         boolean var10 = false /* VF: Semaphore variable */;
         boolean var13 = false /* VF: Semaphore variable */;

         RIMModelFactory[] factories;
         label92: {
            label91: {
               try {
                  label89:
                  try {
                     var13 = true;
                     var10 = true;
                     SyncBuffer e = (SyncBuffer)ContextObject.get(initialData, 255);
                     if (e == null) {
                        factories = null;
                        var10 = false;
                        var13 = false;
                        break label92;
                     }

                     factories = getFactories();
                     decodeContext.put(255, e);
                     RIMModelFactoryCache.addToModelWithCache(_factoryCache, factories, null, e, model, decodeContext);
                     int uid = e.getUID();
                     if (uid != 0) {
                        model.setUID(uid);
                        var10 = false;
                        var13 = false;
                     } else {
                        var10 = false;
                        var13 = false;
                     }
                     break label91;
                  } finally {
                     if (var13) {
                        factories = null;
                        var10 = false;
                        break label89;
                     }
                  }
               } finally {
                  if (var10) {
                     decodeContext.remove(255);
                  }
               }

               decodeContext.remove(255);
               return factories;
            }

            decodeContext.remove(255);
            return FilterModel.createGroup(model);
         }

         decodeContext.remove(255);
         return factories;
      } else {
         return null;
      }
   }

   @Override
   public final int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }

   @Override
   public final boolean recognize(Object o) {
      if (ContextObject.getFlag(o, 22) && ContextObject.getFlag(o, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(o, 255);
         return sb != null && sb.getFieldType() == 9;
      } else {
         return o instanceof FilterModel;
      }
   }
}
