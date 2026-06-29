package net.rim.device.apps.api.framework.registration;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.model.CompoundRecognizer;
import net.rim.device.apps.api.framework.model.Recognizer;

public class RecognizerRepository {
   private static final long RIM_RECOGNIZER_REPOSITORY_ID;
   private static LongHashtable _recognizerRepository = ApplicationRegistry.getApplicationRegistry().getLongHashtable(4942770380214593225L);

   private RecognizerRepository() {
   }

   public static void registerRecognizer(long objectTypeId, Recognizer recognizer) {
      synchronized (_recognizerRepository) {
         Recognizer element = (Recognizer)_recognizerRepository.get(objectTypeId);
         if (element != null) {
            CompoundRecognizer recognizers;
            if (!(element instanceof CompoundRecognizer)) {
               recognizers = new CompoundRecognizer();
               recognizers.addRecognizer(element);
               _recognizerRepository.put(objectTypeId, recognizers);
            } else {
               recognizers = (CompoundRecognizer)element;
            }

            recognizers.addRecognizer(recognizer);
         } else {
            _recognizerRepository.put(objectTypeId, recognizer);
         }
      }
   }

   public static Recognizer getRecognizers(long objectTypeId) {
      return (Recognizer)_recognizerRepository.get(objectTypeId);
   }

   public static void unregisterRecognizer(long objectTypeId) {
      synchronized (_recognizerRepository) {
         _recognizerRepository.remove(objectTypeId);
      }
   }
}
