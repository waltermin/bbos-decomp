package net.rim.device.apps.internal.memo;

import net.rim.device.apps.api.framework.registration.RIMModelFactory;

final class MemoModelFactory extends RIMModelFactory {
   private static MemoModelFactory _instance;

   private MemoModelFactory() {
   }

   public static final MemoModelFactory getInstance() {
      if (_instance == null) {
         _instance = new MemoModelFactory();
      }

      return _instance;
   }

   @Override
   public final Object createInstance(Object initialData) {
      return new MemoModelImpl();
   }

   @Override
   public final int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }

   @Override
   public final boolean recognize(Object o) {
      return false;
   }
}
