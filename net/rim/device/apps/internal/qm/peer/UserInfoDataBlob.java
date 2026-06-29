package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.util.DataBuffer;

final class UserInfoDataBlob extends PeerDataBlob {
   private boolean _request;
   private String _conversationId;
   private String _originalInfo;

   public UserInfoDataBlob() {
      this(false, null, null);
   }

   public UserInfoDataBlob(boolean request, String conversationId, String originalInfo) {
      this._request = request;
      this._conversationId = conversationId;
      this._originalInfo = originalInfo == null ? "" : originalInfo;
   }

   public final String getConversationId() {
      return this._conversationId;
   }

   public final boolean isRequest() {
      return this._request;
   }

   @Override
   public final int getType() {
      return 7;
   }

   @Override
   public final void pickle(DataBuffer db) {
      DataBuffer db2 = (DataBuffer)(new Object(db.isBigEndian()));
      db2.writeUTF(this._conversationId);
      db2.writeUTF(this._originalInfo);
      db2.writeCompressedInt(this._request ? 1 : 0);
      this.appendDataBuffer(db, db2);
   }

   @Override
   public final void unPickle(DataBuffer db, int length) {
      this._request = db.readCompressedInt() == 1;
      this._conversationId = db.readUTF();
      this._originalInfo = db.readUTF();
   }
}
