package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class ProgressIndicator {
   VerticalFieldManager _vfm = (VerticalFieldManager)(new Object(1152921504606846976L));
   protected PopupScreen _popupScreen = (PopupScreen)(new Object(this._vfm));
   GaugeField _gaugeField;
   LabelField _labelField = (LabelField)(new Object("", 12884901952L));
   ProgressRunnable _progressRunnable;
   InvokeLaterRunnable _invokeLaterRunnable = new InvokeLaterRunnable();
   private ProgressIndicator$InitializeRunnable _initializeRunnable = new ProgressIndicator$InitializeRunnable(null);
   private UiApplication _uiApplication = UiApplication.getUiApplication();
   private ProgressIndicator$SetProgressRunnable _setProgressRunnable = new ProgressIndicator$SetProgressRunnable(null);

   public ProgressIndicator(long gaugeStyle) {
      this._gaugeField = (GaugeField)(new Object(null, 0, 1, 0, gaugeStyle));
      this._vfm.add(this._labelField);
      this._vfm.add(this._gaugeField);
   }

   public void setProgressRunnable(ProgressRunnable progressRunnable) {
      this._progressRunnable = progressRunnable;
   }

   public void run() {
      this._progressRunnable.setProgressIndicator(this);
      ((Thread)(new Object(this._progressRunnable))).start();
   }

   public void initialize(String popupLabel, String gaugeLabel, int bottom, int top, int start) {
      this._initializeRunnable.setParameters(this._labelField, popupLabel, this._gaugeField, gaugeLabel, bottom, top, start, this._popupScreen);
      this._uiApplication.invokeLater(this._initializeRunnable);
   }

   private void checkForComplete() {
      boolean done = this._invokeLaterRunnable.doneProcessing();
      this._invokeLaterRunnable.resetDoneProcessing();
      if (done) {
         this._uiApplication.invokeLater(this._invokeLaterRunnable);
      }
   }

   public void setProgress(int value) {
      synchronized (this._invokeLaterRunnable) {
         if (this._gaugeField != null) {
            this._setProgressRunnable.setParameters(this._gaugeField, value);
            this._invokeLaterRunnable.setRunnable(this._setProgressRunnable);
            this.checkForComplete();
         }
      }
   }

   public void dismiss() {
      synchronized (this._invokeLaterRunnable) {
         this._gaugeField = null;
         this._invokeLaterRunnable.setRunnable(new ProgressIndicator$1(this));
         this.checkForComplete();
      }
   }

   public void setStatusString(String label, boolean wait) {
      ProgressIndicator$SetStatusStringRunnable setStatusStringRunnable = new ProgressIndicator$SetStatusStringRunnable(this._labelField, label);
      if (wait) {
         this._uiApplication.invokeAndWait(setStatusStringRunnable);
      } else {
         this._uiApplication.invokeLater(setStatusStringRunnable);
      }
   }
}
