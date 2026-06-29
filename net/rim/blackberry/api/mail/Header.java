package net.rim.blackberry.api.mail;

public class Header {
   private String _name;
   private String _value;
   public static String TO = "To:";
   public static String DATE = "Date:";
   public static String FROM = "From:";
   public static String BCC = "Bcc:";
   public static String SENDER = "Sender:";
   public static String REPLY_TO = "Reply-To:";
   public static String SUBJECT = "Subject:";
   public static String CC = "Cc:";
   public static final String MESSAGE_ID = "Message-ID:";

   public Header(String name, String value) {
      this._name = name;
      this._value = value == null ? "" : value;
   }

   public String getName() {
      return this._name;
   }

   public String getValue() {
      return this._value;
   }
}
