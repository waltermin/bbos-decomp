package net.rim.device.apps.api.framework.model;

import net.rim.device.api.util.Arrays;

public class CompoundRecognizer implements Recognizer {
   protected Recognizer[] _recognizers;

   @Override
   public synchronized boolean recognize(Object o) {
      int length = this._recognizers.length;

      for (int i = 0; i < length; i++) {
         if (this._recognizers[i].recognize(o)) {
            return true;
         }
      }

      return false;
   }

   public synchronized void addRecognizer(Recognizer recognizer) {
      Arrays.add(this._recognizers, recognizer);
   }

   public synchronized Recognizer[] getRecognizers() {
      return this._recognizers;
   }

   public CompoundRecognizer() {
      this._recognizers = new Recognizer[0];
   }

   public CompoundRecognizer(Recognizer[] recognizers) {
      int length = recognizers.length;
      this._recognizers = new Recognizer[length];
      System.arraycopy(recognizers, 0, this._recognizers, 0, length);
   }
}
