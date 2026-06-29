package net.rim.device.apps.internal.bis.api.ui;

import java.util.Hashtable;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.internal.bis.ApplicationResources;

public class RefreshableScreen extends AppsMainScreen {
   protected String _errorMessage;
   protected String _statusMessage;

   public RefreshableScreen(long style) {
      super(style);
   }

   @Override
   protected void onDisplay() {
      super.onDisplay();
      if (this._errorMessage != null) {
         this.showErrorMessage();
      } else {
         if (this._statusMessage != null) {
            this.showStatusMessage();
         }
      }
   }

   @Override
   protected void onUndisplay() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   public void clearScreen() {
      throw null;
   }

   public void refresh(Hashtable screenParams) {
   }

   public void setError(String errorMessage) {
      this._errorMessage = errorMessage;
      if (this._errorMessage != null && this.isDisplayed()) {
         if (Application.isEventDispatchThread()) {
            this.showErrorMessage();
            return;
         }

         Application.getApplication().invokeLater(new RefreshableScreen$1(this));
      }
   }

   public void setStatus(String statusMessage) {
      this._statusMessage = statusMessage;
      if (this._statusMessage != null && this.isDisplayed()) {
         if (Application.isEventDispatchThread()) {
            this.showStatusMessage();
            return;
         }

         Application.getApplication().invokeLater(new RefreshableScreen$2(this));
      }
   }

   protected void showStatusMessage() {
      Dialog d = new Dialog(this._statusMessage, new Object[]{ApplicationResources.getString(39)}, new int[]{0, -805044220, 0, -805044213}, 0, null, 0);
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_information"));
      d.doModal();
      this._statusMessage = null;
   }

   protected void showErrorMessage() {
      Dialog d = new Dialog(this._errorMessage, new Object[]{ApplicationResources.getString(39)}, new int[]{0, -805044220, 0, -805044213}, 0, null, 0);
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_exclamation"));
      d.doModal();
      this._errorMessage = null;
   }

   public boolean canGoBack() {
      throw null;
   }

   public boolean canComeBack() {
      return true;
   }
}
