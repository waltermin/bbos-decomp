package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.blackberryemail.email.RemoveWhenSendingModel;

public class SecureEmailOptions implements Persistable, RIMModel, RemoveWhenSendingModel, CollectionListener, GlobalEventListener {
   protected KeyStoreData _signingKeyStoreData;
   protected KeyStoreData _encryptionKeyStoreData;
   protected int _contentCipherBitfield;
   protected boolean _showMessageDetails;
   protected boolean _promptProblemPersonalCerts;
   protected boolean _promptTruncatedBody;
   protected boolean _promptIgnoredAttachment;
   private static final boolean SHOW_MESSAGE_DETAILS_DEFAULT = true;
   private static final boolean PROMPT_PROBLEM_PERSONAL_CERTS_DEFAULT = true;
   private static final boolean PROMPT_TRUNCATED_BODY = true;
   private static final boolean PROMPT_IGNORED_ATTACHMENT = true;

   protected void reset() {
      this._signingKeyStoreData = null;
      this._encryptionKeyStoreData = null;
      this._showMessageDetails = true;
      this._promptProblemPersonalCerts = true;
      this._promptTruncatedBody = true;
      this._promptIgnoredAttachment = true;
   }

   public void copy(SecureEmailOptions s) {
      this._signingKeyStoreData = s._signingKeyStoreData;
      this._encryptionKeyStoreData = s._encryptionKeyStoreData;
      this._contentCipherBitfield = s._contentCipherBitfield;
      this._showMessageDetails = s._showMessageDetails;
      this._promptProblemPersonalCerts = s._promptProblemPersonalCerts;
      this._promptTruncatedBody = s._promptTruncatedBody;
      this._promptIgnoredAttachment = s._promptIgnoredAttachment;
   }

   public synchronized boolean isConfigured(int encodingAction, ServiceRecord serviceRecord) {
      return (encodingAction & 1) == 0 || this._signingKeyStoreData != null;
   }

   public void setSigningKeyStoreData(KeyStoreData signingKeyStoreData) {
      this._signingKeyStoreData = signingKeyStoreData;
   }

   public KeyStoreData getSigningKeyStoreData() {
      return this._signingKeyStoreData;
   }

   public void setEncryptionKeyStoreData(KeyStoreData encryptionKeyStoreData) {
      this._encryptionKeyStoreData = encryptionKeyStoreData;
   }

   public KeyStoreData getEncryptionKeyStoreData() {
      return this._encryptionKeyStoreData;
   }

   public int getContentCipherBitfield() {
      return this._contentCipherBitfield;
   }

   public boolean getShowMessageDetails() {
      return this._showMessageDetails;
   }

   public boolean getPromptProblemPersonalCerts() {
      return this._promptProblemPersonalCerts;
   }

   public boolean getPromptTruncatedMessage() {
      return this.getPromptTruncatedBody() && this.getPromptIgnoredAttachment();
   }

   public void setPromptTruncatedMessage(boolean promptTruncatedMessage) {
      this.setPromptIgnoredAttachment(promptTruncatedMessage);
      this.setPromptTruncatedBody(promptTruncatedMessage);
   }

   public void setPromptProblemPersonalCerts(boolean promptProblemPersonalCerts) {
      this._promptProblemPersonalCerts = promptProblemPersonalCerts;
   }

   public void setContentCipherBitfield(int contentCipherBitfield) {
      this._contentCipherBitfield = contentCipherBitfield;
   }

   public void setShowMessageDetails(boolean showMessageDetails) {
      this._showMessageDetails = showMessageDetails;
   }

   public OTASyncCapableSyncItem getSyncItem() {
      throw null;
   }

   @Override
   public boolean removeAfterSending() {
      return true;
   }

   @Override
   public boolean removeBeforeSending() {
      return false;
   }

   @Override
   public void elementRemoved(Collection _1, Object _2) {
      throw null;
   }

   @Override
   public void elementUpdated(Collection _1, Object _2, Object _3) {
      throw null;
   }

   @Override
   public void elementAdded(Collection _1, Object _2) {
      throw null;
   }

   @Override
   public void reset(Collection _1) {
      throw null;
   }

   @Override
   public void eventOccurred(long _1, int _3, int _4, Object _5, Object _6) {
      throw null;
   }

   private void setPromptIgnoredAttachment(boolean promptIgnoredAttachment) {
      this._promptIgnoredAttachment = promptIgnoredAttachment;
   }

   private boolean getPromptTruncatedBody() {
      return this._promptTruncatedBody;
   }

   private boolean getPromptIgnoredAttachment() {
      return this._promptIgnoredAttachment;
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof SecureEmailOptions)) {
         return false;
      }

      SecureEmailOptions s = (SecureEmailOptions)other;
      return ObjectUtilities.objEqual(this._signingKeyStoreData, s._signingKeyStoreData)
         && ObjectUtilities.objEqual(this._encryptionKeyStoreData, s._encryptionKeyStoreData)
         && this._contentCipherBitfield == s._contentCipherBitfield
         && this._showMessageDetails == s._showMessageDetails
         && this._promptProblemPersonalCerts == s._promptProblemPersonalCerts
         && this._promptTruncatedBody == s._promptTruncatedBody
         && this._promptIgnoredAttachment == s._promptIgnoredAttachment;
   }

   private void setPromptTruncatedBody(boolean promptTruncatedBody) {
      this._promptTruncatedBody = promptTruncatedBody;
   }
}
