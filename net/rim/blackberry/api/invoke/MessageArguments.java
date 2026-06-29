package net.rim.blackberry.api.invoke;

import javax.wireless.messaging.TextMessage;
import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.Message;

public final class MessageArguments extends ApplicationArguments {
   private Message _message;
   private TextMessage _textMessage;
   private Folder _folder;
   public static final String ARG_DEFAULT = "mainview";
   public static final String ARG_SAVED = "saved";
   public static final String ARG_SEARCH = "search";
   public static final String ARG_NEW = "new";
   public static final String ARG_NEW_PIN = "newpin";
   public static final String ARG_NEW_SMS = "newsms";
   public static final String ARG_NEW_MMS = "newmms";

   public MessageArguments() {
      super._args = new String[]{"mainview"};
   }

   public MessageArguments(String arg) {
      if (arg == null
         || !arg.equals("saved")
            && !arg.equals("search")
            && !arg.equals("mainview")
            && !arg.equals("new")
            && !arg.equals("newpin")
            && !arg.equals("newsms")
            && !arg.equals("newmms")) {
         throw new IllegalArgumentException("Invalid argument specified");
      }

      super._args = new String[]{arg};
   }

   public MessageArguments(String arg, String to, String subject, String body) {
      if (arg == null || !arg.equals("new") && !arg.equals("newpin")) {
         throw new IllegalArgumentException("Invalid argument specified.");
      }

      super._args = new String[]{arg, to, subject, body};
   }

   public MessageArguments(Message message) {
      if (message.getMessageType() == Message.EMAIL_MESSAGE) {
         super._args = new String[]{"new"};
      } else {
         super._args = new String[]{"newpin"};
      }

      this._message = message;
   }

   public MessageArguments(TextMessage textMessage) {
      super._args = new String[]{"newsms"};
      this._textMessage = textMessage;
   }

   public MessageArguments(Folder folder) {
      this._folder = folder;
   }

   final Message getMessageArg() {
      return this._message;
   }

   final TextMessage getTextMessageArg() {
      return this._textMessage;
   }

   final Folder getFolderArg() {
      return this._folder;
   }
}
