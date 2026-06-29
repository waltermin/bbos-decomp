package net.rim.device.api.util;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.util.ExternalStringPattern;
import net.rim.vm.TraceBack;

public final class StringPatternRepository {
   private StringPatternContainer _container = new StringPatternContainer(new StringPattern[0]);
   private static final long NAME_OF_OBJECT;
   private static StringPatternRepository _patternRepository;

   private static final void assertPermission() {
      ApplicationControl.assertIPCAllowed(true);
   }

   private StringPatternRepository() {
   }

   private final synchronized void add(StringPattern pattern) {
      StringPattern[] origPatterns = this._container.getElements();
      int count = origPatterns.length;
      StringPattern[] newPatterns = new StringPattern[count + 1];
      System.arraycopy(origPatterns, 0, newPatterns, 0, count);
      newPatterns[count] = pattern;
      this._container = new StringPatternContainer(newPatterns);
   }

   private final synchronized void remove(long[] ids) {
      StringPattern[] origPatterns = this._container.getElements();
      StringPattern[] newPatterns = new StringPattern[origPatterns.length];
      System.arraycopy(origPatterns, 0, newPatterns, 0, origPatterns.length);

      for (int i = 0; i < ids.length; i++) {
         for (int j = 0; j < origPatterns.length; j++) {
            StringPattern var10000 = origPatterns[j];
            if (origPatterns[j] instanceof ExternalStringPattern) {
               ExternalStringPattern pattern = (ExternalStringPattern)var10000;
               if (pattern.getID() == ids[i]) {
                  Arrays.remove(newPatterns, origPatterns[j]);
                  break;
               }
            }
         }
      }

      this._container = new StringPatternContainer(newPatterns);
   }

   private final synchronized StringPatternContainer getContainer() {
      return this._container;
   }

   public static final void addPattern(StringPattern pattern) {
      assertPermission();
      if (pattern == null) {
         throw new IllegalArgumentException();
      }

      getInstance().add(pattern);
   }

   public static final void removeExternalPatterns(long[] ids) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      getInstance().remove(ids);
   }

   private static final StringPatternRepository getInstance() {
      if (_patternRepository != null) {
         return _patternRepository;
      }

      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      synchronized (appRegistry) {
         _patternRepository = (StringPatternRepository)appRegistry.get(175320883679689398L);
         if (_patternRepository == null) {
            _patternRepository = new StringPatternRepository();
            appRegistry.put(175320883679689398L, _patternRepository);
         }
      }

      return _patternRepository;
   }
}
