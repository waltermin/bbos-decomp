package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.apps.api.ui.ProgressIndicator;
import net.rim.device.apps.api.ui.ProgressRunnable;

final class ParsingProgressThread extends BaseParsingThread implements ProgressRunnable, KeyListener {
   private ProgressIndicator _progressIndicator = (ProgressIndicator)(new Object(4));

   ParsingProgressThread(DocViewParser coreData, int consecutiveBlockCount, DocViewParserObj notifyObj) {
      super(coreData, consecutiveBlockCount, notifyObj);
   }

   @Override
   final void doStart() {
      this._progressIndicator.setProgressRunnable(this);
      this._progressIndicator
         .initialize(ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView").getString(38), "", 0, 100, 1);
      this._progressIndicator.run();
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\u001b':
            if (super._coreData != null) {
               new ParsingProgressThread$1(this).start();
            }

            return true;
         default:
            return false;
      }
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   private final boolean addKeyListenerToProgressInd() {
      Screen activeScreen = Ui.getUiEngine().getActiveScreen();
      if (!(activeScreen instanceof Object)) {
         return false;
      }

      ((Screen)activeScreen).addKeyListener(this);
      return true;
   }

   @Override
   public final void setProgressIndicator(ProgressIndicator progressIndicator) {
   }

   @Override
   public final void stop() {
   }

   @Override
   protected final boolean onStartLoop() {
      return this.addKeyListenerToProgressInd();
   }

   @Override
   protected final void onStop(boolean success) {
      if (success) {
         this._progressIndicator.setProgress(100);

         label21:
         try {
            Thread.sleep(150);
         } finally {
            break label21;
         }
      }

      this._progressIndicator.dismiss();
   }

   @Override
   protected final void onEndLoop(boolean success, int succeededBlockCount) {
      int percent = super._coreData.getCurrentParsePercentage();
      int iPercentData = ((success ? succeededBlockCount - 1 : succeededBlockCount) * 100 + percent) / super._consecutiveBlockCount;
      if (iPercentData <= 100) {
         this._progressIndicator.setProgress(iPercentData);
      }
   }
}
