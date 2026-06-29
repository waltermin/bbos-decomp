package net.rim.device.internal.crypto.fips;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.i18n.CommonResource;

final class SelfTestsStartupDialog extends PopupScreen {
   private String[] _tests;
   private boolean[] _testResults;
   private SelfTests _selfTests;
   private ButtonField _okButton;
   private ButtonField _detailsButton;
   RichTextField _resultField;
   GaugeField _gaugeField;
   int _numTestsCompleted;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(-1504593620783806182L, "net.rim.device.internal.resource.SelfTests");

   SelfTestsStartupDialog(SelfTests selfTests, String[] tests) {
      super(new VerticalFieldManager());
      this._selfTests = selfTests;
      this._tests = tests;
      this._testResults = new boolean[tests.length];
      this.add(new RichTextField(_rb.getString(5), 36028797018963968L));
      this.add(new SeparatorField());
      this._gaugeField = new GaugeField(null, 0, this._tests.length, 0, 4);
      this.add(this._gaugeField);
      this._resultField = new RichTextField(null, 36028797019226112L);
      this.add(this._resultField);
   }

   final void display() {
      Ui.getUiEngine().pushGlobalScreen(this, Integer.MIN_VALUE, 0);
   }

   final void setTestPassed(int passedIndex) {
      this._testResults[passedIndex] = true;
      this.testCompleted();
   }

   final void setTestFailed(int failedIndex) {
      this._testResults[failedIndex] = false;
      this.testCompleted();
   }

   final void testCompleted() {
      this._numTestsCompleted++;
      synchronized (Application.getApplication().getAppEventLock()) {
         this._gaugeField.setValue(this._numTestsCompleted);
      }
   }

   final void testsPassed() {
      synchronized (Application.getApplication().getAppEventLock()) {
         this._resultField.setText(_rb.getString(2));
      }

      try {
         Thread.sleep(1000);
      } catch (InterruptedException var5) {
      }

      synchronized (Application.getApplication().getAppEventLock()) {
         Ui.getUiEngine().popScreen(this);
      }
   }

   private final void addFailButtons() {
      HorizontalFieldManager buttonManager = new HorizontalFieldManager(12884901888L);
      this._okButton = new ButtonField(CommonResource.getString(100));
      this._detailsButton = new ButtonField(_rb.getString(7));
      buttonManager.add(this._okButton);
      buttonManager.add(this._detailsButton);
      this.add(buttonManager);
   }

   final void testsFailed() {
      synchronized (Application.getApplication().getAppEventLock()) {
         this._resultField.setText(_rb.getString(3));
         this.addFailButtons();
         this._okButton.setFocus();
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      super.trackwheelClick(status, time);
      if (this._okButton != null) {
         Field fieldWithFocus = this.getLeafFieldWithFocus();
         if (fieldWithFocus == this._okButton) {
            Ui.getUiEngine().popScreen(this);
            return true;
         }

         if (fieldWithFocus == this._detailsButton) {
            this.showDetails();
            Ui.getUiEngine().popScreen(this);
         }
      }

      return true;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      boolean ret = super.keyChar(key, status, time);
      if (this._okButton != null) {
         Field fieldWithFocus = this.getLeafFieldWithFocus();
         if ((fieldWithFocus != this._okButton || key != '\n') && key != 27) {
            if (fieldWithFocus == this._detailsButton) {
               this.showDetails();
               Ui.getUiEngine().popScreen(this);
            }
         } else {
            Ui.getUiEngine().popScreen(this);
         }
      }

      return ret;
   }

   private final void showDetails() {
      SelfTestsDialog detailedDialog = new SelfTestsDialog(null, this._tests, true, this._testResults);
      detailedDialog.testsFailed();
      detailedDialog.display();
   }

   @Override
   public final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this._selfTests.dialogDisplayed();
      }
   }
}
