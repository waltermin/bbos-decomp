package net.rim.device.api.notification;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.vm.Array;
import net.rim.vm.TraceBack;

public final class NotificationsManager implements NotificationsConstants {
   private static Hashtable _sourcesByObject;
   private static final long SOURCES_BY_OBJECT;
   private static LongHashtable _sourcesById;
   private static final long SOURCES_BY_ID;
   private static LongHashtable _consequences;
   private static final long CONSEQUENCES;
   private static NotificationsEngine _engine;
   private static LongHashtable _listenersById;
   private static final long LISTENERS_BY_ID;
   private static Vector _sourceListeners = new Vector();
   private static final long SOURCE_LISTENERS;
   private static final long INVALID_VALUE;

   private NotificationsManager() {
   }

   public static final void triggerImmediateEvent(long sourceID, long eventID, Object eventReference, Object context) {
      NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesById.get(sourceID);
      if (wrapper != null) {
         if (_engine == null) {
            _engine = (NotificationsEngine)ApplicationRegistry.getApplicationRegistry().get(6720217471165517311L);
         }

         if (_engine != null) {
            _engine.triggerImmediateEvent(wrapper._id, wrapper._object, wrapper._level, eventID, eventReference, context);
         }
      }
   }

   public static final void cancelImmediateEvent(long sourceID, long eventID, Object eventReference, Object context) {
      NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesById.get(sourceID);
      if (wrapper != null) {
         if (_engine == null) {
            _engine = (NotificationsEngine)ApplicationRegistry.getApplicationRegistry().get(6720217471165517311L);
         }

         if (_engine != null) {
            _engine.cancelImmediateEvent(wrapper._id, wrapper._object, wrapper._level, eventID, eventReference, context);
         }
      }
   }

   public static final void negotiateDeferredEvent(long sourceID, long eventID, Object eventReference, long eventDate, int triggerIndex, Object context) {
      NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesById.get(sourceID);
      if (wrapper != null) {
         if (_engine == null) {
            _engine = (NotificationsEngine)ApplicationRegistry.getApplicationRegistry().get(6720217471165517311L);
         }

         if (_engine != null) {
            _engine.negotiateDeferredEvent(wrapper._id, wrapper._object, wrapper._level, eventID, eventReference, eventDate, triggerIndex, context);
         }
      }
   }

   public static final void cancelDeferredEvent(long sourceID, long eventID, Object eventReference, int triggerIndex, Object context) {
      NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesById.get(sourceID);
      if (wrapper != null) {
         if (_engine == null) {
            _engine = (NotificationsEngine)ApplicationRegistry.getApplicationRegistry().get(6720217471165517311L);
         }

         if (_engine != null) {
            _engine.cancelDeferredEvent(wrapper._id, wrapper._object, wrapper._level, eventID, eventReference, triggerIndex, context);
         }
      }
   }

   public static final void cancelAllDeferredEvents(long sourceID, int triggerIndex, Object context) {
      NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesById.get(sourceID);
      if (wrapper != null) {
         if (_engine == null) {
            _engine = (NotificationsEngine)ApplicationRegistry.getApplicationRegistry().get(6720217471165517311L);
         }

         if (_engine != null) {
            _engine.cancelAllDeferredEvents(wrapper._id, wrapper._object, wrapper._level, triggerIndex, context);
         }
      }
   }

   public static final int getDeferredEventCount(long sourceID) {
      if (_engine == null) {
         _engine = (NotificationsEngine)ApplicationRegistry.getApplicationRegistry().get(6720217471165517311L);
      }

      return _engine != null ? _engine.getDeferredEventCount(sourceID) : 0;
   }

   public static final Object[] getDeferredEvents(long sourceID) {
      if (_engine == null) {
         _engine = (NotificationsEngine)ApplicationRegistry.getApplicationRegistry().get(6720217471165517311L);
      }

      return _engine != null ? _engine.getDeferredEvents(sourceID) : null;
   }

   public static final long[] getDeferredEventIds(long sourceID) {
      if (_engine == null) {
         _engine = (NotificationsEngine)ApplicationRegistry.getApplicationRegistry().get(6720217471165517311L);
      }

      return _engine != null ? _engine.getDeferredEventIds(sourceID) : null;
   }

   public static final void registerSource(long sourceID, Object source, int level, long relatedSourceID) {
      synchronized (_sourcesById) {
         NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesById.get(sourceID);
         if (wrapper == null) {
            wrapper = new NotificationsManager$Wrapper(null);
            wrapper._object = source;
            wrapper._level = level;
            wrapper._id = sourceID;
            wrapper._relatedId = relatedSourceID;
            _sourcesById.put(sourceID, wrapper);
            _sourcesByObject.put(source, wrapper);
         } else {
            if (source != wrapper._object) {
               _sourcesByObject.remove(wrapper._object);
               wrapper._object = source;
               wrapper._level = level;
               wrapper._relatedId = relatedSourceID;
               wrapper._hidden = false;
               _sourcesByObject.put(source, wrapper);
            }

            wrapper._level = level;
         }

         sourceUpdated();
      }
   }

   public static final void moveSource(long srcSourceID, long destSourceID) {
      synchronized (_sourcesById) {
         NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesById.get(srcSourceID);
         if (wrapper != null) {
            _sourcesById.remove(wrapper._id);
            wrapper._id = destSourceID;
            _sourcesById.put(destSourceID, wrapper);
            sourceUpdated();
         }
      }
   }

   public static final void registerSource(long sourceID, Object source, int level) {
      registerSource(sourceID, source, level, -1);
   }

   public static final void deregisterSource(long sourceID) {
      synchronized (_sourcesById) {
         NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesById.get(sourceID);
         if (wrapper != null) {
            _sourcesById.remove(sourceID);
            _sourcesByObject.remove(wrapper._object);
            sourceUpdated();
         }
      }
   }

   public static final void hideSource(long sourceID) {
      synchronized (_sourcesById) {
         NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesById.get(sourceID);
         if (wrapper != null) {
            wrapper._hidden = true;
            sourceUpdated();
         }
      }
   }

   public static final void unHideSource(long sourceID) {
      synchronized (_sourcesById) {
         NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesById.get(sourceID);
         if (wrapper != null) {
            wrapper._hidden = false;
            sourceUpdated();
         }
      }
   }

   public static final boolean isHidden(long sourceID) {
      NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesById.get(sourceID);
      return wrapper != null && wrapper._hidden;
   }

   public static final Object getSource(long sourceID) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesById.get(sourceID);
      return wrapper != null ? wrapper._object : null;
   }

   public static final long[] getRelatedSourceIds(long sourceID, boolean includeHiddenSources) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      long[] result = new long[0];
      Enumeration objects = _sourcesById.elements();

      while (objects.hasMoreElements()) {
         NotificationsManager$Wrapper w = (NotificationsManager$Wrapper)objects.nextElement();
         if (w._relatedId == sourceID && (includeHiddenSources || !w._hidden)) {
            Array.resize(result, result.length + 1);
            result[result.length - 1] = w._id;
         }
      }

      return result;
   }

   public static final long[] getHiddenSourceIds() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      long[] result = new long[0];
      Enumeration objects = _sourcesById.elements();

      while (objects.hasMoreElements()) {
         NotificationsManager$Wrapper w = (NotificationsManager$Wrapper)objects.nextElement();
         if (w._hidden) {
            Array.resize(result, result.length + 1);
            result[result.length - 1] = w._id;
         }
      }

      return result;
   }

   public static final long getParentSourceID(long sourceID) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesById.get(sourceID);
      return wrapper != null ? wrapper._relatedId : -1;
   }

   public static final long[] enumerateSourceIds() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      synchronized (_sourcesById) {
         long[] result = new long[_sourcesById.size()];
         Enumeration objects = _sourcesById.elements();
         int count = 0;

         while (objects.hasMoreElements()) {
            NotificationsManager$Wrapper w = (NotificationsManager$Wrapper)objects.nextElement();
            result[count++] = w._id;
         }

         return result;
      }
   }

   public static final long getSourceId(Object source) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesByObject.get(source);
      if (wrapper == null) {
         throw new IllegalArgumentException();
      } else {
         return wrapper._id;
      }
   }

   public static final int getSourceLevel(long sourceID) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesById.get(sourceID);
      if (wrapper == null) {
         throw new IllegalArgumentException();
      } else {
         return wrapper._level;
      }
   }

   public static final int getSourceLevel(Object source) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      NotificationsManager$Wrapper wrapper = (NotificationsManager$Wrapper)_sourcesByObject.get(source);
      if (wrapper == null) {
         throw new IllegalArgumentException();
      } else {
         return wrapper._level;
      }
   }

   public static final void registerNotificationsEngineListener(long sourceID, NotificationsEngineListener listener) {
      synchronized (_listenersById) {
         Object target = getListenerByID(sourceID);
         if (target == null) {
            _listenersById.put(sourceID, listener);
         } else if (!(target instanceof Vector)) {
            if (target instanceof NotificationsEngineListener) {
               Vector newTarget = new Vector();
               newTarget.addElement(target);
               newTarget.addElement(listener);
               _listenersById.put(sourceID, newTarget);
            }
         } else {
            ((Vector)target).addElement(listener);
         }
      }
   }

   public static final void deregisterNotificationsEngineListener(long sourceID, NotificationsEngineListener listener) {
      synchronized (_listenersById) {
         Object target = getListenerByID(sourceID);
         Vector listeners = null;
         if (target != null) {
            if (!(target instanceof Vector)) {
               if (target instanceof NotificationsEngineListener) {
                  _listenersById.remove(sourceID);
               }
            } else {
               listeners = (Vector)target;
               listeners.removeElement(listener);
               if (listeners.size() == 0) {
                  _listenersById.remove(sourceID);
               }
            }
         }
      }
   }

   public static final void fireStateChanged(int state, long sourceID, long eventID, Object eventReference, Object context) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      Object target = null;
      Vector listeners = null;
      synchronized (_listenersById) {
         Enumeration listenerTargets = _listenersById.elements();

         while (listenerTargets.hasMoreElements()) {
            target = listenerTargets.nextElement();
            if (target instanceof Vector) {
               listeners = (Vector)target;
               int size = listeners.size();

               for (int index = 0; index < size; index++) {
                  try {
                     ((NotificationsEngineListener)listeners.elementAt(index))
                        .notificationsEngineStateChanged(state, sourceID, eventID, eventReference, context);
                  } catch (Throwable var16) {
                  }
               }
            } else if (target instanceof NotificationsEngineListener) {
               try {
                  ((NotificationsEngineListener)target).notificationsEngineStateChanged(state, sourceID, eventID, eventReference, context);
               } catch (Throwable var15) {
               }
            }
         }
      }
   }

   public static final void fireDefer(long sourceID, long eventID, Object eventReference, Object context) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      Vector listeners = null;
      synchronized (_listenersById) {
         Object target = getListenerByID(sourceID);
         NotificationsEngineListener listener = null;
         if (target != null) {
            if (!(target instanceof Vector)) {
               if (target instanceof NotificationsEngineListener) {
                  try {
                     listener = (NotificationsEngineListener)target;
                     listener.deferredEventWasSuperseded(sourceID, eventID, eventReference, context);
                  } catch (Throwable var14) {
                  }
               }
            } else {
               listeners = (Vector)target;
               int size = listeners.size();

               for (int index = 0; index < size; index++) {
                  try {
                     listener = (NotificationsEngineListener)listeners.elementAt(index);
                     listener.deferredEventWasSuperseded(sourceID, eventID, eventReference, context);
                  } catch (Throwable var15) {
                  }
               }
            }
         }
      }
   }

   public static final void fireProceed(long sourceID, long eventID, Object eventReference, Object context) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      Vector listeners = null;
      synchronized (_listenersById) {
         Object target = getListenerByID(sourceID);
         NotificationsEngineListener listener = null;
         if (target != null) {
            if (!(target instanceof Vector)) {
               if (target instanceof NotificationsEngineListener) {
                  try {
                     listener = (NotificationsEngineListener)target;
                     listener.proceedWithDeferredEvent(sourceID, eventID, eventReference, context);
                  } catch (Throwable var14) {
                  }
               }
            } else {
               listeners = (Vector)target;
               int size = listeners.size();

               for (int index = 0; index < size; index++) {
                  try {
                     listener = (NotificationsEngineListener)listeners.elementAt(index);
                     listener.proceedWithDeferredEvent(sourceID, eventID, eventReference, context);
                  } catch (Throwable var15) {
                  }
               }
            }
         }
      }
   }

   private static final Object getListenerByID(long sourceID) {
      Object target = _listenersById.get(sourceID);
      if (target == null) {
         sourceID = getParentSourceID(sourceID);
         target = _listenersById.get(sourceID);
      }

      return target;
   }

   public static final void registerConsequence(long consequenceID, Consequence consequence) {
      synchronized (_consequences) {
         _consequences.put(consequenceID, consequence);
      }
   }

   public static final void deregisterConsequence(long consequenceID) {
      synchronized (_consequences) {
         _consequences.remove(consequenceID);
      }
   }

   public static final Consequence getConsequence(long consequenceID) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return (Consequence)_consequences.get(consequenceID);
   }

   public static final LongEnumeration enumerateConsequenceIds() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return _consequences.keys();
   }

   public static final long getLastEventDate() {
      return _engine.getLastEventDate();
   }

   public static final boolean isImmediateEventOccuring(long sourceIdLong) {
      if (_engine == null) {
         _engine = (NotificationsEngine)ApplicationRegistry.getApplicationRegistry().get(6720217471165517311L);
      }

      return _engine != null ? _engine.isImmediateEventOccuring(sourceIdLong) : false;
   }

   private static final void sourceUpdated() {
      for (int i = _sourceListeners.size() - 1; i >= 0; i--) {
         try {
            ((NotificationsManager$SourceListener)_sourceListeners.elementAt(i)).sourceUpdated();
         } catch (Throwable var2) {
         }
      }
   }

   public static final void addSourceChangedListener(NotificationsManager$SourceListener listener) {
      _sourceListeners.addElement(listener);
   }

   public static final void removeSourceChangedListener(NotificationsManager$SourceListener listener) {
      _sourceListeners.removeElement(listener);
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _sourcesByObject = ar.getHashtable(-4056382830017962026L);
      _sourcesById = ar.getLongHashtable(-8981944619630607405L);
      _consequences = ar.getLongHashtable(957977102827493287L);
      _listenersById = ar.getLongHashtable(2741810622403670030L);
      _sourceListeners = ar.getVector(2160240915005830592L);
      _engine = (NotificationsEngine)ar.get(6720217471165517311L);
   }
}
