package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.container.HorizontalFieldManager;

public class WizardButtonBar extends HorizontalFieldManager {
   private FieldChangeListener _changeListener;
   private WizardButtonBar$WizardButtonField _cancelButton;
   private WizardButtonBar$WizardButtonField _backButton;
   private WizardButtonBar$WizardButtonField _nextButton;
   int _navigationMode;
   private long _buttonsDirection;
   private boolean _buttonInvokeOverride;
   public static final int POSITION_NORMAL = 0;
   public static final int POSITION_FIRST = 1;
   public static final int POSITION_LAST = 2;
   private static final int BUTTON_FONT_SIZE_PT = 5;

   public WizardButtonBar(int navigationMode) {
      super(12884901888L);
      this.resetFonts();
      this._navigationMode = navigationMode;
      this._buttonsDirection = this.buttonsDirection();
      this._cancelButton = new WizardButtonBar$WizardButtonField(this, 0);
      this._backButton = new WizardButtonBar$WizardButtonField(this, 1);
      this._nextButton = new WizardButtonBar$WizardButtonField(this, 2);
      switch (navigationMode) {
         case 0:
            if (this._buttonsDirection == 549755813888L) {
               this.add(this._cancelButton);
               this.add((Field)(new Object(" ")));
               this.add(this._backButton);
               this.add(this._nextButton);
               return;
            }

            this.add(this._nextButton);
            this.add(this._backButton);
            this.add((Field)(new Object(" ")));
            this.add(this._cancelButton);
            return;
         case 1:
         default:
            this.add(this._nextButton);
            return;
         case 2:
            this._nextButton = new WizardButtonBar$WizardButtonField(this, 3);
            if (this._buttonsDirection == 549755813888L) {
               this.add(this._backButton);
               this.add((Field)(new Object(" ")));
               this.add(this._nextButton);
               return;
            } else {
               this.add(this._nextButton);
               this.add((Field)(new Object(" ")));
               this.add(this._backButton);
            }
         case -1:
      }
   }

   public WizardButtonBar() {
      this(0);
   }

   protected long buttonsDirection() {
      int mask = -65536;
      int currentLocale = Locale.getDefault().getCode() & mask;
      return (1634861056 & mask) != currentLocale && (1751449600 & mask) != currentLocale ? 549755813888L : 274877906944L;
   }

   @Override
   public void setChangeListener(FieldChangeListener changeListener) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void resetFonts() {
      int fontSize = Ui.convertSize(5, 3, 0);
      this.setFont(Font.getDefault().derive(1, fontSize));
   }

   private void handleButtonClick(WizardButtonBar$WizardButtonField button) {
      if (this._changeListener != null && button != null) {
         this._changeListener.fieldChanged(this, button.getCommand());
      }
   }

   @Override
   public boolean keyChar(char key, int status, int time) {
      if (key == '\n' || key == ' ') {
         Field focus = this.getFieldWithFocus();
         if (focus instanceof WizardButtonBar$WizardButtonField) {
            this.handleButtonClick((WizardButtonBar$WizardButtonField)focus);
            return true;
         }
      }

      return super.keyChar(key, status, time);
   }

   public void invokeNext() {
      this.invokeButton(this._nextButton);
   }

   public void invokeBack() {
      this.invokeButton(this._backButton);
   }

   public void invokeCancel() {
      this.invokeButton(this._cancelButton);
   }

   private void invokeButton(WizardButtonBar$WizardButtonField button) {
      if (button != null) {
         this._buttonInvokeOverride = true;
         if (button.getManager() != null) {
            button.setFocus();
         }

         this._buttonInvokeOverride = false;
         Application.getApplication().invokeLater(new WizardButtonBar$ButtonInvoker(this, button));
      }
   }

   private void reloadLabel(WizardButtonBar$WizardButtonField button) {
      if (button != null) {
         button.loadLabel();
      }
   }

   public void reloadLabels() {
      this.reloadLabel(this._cancelButton);
      this.reloadLabel(this._backButton);
      this.reloadLabel(this._nextButton);
   }

   @Override
   protected int firstFocus(int direction) {
      return this._buttonInvokeOverride || this._navigationMode != 0 && this._navigationMode != 2 ? super.firstFocus(direction) : this._nextButton.getIndex();
   }
}
