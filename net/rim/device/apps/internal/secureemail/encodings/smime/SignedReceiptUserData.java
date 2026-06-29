package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.util.Persistable;

public final class SignedReceiptUserData implements Persistable {
   private String _to;
   private String _cc;
   private String _bcc;
   private String _subject;
   private long _sent;

   public SignedReceiptUserData(String to, String cc, String bcc, String subject, long sent) {
      this._to = to;
      this._cc = cc;
      this._bcc = bcc;
      this._subject = subject;
      this._sent = sent;
   }

   public final String getTo() {
      return this._to;
   }

   public final String getCC() {
      return this._cc;
   }

   public final String getBCC() {
      return this._bcc;
   }

   public final String getSubject() {
      return this._subject;
   }

   public final long getSent() {
      return this._sent;
   }
}
