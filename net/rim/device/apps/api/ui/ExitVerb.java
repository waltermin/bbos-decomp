package net.rim.device.apps.api.ui;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

public class ExitVerb extends Verb {
   private int _invokeAction;
   private String _description;
   private ResourceBundleFamily _descriptionFamily;
   private int _descriptionId;
   private Confirmation _confirmation;
   public static final int POP_SCREEN = 0;
   public static final int SYSTEM_EXIT = 1;
   public static final int APP_REQUEST_BACKGROUND = 2;
   public static final int SHOW_RIBBON = 3;
   public static final int NOTHING = 4;

   public ExitVerb(int invokeAction, Confirmation confirmation) {
      this(268501008, CommonResource.getBundle(), 9, invokeAction, confirmation);
   }

   public ExitVerb(int menuOrdering, String description, int invokeAction, Confirmation confirmation) {
      super(menuOrdering);
      this._description = description;
      this._invokeAction = invokeAction;
      this._confirmation = confirmation;
   }

   public ExitVerb(int menuOrdering, ResourceBundleFamily descriptionFamily, int descriptionId, int invokeAction, Confirmation confirmation) {
      super(menuOrdering);
      this._descriptionFamily = descriptionFamily;
      this._descriptionId = descriptionId;
      this._invokeAction = invokeAction;
      this._confirmation = confirmation;
   }

   public static Verb createCancelVerb(int invokeAction, Confirmation confirmation) {
      return new ExitVerb(268500992, CommonResource.getBundle(), 19, invokeAction, confirmation);
   }

   public static Verb createCloseVerb(int invokeAction, Confirmation confirmation) {
      return new ExitVerb(268501008, CommonResource.getBundle(), 9, invokeAction, confirmation);
   }

   public void setAction(int invokeAction) {
      this._invokeAction = invokeAction;
   }

   @Override
   public Object invoke(Object parameter) {
      if (this._confirmation != null && !this._confirmation.confirm(this, parameter)) {
         return null;
      }

      switch (this._invokeAction) {
         case 0:
         default:
            this.invokePopScreen();
            return this;
         case 1:
            this.invokeSystemExit();
            return this;
         case 2:
            this.invokeAppRequestBackground();
            return this;
         case 3:
            this.invokeShowRibbon();
         case -1:
            return this;
      }
   }

   protected void invokePopScreen() {
      UiEngine uiEngine = Ui.getUiEngine();
      uiEngine.popScreen(uiEngine.getActiveScreen());
   }

   protected void invokeSystemExit() {
      System.exit(0);
   }

   protected void invokeAppRequestBackground() {
      Application.getApplication().requestBackground();
   }

   protected void invokeShowRibbon() {
      ApplicationManager.getApplicationManager().requestForegroundForConsole();
   }

   @Override
   public String toString() {
      return this._description != null ? this._description : this._descriptionFamily.getString(this._descriptionId);
   }
}
