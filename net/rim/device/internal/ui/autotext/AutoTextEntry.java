package net.rim.device.internal.ui.autotext;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;

final class AutoTextEntry implements Persistable, SyncObject {
   private String _find;
   private Object _replaceEncoding;
   private int _case;
   private int _localeCode;
   private int _uid;

   final int getCase() {
      return this._case;
   }

   final int getLocaleCode() {
      return this._localeCode;
   }

   final String getFindString() {
      return this._find;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   final String getReplaceString() {
      return PersistentContent.decodeString(this._replaceEncoding);
   }

   final boolean checkCrypt(boolean encrypt) {
      return PersistentContent.checkEncoding(this._replaceEncoding, false, encrypt);
   }

   final AutoTextEntry reCrypt(boolean encrypt) {
      AutoTextEntry newModel = (AutoTextEntry)ObjectGroup.expandGroup(this);
      newModel._replaceEncoding = PersistentContent.reEncode(newModel._replaceEncoding, false, encrypt);
      ObjectGroup.createGroupIgnoreTooBig(newModel);
      return newModel;
   }

   AutoTextEntry(String find, String replace, int caseUsed, int localeCode) {
      this(find, replace, caseUsed, localeCode, UIDGenerator.getUID());
   }

   AutoTextEntry(String find, String replace, int caseUsed, int localeCode, int uid) {
      this._find = find != null ? find.toLowerCase() : "";
      this._replaceEncoding = PersistentContent.encode(replace != null ? replace : "", false, AutoTextCollection.encryptAutoTextEntries());
      this._case = caseUsed;
      this._localeCode = localeCode;
      this._uid = uid;
      ObjectGroup.createGroupIgnoreTooBig(this);
   }
}
