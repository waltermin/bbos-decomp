package net.rim.device.apps.api.framework.registration;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.model.RIMModel;

public class ModelViewListenerRegistry {
   private static LongHashtable _typeToListenersVectorTable;
   private static final long TYPE_TO_LISTENERS_VECTOR_TABLE = 698947564434617175L;
   private static OpenViewer[] _openViewers;
   private static final long OPEN_VIEWERS_VECTOR = 8355840199595148178L;

   private ModelViewListenerRegistry() {
   }

   private static ModelViewListener[] getListenerList(long modelType) {
      return (ModelViewListener[])_typeToListenersVectorTable.get(modelType);
   }

   public static void addModelViewListener(long modelType, ModelViewListener listener) {
      synchronized (_typeToListenersVectorTable) {
         ModelViewListener[] currentListeners = getListenerList(modelType);
         if (currentListeners == null) {
            currentListeners = new ModelViewListener[0];
            _typeToListenersVectorTable.put(modelType, currentListeners);
         }

         Arrays.add(currentListeners, listener);
      }
   }

   public static void removeModelViewListener(long modelType, ModelViewListener listener) {
      synchronized (_typeToListenersVectorTable) {
         ModelViewListener[] currentListeners = getListenerList(modelType);
         if (currentListeners != null) {
            Arrays.remove(currentListeners, listener);
         }
      }
   }

   public static void notifyModelOpened(OpenViewer openViewer, Object context) {
      synchronized (_openViewers) {
         Arrays.add(_openViewers, openViewer);
      }

      synchronized (_typeToListenersVectorTable) {
         ModelViewListener[] listeners = getListenerList(openViewer.getModelType(context));
         if (listeners != null) {
            int numListeners = listeners.length;

            for (int i = 0; i < numListeners; i++) {
               listeners[i].modelOpened(openViewer, context);
            }
         }
      }
   }

   public static void notifyModelClosed(OpenViewer openViewer, Object context) {
      synchronized (_openViewers) {
         Arrays.remove(_openViewers, openViewer);
      }

      synchronized (_typeToListenersVectorTable) {
         ModelViewListener[] listeners = getListenerList(openViewer.getModelType(context));
         if (listeners != null) {
            int numListeners = listeners.length;

            for (int i = 0; i < numListeners; i++) {
               listeners[i].modelClosed(openViewer, context);
            }
         }
      }
   }

   public static void notifyOfOpenedModelChange(RIMModel oldModel, RIMModel newModel, Object context) {
      int i = 0;

      while (true) {
         OpenViewer openViewer = null;
         synchronized (_openViewers) {
            int numListeners = _openViewers.length;
            if (i >= numListeners) {
               return;
            }

            openViewer = _openViewers[i];
         }

         openViewer.notifyOfOpenedModelChange(oldModel, newModel, context);
         i++;
      }
   }

   public static boolean isViewerUp(long modelType, RIMModel model, Object context) {
      synchronized (_openViewers) {
         int numViewers = _openViewers.length;

         for (int i = 0; i < numViewers; i++) {
            OpenViewer openViewer = _openViewers[i];
            if ((modelType == 0 || modelType == openViewer.getModelType(context)) && (model == null || model == openViewer.getOpenedModel(context))) {
               return true;
            }
         }

         return false;
      }
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _typeToListenersVectorTable = ar.getLongHashtable(698947564434617175L);
      _openViewers = (OpenViewer[])ar.getOrWaitFor(8355840199595148178L);
      if (_openViewers == null) {
         _openViewers = new OpenViewer[0];
         ar.put(8355840199595148178L, _openViewers);
      }
   }
}
