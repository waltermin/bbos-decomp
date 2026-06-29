package net.rim.device.apps.internal.ldap;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ldap.LDAPEntry;

public final class LDAPBrowserContainer {
   private LDAPEntry _entry;
   private Certificate _certificate;
   private byte[] _certificateID;
   private LDAPBrowserContext _context;
   private String _label;

   public LDAPBrowserContainer(LDAPEntry entry, Certificate certificate, byte[] certificateID, LDAPBrowserContext context) {
      if (entry != null && certificateID != null && context != null) {
         this._entry = entry;
         this._certificate = certificate;
         this._certificateID = certificateID;
         this._context = context;
         this._label = this.determineLabel();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final LDAPEntry getEntry() {
      return this._entry;
   }

   public final Certificate getCertificate() {
      return this._certificate;
   }

   public final byte[] getCertificateID() {
      return this._certificateID;
   }

   public final void setEntry(LDAPEntry entry) {
      if (entry == null) {
         throw new IllegalArgumentException();
      }

      this._entry = entry;
   }

   public final void setCertificate(Certificate certificate) {
      this._certificate = certificate;
      this._label = this.determineLabel();
   }

   public final String getLabel() {
      return this._label;
   }

   private final String determineLabel() {
      StringBuffer buffer = new StringBuffer();
      String friendlyName = null;

      try {
         friendlyName = this._context.getFriendlyName(this._entry, this._certificate);
      } catch (LDAPBrowserException var6) {
      }

      String email = null;

      try {
         email = this._context.getEmail(this._entry);
      } catch (LDAPBrowserException var5) {
      }

      if (friendlyName != null && friendlyName.length() > 0 && email != null && email.length() > 0) {
         buffer.append(friendlyName);
         buffer.append(',');
         buffer.append(' ');
         buffer.append(email);
      } else if (friendlyName != null && friendlyName.length() > 0) {
         buffer.append(friendlyName);
      } else if (email != null && email.length() > 0) {
         buffer.append(email);
      } else {
         ResourceBundleFamily rb = ResourceBundle.getBundle(-4732399874182263183L, "net.rim.device.apps.internal.ldap.resource.LDAP");
         buffer.append(rb.getString(78));
      }

      return buffer.toString();
   }
}
