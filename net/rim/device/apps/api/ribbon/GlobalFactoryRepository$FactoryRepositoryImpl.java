package net.rim.device.apps.api.ribbon;

import java.util.Hashtable;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.indicators.TestPoint;

final class GlobalFactoryRepository$FactoryRepositoryImpl implements FactoryRepository, TestPoint {
   private Hashtable _factories = new Hashtable(20);

   @Override
   public final void test(Object id, Object value) {
      if (id instanceof String) {
         Factory factory = this.getFactory((String)id);
         if (factory instanceof TestPoint) {
            TestPoint tp = (TestPoint)factory;
            tp.test(id, value);
         }
      }
   }

   @Override
   public final void addFactory(String key, Factory factory) {
      if (factory != null && key != null && key.length() > 0) {
         synchronized (this._factories) {
            if (this._factories.containsKey(key)) {
               throw new IllegalArgumentException();
            }

            this._factories.put(key, factory);
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void removeFactory(String key) {
      if (key != null && key.length() > 0) {
         this._factories.remove(key);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final Factory getFactory(String key) {
      if (key != null && key.length() > 0) {
         return (Factory)this._factories.get(key);
      } else {
         throw new IllegalArgumentException();
      }
   }
}
