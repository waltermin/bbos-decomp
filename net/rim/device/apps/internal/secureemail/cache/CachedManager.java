package net.rim.device.apps.internal.secureemail.cache;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.secureemail.SecureEmailMessageBlockManager;

public class CachedManager extends CachedField {
   private Vector _cachedFields = (Vector)(new Object());
   private Vector _cachedHeaderFields = (Vector)(new Object());
   private Vector _cachedFooterFields = (Vector)(new Object());

   protected Manager createManager(Object context) {
      return new SecureEmailMessageBlockManager(1152921504606846976L);
   }

   @Override
   public void fillManager(Manager manager, Object context) {
      Manager subManager = this.createManager(context);
      if (!this.endsWithSeparator(manager)) {
         subManager.add((Field)(new Object()));
      }

      this.fillManagerHeader(subManager, context);
      Enumeration e = this._cachedFields.elements();

      while (e.hasMoreElements()) {
         ((CachedField)e.nextElement()).fillManager(subManager, context);
      }

      this.fillManagerFooter(subManager, context);
      manager.add(subManager);
   }

   public boolean hasHeaderFields() {
      return this._cachedHeaderFields.size() > 0;
   }

   public void fillManagerHeader(Manager manager, Object context) {
      Enumeration e = this._cachedHeaderFields.elements();

      while (e.hasMoreElements()) {
         ((CachedField)e.nextElement()).fillManager(manager, context);
      }
   }

   public boolean hasFooterFields() {
      return this._cachedFooterFields.size() > 0;
   }

   public void fillManagerFooter(Manager manager, Object context) {
      Enumeration e = this._cachedFooterFields.elements();

      while (e.hasMoreElements()) {
         ((CachedField)e.nextElement()).fillManager(manager, context);
      }
   }

   @Override
   public void fillStringBuffer(StringBuffer stringBuffer, Object context) {
      Enumeration e = this._cachedFields.elements();

      while (e.hasMoreElements()) {
         ((CachedField)e.nextElement()).fillStringBuffer(stringBuffer, context);
      }
   }

   @Override
   public void setEmailMessageModel(EmailMessageModel emailMessageModel, ServiceRecord serviceRecord) {
      super.setEmailMessageModel(emailMessageModel, serviceRecord);
      this.setEmailMessageModel(emailMessageModel, serviceRecord, this._cachedFields);
      this.setEmailMessageModel(emailMessageModel, serviceRecord, this._cachedHeaderFields);
      this.setEmailMessageModel(emailMessageModel, serviceRecord, this._cachedFooterFields);
   }

   private void setEmailMessageModel(EmailMessageModel emailMessageModel, ServiceRecord serviceRecord, Vector fields) {
      Enumeration e = fields.elements();

      while (e.hasMoreElements()) {
         ((CachedField)e.nextElement()).setEmailMessageModel(emailMessageModel, serviceRecord);
      }
   }

   public void addField(CachedField cachedField) {
      this.addField(cachedField, this._cachedFields);
   }

   public void addHeaderField(CachedField cachedField) {
      this.addField(cachedField, this._cachedHeaderFields);
   }

   public void addFooterField(CachedField cachedField) {
      this.addField(cachedField, this._cachedFooterFields);
   }

   private void addField(CachedField cachedField, Vector fields) {
      cachedField.setManager(this);
      fields.addElement(cachedField);
   }
}
