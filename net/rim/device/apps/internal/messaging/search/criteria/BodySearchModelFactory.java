package net.rim.device.apps.internal.messaging.search.criteria;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public final class BodySearchModelFactory extends RIMModelFactory {
   private RIMModelFactory[] _subCriterionFactories;
   private static final long REGKEY = 2553254370272492794L;
   private static BodySearchModelFactory _factory;

   private BodySearchModelFactory() {
   }

   public static final BodySearchModelFactory getInstance() {
      if (_factory == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _factory = (BodySearchModelFactory)ar.getOrWaitFor(2553254370272492794L);
         if (_factory == null) {
            _factory = new BodySearchModelFactory();
            ar.put(2553254370272492794L, _factory);
         }
      }

      return _factory;
   }

   @Override
   public final boolean recognize(Object o) {
      if (ContextObject.getFlag(o, 22) && ContextObject.getFlag(o, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(o, 255);
         if (sb == null) {
            return false;
         }

         if (sb.getFieldType(true) == 3) {
            return true;
         }

         if (this._subCriterionFactories != null) {
            int numSubCriterionFactories = this._subCriterionFactories.length;

            for (int i = 0; i < numSubCriterionFactories; i++) {
               if (this._subCriterionFactories[i].recognize(o)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return o instanceof BodySearchModel;
      }
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }

   @Override
   public final Object createInstance(Object context) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer sb = (SyncBuffer)ContextObject.get(context, 255);
         if (sb == null) {
            return null;
         }

         int startPosition = sb.getPosition();
         BodySearchModel bodySearchModel = new BodySearchModel();

         label74:
         try {
            String bodyText = sb.getString(3, true);
            bodySearchModel.setValue(bodyText);
         } finally {
            break label74;
         }

         if (this._subCriterionFactories != null) {
            int numSubCriterionFactories = this._subCriterionFactories.length;

            for (int i = 0; i < numSubCriterionFactories; i++) {
               sb.setPosition(startPosition);
               Object subCriterion = this._subCriterionFactories[i].createInstance(context);
               if (subCriterion != null) {
                  bodySearchModel.addSubCriterion((PersistableRIMModel)subCriterion);
               }
            }
         }

         return bodySearchModel;
      } else {
         BodySearchModel bodySearchModel = new BodySearchModel();
         if (this._subCriterionFactories != null) {
            int numSubCriterionFactories = this._subCriterionFactories.length;

            for (int i = 0; i < numSubCriterionFactories; i++) {
               Object subCriterion = this._subCriterionFactories[i].createInstance(context);
               if (subCriterion != null) {
                  bodySearchModel.addSubCriterion((PersistableRIMModel)subCriterion);
               }
            }
         }

         return bodySearchModel;
      }
   }

   public final synchronized void registerSubCriterionFactory(RIMModelFactory factory) {
      if (this._subCriterionFactories == null) {
         this._subCriterionFactories = new RIMModelFactory[]{factory};
      } else {
         Arrays.add(this._subCriterionFactories, factory);
      }
   }

   public final RIMModelFactory[] getSubCriterionFactories() {
      return this._subCriterionFactories;
   }
}
