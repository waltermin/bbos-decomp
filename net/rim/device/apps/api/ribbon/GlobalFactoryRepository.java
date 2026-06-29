package net.rim.device.apps.api.ribbon;

import net.rim.device.api.system.ApplicationRegistry;

public class GlobalFactoryRepository {
   public static final long RIBBON_COMPONENT;
   public static final long SKIN_MANAGER;

   public static FactoryRepository getFactoryRepository(long id) {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      synchronized (reg) {
         GlobalFactoryRepository$FactoryRepositoryImpl repos = (GlobalFactoryRepository$FactoryRepositoryImpl)reg.get(id);
         if (repos == null) {
            repos = new GlobalFactoryRepository$FactoryRepositoryImpl();
            reg.put(id, repos);
         }

         return repos;
      }
   }
}
