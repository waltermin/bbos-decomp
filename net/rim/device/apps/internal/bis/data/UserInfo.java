package net.rim.device.apps.internal.bis.data;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.Arrays;

public final class UserInfo {
   private int _features;
   private int _features2;
   private int _numSupportedMailboxes;
   private Mailbox[] _mailboxes;
   private String _pin;
   private String _username;
   private boolean _hasHostedMailbox;
   private UserInfo$MailboxComparator _mailboxComparator = new UserInfo$MailboxComparator();
   private long _timestamp;
   private Locale _locale;
   private boolean _autoAuth;
   private static final int USER_PERSONALIZATION_ENABLED;
   private static final int HOSTED_MAILBOX_ENABLED;
   private static final int AUTO_BCC_ENABLED;
   private static final int CHANGE_REPLYTO_ENABLED;
   private static final int BBMAIL_PLAN;
   private static final int AUTO_FORWARD_ENABLED;
   private static final int MARKETING_TEXT_ENABLED;

   public final Mailbox getMailbox(String description) {
      Mailbox mailbox = null;
      if (this._mailboxes != null) {
         int numMailboxes = this._mailboxes.length;
         Mailbox currentMailbox = null;

         for (int i = 0; i < numMailboxes; i++) {
            currentMailbox = this._mailboxes[i];
            String currentMailboxDescription = currentMailbox.getDescription();
            if (currentMailboxDescription.equals(description)) {
               return currentMailbox;
            }
         }
      }

      return mailbox;
   }

   public final Mailbox getMailboxByEmail(String email) {
      Mailbox mailbox = null;
      if (this._mailboxes != null) {
         int numMailboxes = this._mailboxes.length;
         Mailbox currentMailbox = null;

         for (int i = 0; i < numMailboxes; i++) {
            currentMailbox = this._mailboxes[i];
            String currentMailboxEmail = currentMailbox.getEmail();
            if (currentMailboxEmail.equals(email)) {
               return currentMailbox;
            }
         }
      }

      return mailbox;
   }

   public final Mailbox[] getMailboxes() {
      return this._mailboxes;
   }

   public final String getPIN() {
      return this._pin;
   }

   public final void setPIN(String pin) {
      this._pin = pin;
   }

   public final String getUsername() {
      return this._username;
   }

   public final void setUsername(String username) {
      this._username = username;
   }

   public final long getTimeStamp() {
      return this._timestamp;
   }

   public final void setTimeStamp(long timestamp) {
      this._timestamp = timestamp;
   }

   public final Locale getLocale() {
      return this._locale;
   }

   public final void setLocale(Locale locale) {
      this._locale = locale;
   }

   public final void addMailbox(Mailbox mailbox) {
      if (this._mailboxes == null) {
         this._mailboxes = new Mailbox[0];
      }

      Arrays.add(this._mailboxes, mailbox);
      Arrays.sort(this._mailboxes, this._mailboxComparator);
      if (mailbox.getMailboxType() == 2) {
         this._hasHostedMailbox = true;
      }
   }

   public final void removeMailbox(Mailbox mailbox) {
      if (this._mailboxes != null) {
         Arrays.remove(this._mailboxes, mailbox);
         Arrays.sort(this._mailboxes, this._mailboxComparator);
         if (mailbox.getMailboxType() == 2) {
            this._hasHostedMailbox = false;
         }
      }
   }

   public final boolean hasMaxMailboxes() {
      boolean result = false;
      if (this._mailboxes != null && this._mailboxes.length >= this.getNumSupportedMailboxes()) {
         result = true;
      }

      return result;
   }

   public final boolean hasHostedMailbox() {
      return this._hasHostedMailbox;
   }

   public final void setFeatures(int features) {
      this._features = features;
   }

   public final void setFeatures2(int features2) {
      this._features2 = features2;
   }

   public final boolean isUserPersonalizationEnabled() {
      return (this._features & 8388608) != 0;
   }

   public final boolean isAutoBCCEnabled() {
      return (this._features & 1073741824) != 0;
   }

   public final boolean isChangeReplyToEnabled() {
      return (this._features2 & 8) != 0;
   }

   public final boolean isAutoForwardEnabled() {
      return (this._features2 & 128) != 0;
   }

   public final int getNumSupportedMailboxes() {
      return this._numSupportedMailboxes;
   }

   public final void setNumSupportedMailboxes(int numSupportedMailboxes) {
      this._numSupportedMailboxes = numSupportedMailboxes;
   }

   public final boolean isBBMail() {
      return (this._features2 & 64) != 0;
   }

   public final boolean isHostedMailboxEnabled() {
      return (this._features & 33554432) != 0;
   }

   public final boolean isAutoAuth() {
      return this._autoAuth;
   }

   public final void setAutoAuth(boolean autoAuth) {
      this._autoAuth = autoAuth;
   }

   public final Mailbox getHostedMailbox() {
      if (this._mailboxes != null) {
         int numMailboxes = this._mailboxes.length;

         for (int i = 0; i < numMailboxes; i++) {
            if (this._mailboxes[i].getMailboxType() == 2) {
               return this._mailboxes[i];
            }
         }
      }

      return null;
   }
}
