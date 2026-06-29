package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.vm.Array;

public class SubmemberUtilities {
   public static Object getFirstSubmember(ReadableList s, Recognizer r) {
      int count = s.size();

      for (int i = 0; i < count; i++) {
         Object o = s.getAt(i);
         if (r.recognize(o)) {
            return o;
         }
      }

      return null;
   }

   public static Object[] getSubmembers(ReadableList s, Recognizer r) {
      int size = s.size();
      Object[] models = new Object[size];
      int dest = 0;

      for (int i = 0; i < size; i++) {
         Object o = s.getAt(i);
         if (r.recognize(o)) {
            models[dest++] = o;
         }
      }

      Array.resize(models, dest);
      return models;
   }
}
