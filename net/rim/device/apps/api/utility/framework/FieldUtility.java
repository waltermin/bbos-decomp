package net.rim.device.apps.api.utility.framework;

import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.ui.Field;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;

public class FieldUtility {
   public static Object[] getFields(ReadableList models, Object context) {
      int size = models.size();
      Object[] modelArray = new Object[size];
      models.getAt(0, size, modelArray, 0);
      return modelArray;
   }

   public static Vector sortFieldsByOrder(Object[] modelArray, Object context) {
      int size = modelArray.length;
      RIMModel[] sortedModels = new RIMModel[size];
      int[] orderValues = new int[size];
      size = orders(modelArray, sortedModels, orderValues, context);
      Vector orderedFields = new Vector(size);

      for (int i = 0; i < size; i++) {
         RIMModel currModel = sortedModels[i];
         FieldProvider fieldProvider = (FieldProvider)currModel;
         Field f = fieldProvider.getField(context);
         if (f != null) {
            if (f.getCookie() == null) {
               f.setCookie(currModel);
            }

            orderedFields.addElement(f);
         }
      }

      return orderedFields;
   }

   public static int orders(Object[] models, RIMModel[] sortedModels, int[] orders, Object context) {
      if (models == null) {
         return 0;
      }

      int accumulator = 0;

      for (Object o : models) {
         if (o instanceof FieldProvider) {
            FieldProvider ofp = (FieldProvider)o;
            sortedModels[accumulator] = (RIMModel)o;
            orders[accumulator] = ofp.getOrder(context);
            accumulator++;
         }
      }

      if (accumulator > 0) {
         Arrays.sort(orders, 0, accumulator, sortedModels);
      }

      return accumulator;
   }
}
