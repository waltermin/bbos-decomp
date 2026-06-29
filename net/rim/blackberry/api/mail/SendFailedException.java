package net.rim.blackberry.api.mail;

public class SendFailedException extends MessagingException {
   private Address[] _validSent;
   private Address[] _validUnsent;
   private Address[] _invalid;
   static String ERR_SERVICE_RECORD = "MAIL_API: No RIM Service Record.";
   static String ERR_MESSAGING_SERVICE = "MAIL_API: No RIM Messaging Service.";

   public SendFailedException() {
   }

   public SendFailedException(String s) {
      super(s);
   }

   public SendFailedException(String msg, Address[] validSent, Address[] validUnsent, Address[] invalid) {
      super(msg);
      this._validSent = validSent;
      this._validUnsent = validUnsent;
      this._invalid = invalid;
   }

   public Address[] getInvalidAddresses() {
      return this._invalid;
   }

   public Address[] getValidSentAddresses() {
      return this._validSent;
   }

   public Address[] getValidUnsentAddresses() {
      return this._validUnsent;
   }
}
