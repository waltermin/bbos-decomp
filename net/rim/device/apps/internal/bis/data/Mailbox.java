package net.rim.device.apps.internal.bis.data;

import java.util.Vector;
import net.rim.device.api.util.ObjectUtilities;

public final class Mailbox {
   private Boolean _hosted;
   private String _email;
   private String _protocol;
   private String _server;
   private String _userName;
   private String _password;
   private Boolean _useSSL;
   private String _friendlyName;
   private String _replyTo;
   private String _autoBCC;
   private String _autoForward;
   private Boolean _autoForwardAll;
   private String _description;
   private String _signature;
   private Integer _timeout;
   private Integer _port;
   private String _srcMboxID;
   private Boolean _deleteSync;
   private int _settings;
   private String _fieldPermissions;
   private Vector _filters;
   private String _secretQuestion;
   private Integer _secretQuestionId;
   private String _secretAnswer;
   public static final int MAILBOX_TYPE_UNKNOWN = 0;
   public static final int MAILBOX_TYPE_SIMPLE = 1;
   public static final int MAILBOX_TYPE_HOSTED = 2;
   public static final int MAILBOX_TYPE_PROPRIETARY = 3;
   public static final int MAILBOX_TYPE_ISP = 4;
   public static final int MAILBOX_TYPE_OWA = 5;
   public static final int MAILBOX_TYPE_SMC = 6;
   public static final String PROTOCOL_POP = "pop";
   public static final String PROTOCOL_IMAP = "imap";
   public static final String PROTOCOL_OWA = "owa";
   public static final String PROTOCOL_NATIVE = "native";
   private static final int SOURCE_MAILBOX_VALIDATED = 16;
   private static final int DELETE_SYNC_ENABLED = 67108864;
   private static final int FORWARD_MESSAGES_TO_DEVICE = 1048576;
   public static final int FIELD_HIDDEN = 0;
   public static final int FIELD_READONLY = 1;
   public static final int FIELD_ALTERABLE = 2;
   private static final int FIELD_PERMISSION_INDEX_DESCRIPTION = 0;
   private static final int FIELD_PERMISSION_INDEX_FRIENDLYNAME = 1;
   private static final int FIELD_PERMISSION_INDEX_ADDRESS = 2;
   private static final int FIELD_PERMISSION_INDEX_LOGIN = 3;
   private static final int FIELD_PERMISSION_INDEX_REPLYTO = 4;

   public final Boolean getHosted() {
      return this._hosted;
   }

   public final void setHosted(boolean hosted) {
      this._hosted = new Boolean(hosted);
   }

   public final String getEmail() {
      return this._email;
   }

   public final void setEmail(String email) {
      this._email = email;
   }

   public final String getProtocol() {
      return this._protocol;
   }

   public final void setProtocol(String protocol) {
      this._protocol = protocol;
   }

   public final String getServer() {
      return this._server;
   }

   public final void setServer(String server) {
      this._server = server;
   }

   public final String getDescription() {
      return this._description;
   }

   public final void setDescription(String description) {
      this._description = description;
   }

   public final String getUserName() {
      return this._userName;
   }

   public final void setUserName(String userName) {
      this._userName = userName;
   }

   public final String getPassword() {
      return this._password;
   }

   public final void setPassword(String password) {
      this._password = password;
   }

   public final Boolean getUseSSL() {
      return this._useSSL;
   }

   public final void setUseSSL(boolean useSSL) {
      this._useSSL = new Boolean(useSSL);
   }

   public final String getFriendlyName() {
      return this._friendlyName;
   }

   public final void setFriendlyName(String friendlyName) {
      this._friendlyName = friendlyName;
   }

   public final String getReplyTo() {
      return this._replyTo;
   }

   public final void setReplyTo(String replyTo) {
      this._replyTo = replyTo;
   }

   public final String getAutoBCC() {
      return this._autoBCC;
   }

   public final void setAutoBCC(String autoBCC) {
      this._autoBCC = autoBCC;
   }

   public final String getAutoForward() {
      return this._autoForward;
   }

   public final void setAutoForward(String autoForward) {
      this._autoForward = autoForward;
   }

   public final Boolean getAutoForwardAll() {
      return this._autoForwardAll;
   }

   public final void setAutoForwardAll(boolean autoForwardAll) {
      this._autoForwardAll = new Boolean(autoForwardAll);
   }

   public final String getSignature() {
      return this._signature;
   }

   public final void setSignature(String signature) {
      this._signature = signature;
   }

   public final void setDeleteSync(boolean deleteSync) {
      this._deleteSync = new Boolean(deleteSync);
   }

   public final Boolean getDeleteSync() {
      return this._deleteSync;
   }

   public final boolean isDeleteSyncEnabled() {
      return (this._settings & 67108864) == 67108864;
   }

   public final Integer getTimeout() {
      return this._timeout;
   }

   public final void setTimeout(int timeout) {
      this._timeout = new Integer(timeout);
   }

   public final Integer getPort() {
      return this._port;
   }

   public final void setPort(int port) {
      this._port = new Integer(port);
   }

   public final String getSrcMboxID() {
      return this._srcMboxID;
   }

   public final void setSrcMboxID(String srcMboxID) {
      this._srcMboxID = srcMboxID;
   }

   public final boolean isValid() {
      return (this._settings & 16) == 16;
   }

   public final void setValid() {
      if (!this.isValid()) {
         this._settings += 16;
      }
   }

   public final void setInvalid() {
      if (this.isValid()) {
         this._settings -= 16;
      }
   }

   public final void setForwardMessages(boolean forwardMessages) {
      if (forwardMessages) {
         this._settings |= 1048576;
      } else {
         this._settings &= -1048577;
      }
   }

   public final Boolean getForwardMessages() {
      return (this._settings & 1048576) == 1048576 ? new Boolean(true) : new Boolean(false);
   }

   public final boolean isForwardMessagesToDevice() {
      return (this._settings & 1048576) == 1048576;
   }

   public final void setSettings(int settings) {
      this._settings = settings;
   }

   public final int getSettings() {
      return this._settings;
   }

   public final void setFieldPermissions(String fieldPermissions) {
      this._fieldPermissions = fieldPermissions;
   }

   public final String getFieldPermissions() {
      return this._fieldPermissions;
   }

   public final Vector getFilters() {
      return this._filters;
   }

   public final Filter getFilter(String filterId) {
      Filter filterMatch = null;

      for (int i = 0; i < this._filters.size(); i++) {
         Filter filter = (Filter)this._filters.elementAt(i);
         if (filter.getId().equalsIgnoreCase(filterId)) {
            filterMatch = filter;
         }
      }

      return filterMatch;
   }

   public final void addFilter(Filter filter) {
      if (this._filters == null) {
         this._filters = new Vector();
      }

      this._filters.addElement(filter);
   }

   public final void removeFilter(String filterId) {
      Filter filterMatch = null;

      for (int i = 0; i < this._filters.size(); i++) {
         Filter filter = (Filter)this._filters.elementAt(i);
         if (filter.getId().equalsIgnoreCase(filterId)) {
            this._filters.removeElementAt(i);
            return;
         }
      }
   }

   public final void setFilters(Vector filters) {
      this._filters = filters;
   }

   public final String getSecretQuestion() {
      return this._secretQuestion;
   }

   public final void setSecretQuestion(String question) {
      this._secretQuestion = question;
   }

   public final Integer getSecretQuestionId() {
      return this._secretQuestionId;
   }

   public final void setSecretQuestionId(Integer questionId) {
      this._secretQuestionId = questionId;
   }

   public final String getSecretAnswer() {
      return this._secretAnswer;
   }

   public final void setSecretAnswer(String answer) {
      this._secretAnswer = answer;
   }

   public final boolean hasSecretQuestion() {
      return (this.getSecretQuestion() != null && this.getSecretQuestion().length() > 0 || this.getSecretQuestionId() != null)
         && this.getSecretAnswer() != null
         && this.getSecretAnswer().length() > 0;
   }

   public final int getMailboxType() {
      int result = 0;
      if (this._hosted != null) {
         if (this._hosted) {
            return 2;
         }

         if (this._protocol != null) {
            if ("owa".equalsIgnoreCase(this._protocol)) {
               return 5;
            } else if (!"pop".equalsIgnoreCase(this._protocol) && !"imap".equalsIgnoreCase(this._protocol)) {
               return this._protocol.toLowerCase().startsWith("native") ? 6 : 3;
            } else {
               return 4;
            }
         } else {
            return 1;
         }
      } else {
         return result;
      }
   }

   @Override
   public final boolean equals(Object obj) {
      boolean result = false;
      if (obj instanceof Mailbox) {
         Mailbox mailbox = (Mailbox)obj;
         result = ObjectUtilities.objEqual(mailbox.getAutoBCC(), this.getAutoBCC())
            && ObjectUtilities.objEqual(mailbox.getAutoForward(), this.getAutoForward())
            && ObjectUtilities.objEqual(mailbox.getAutoForwardAll(), this.getAutoForwardAll())
            && ObjectUtilities.objEqual(mailbox.getDeleteSync(), this.getDeleteSync())
            && ObjectUtilities.objEqual(mailbox.getDescription(), this.getDescription())
            && ObjectUtilities.objEqual(mailbox.getEmail(), this.getEmail())
            && ObjectUtilities.objEqual(mailbox.getFriendlyName(), this.getFriendlyName())
            && ObjectUtilities.objEqual(mailbox.getHosted(), this.getHosted())
            && ObjectUtilities.objEqual(mailbox.getPassword(), this.getPassword())
            && ObjectUtilities.objEqual(mailbox.getPort(), this.getPort())
            && ObjectUtilities.objEqual(mailbox.getProtocol(), this.getProtocol())
            && ObjectUtilities.objEqual(mailbox.getReplyTo(), this.getReplyTo())
            && ObjectUtilities.objEqual(mailbox.getServer(), this.getServer())
            && ObjectUtilities.objEqual(mailbox.getSignature(), this.getSignature())
            && ObjectUtilities.objEqual(mailbox.getSrcMboxID(), this.getSrcMboxID())
            && ObjectUtilities.objEqual(mailbox.getTimeout(), this.getTimeout())
            && ObjectUtilities.objEqual(mailbox.getUserName(), this.getUserName())
            && ObjectUtilities.objEqual(mailbox.getUseSSL(), this.getUseSSL())
            && ObjectUtilities.objEqual(mailbox.getFieldPermissions(), this.getFieldPermissions())
            && ObjectUtilities.objEqual(mailbox.getSecretAnswer(), this.getSecretAnswer())
            && ObjectUtilities.objEqual(mailbox.getSecretQuestion(), this.getSecretQuestion())
            && ObjectUtilities.objEqual(mailbox.getSecretQuestionId(), this.getSecretQuestionId())
            && ObjectUtilities.objEqual(mailbox.getFilters(), this.getFilters())
            && mailbox.getSettings() == this.getSettings();
      }

      return result;
   }

   public final void copy(Mailbox mailbox) {
      this.setAutoForward(mailbox.getAutoForward());
      Boolean autoForwardAll = mailbox.getAutoForwardAll();
      if (autoForwardAll != null) {
         this.setAutoForwardAll(autoForwardAll);
      }

      this.setAutoBCC(mailbox.getAutoBCC());
      Boolean deleteSync = mailbox.getDeleteSync();
      if (deleteSync != null) {
         this.setDeleteSync(deleteSync);
      }

      this.setDescription(mailbox.getDescription());
      this.setEmail(mailbox.getEmail());
      this.setFriendlyName(mailbox.getFriendlyName());
      Boolean hosted = mailbox.getHosted();
      if (hosted != null) {
         this.setHosted(hosted);
      }

      this.setPassword(mailbox.getPassword());
      Integer port = mailbox.getPort();
      if (port != null) {
         this.setPort(port);
      }

      this.setProtocol(mailbox.getProtocol());
      this.setReplyTo(mailbox.getReplyTo());
      this.setServer(mailbox.getServer());
      this.setSignature(mailbox.getSignature());
      this.setSrcMboxID(mailbox.getSrcMboxID());
      Integer timeout = mailbox.getTimeout();
      if (timeout != null) {
         this.setTimeout(timeout);
      }

      this.setUserName(mailbox.getUserName());
      Boolean useSSL = mailbox.getUseSSL();
      if (useSSL != null) {
         this.setUseSSL(useSSL);
      }

      this.setFieldPermissions(mailbox.getFieldPermissions());
      this.setSettings(mailbox.getSettings());
      this.setSecretQuestion(mailbox.getSecretQuestion());
      this.setSecretQuestionId(mailbox.getSecretQuestionId());
      this.setSecretAnswer(mailbox.getSecretAnswer());
      this.setFilters(mailbox.getFilters());
   }
}
