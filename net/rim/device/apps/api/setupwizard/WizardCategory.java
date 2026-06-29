package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.i18n.ResourceBundle;

public class WizardCategory implements WizardPage {
   private String _title;
   private int _priority;
   private long _uid;
   private WizardCategory _parent;
   private ResourceBundle _rb;
   private int _rbTitleId;

   public boolean discardWizard() {
      return true;
   }

   public boolean saveWizard() {
      return true;
   }

   public long getUid() {
      return this._uid;
   }

   @Override
   public WizardCategory getCategory() {
      return this._parent;
   }

   @Override
   public int showPage(int lastCommand, int context) {
      return 0;
   }

   @Override
   public int getPriority() {
      return this._priority;
   }

   @Override
   public String getTitle() {
      return this._title;
   }

   @Override
   public boolean canSkipWizard() {
      return true;
   }

   @Override
   public boolean isHidden() {
      return false;
   }

   @Override
   public void setProgress(int current, int visibleMax, int absoluteMax) {
   }

   @Override
   public int getPageCount() {
      return 0;
   }

   @Override
   public void reloadTitle() {
      if (this._rb != null) {
         this._title = this._rb.getString(this._rbTitleId);
      }
   }

   @Override
   public void setLogManager(LogManager log) {
   }

   @Override
   public boolean equals(Object rhs) {
      return rhs instanceof WizardCategory ? this._uid == ((WizardCategory)rhs).getUid() : false;
   }

   protected WizardCategory(String title, int priority, WizardCategory parent, long uid) {
      this._title = title;
      this._priority = priority;
      this._uid = uid;
      this._parent = parent;
   }

   protected WizardCategory(ResourceBundle rb, int rbTitleId, int priority, WizardCategory parent, long uid) {
      this(rb.getString(rbTitleId), priority, parent, uid);
      this._rb = rb;
      this._rbTitleId = rbTitleId;
   }
}
