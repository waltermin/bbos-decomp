package net.rim.device.internal.crypto.fips;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.i18n.CommonResource;

final class SelfTestsDialog extends PopupScreen implements ListFieldCallback {
   private String[] _tests;
   private ListField _listField;
   private boolean[] _testResults;
   private SelfTests _selfTests;
   private ButtonField _okButton;
   private VerticalFieldManager _vfmScroll;
   private boolean _startupRun;
   private final ResourceBundle _rb = ResourceBundle.getBundle(-1504593620783806182L, "net.rim.device.internal.resource.SelfTests");

   SelfTestsDialog(SelfTests selfTests, String[] tests, boolean startupRun, boolean[] testResults) {
      super(new VerticalFieldManager());
      this._startupRun = startupRun;
      this._selfTests = selfTests;
      this._tests = tests;
      if (testResults == null) {
         this._testResults = new boolean[tests.length];
      } else {
         this._testResults = testResults;
      }

      this._vfmScroll = new VerticalFieldManager(299067162755072L);
      this.add(new RichTextField(this._rb.getString(5), 36028797018963968L));
      this.add(new SeparatorField());
      this._listField = new ListField(tests.length);
      this._listField.setCallback(this);
      this._vfmScroll.add(this._listField);
      this._vfmScroll.add(new SeparatorField(1152921504606846976L));
      this.add(this._vfmScroll);
      this._listField.setFocus();
   }

   final void display() {
      if (this._startupRun) {
         Ui.getUiEngine().pushGlobalScreen(this, -2147483642, 2);
      } else {
         Ui.getUiEngine().pushScreen(this);
      }
   }

   final void setTestPassed(int passedIndex, boolean selectItem) {
      this._testResults[passedIndex] = true;
      synchronized (Application.getApplication().getAppEventLock()) {
         if (selectItem) {
            this._listField.setSelectedIndex(passedIndex);
         }

         this._listField.invalidate(passedIndex);
      }
   }

   final void setTestFailed(int failedIndex) {
   }

   final void testsPassed() {
      synchronized (Application.getApplication().getAppEventLock()) {
         RichTextField resultsField = new RichTextField(this._rb.getString(2), 36028797019226112L);
         this._vfmScroll.add(resultsField);
         if (!this._startupRun) {
            this.addOkButton();
            this._okButton.setFocus();
         } else {
            resultsField.setFocus();
         }
      }

      if (this._startupRun) {
         try {
            Thread.sleep(1000);
         } catch (InterruptedException var6) {
         }

         synchronized (Application.getApplication().getAppEventLock()) {
            this.closeDialog();
         }
      }
   }

   private final void addOkButton() {
      HorizontalFieldManager buttonManager = new HorizontalFieldManager(12884901888L);
      this._okButton = new ButtonField(CommonResource.getString(100));
      buttonManager.add(this._okButton);
      this._vfmScroll.add(buttonManager);
   }

   final void testsFailed() {
      synchronized (Application.getApplication().getAppEventLock()) {
         this._vfmScroll.add(new RichTextField(this._rb.getString(3), 36028797019226112L));
         this.addOkButton();
         this._okButton.setFocus();
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      if (listField == this._listField && index >= 0 && index < this._tests.length) {
         StringBuffer buff = new StringBuffer();
         buff.append((char)(this._testResults[index] ? '☑' : '☐'));
         buff.append(' ');
         buff.append(this._tests[index]);
         graphics.drawText(buff.toString(), 0, y, 0, width);
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final Object get(ListField listField, int index) {
      return listField == this._listField && index >= 0 && index < this._tests.length ? this._tests[index] : null;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      super.trackwheelClick(status, time);
      if (this._okButton != null) {
         this.closeDialog();
      }

      return true;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      boolean ret = super.keyChar(key, status, time);
      if (this._okButton != null && (this.getLeafFieldWithFocus() == this._okButton && key == '\n' || key == 27)) {
         this.closeDialog();
      }

      return ret;
   }

   private final void closeDialog() {
      Ui.getUiEngine().popScreen(this);
   }

   @Override
   public final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached && this._selfTests != null) {
         this._selfTests.dialogDisplayed();
      }
   }
}
