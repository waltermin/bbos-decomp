package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;

public class PopulateModels {
   public static void populateModels(ReadableList model, long factoryId, Object context) {
      RIMModelFactory[] mf = RIMModelFactoryRepository.getModelFactories(factoryId);
      if (mf != null) {
         WritableSet ws = (WritableSet)model;

         for (RIMModelFactory rimMF : mf) {
            Object[] m = SubmemberUtilities.getSubmembers(model, rimMF);
            int minCount = rimMF.getMinimumCount(context);
            if (minCount > 0) {
               int modelsToAdd = minCount - m.length;

               for (int j = 1; j <= modelsToAdd; j++) {
                  Object o = rimMF.createInstance(context);
                  ws.add(o);
               }
            }
         }
      }
   }
}
