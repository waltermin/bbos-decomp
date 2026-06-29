package net.rim.device.apps.internal.docview.gui;

final class DocViewDisplayScreen$ExecuteObj {
   String _domID;
   int _latestBookmarkRequestByRemoteLink = -1;
   int _latestChunkRequestByRemoteLink = -1;
   byte _type;
   protected static final byte EMBEDDEDOBJECT_TYPE;
   protected static final byte TEXTLINK_TYPE;
   protected static final byte ARBDOMID_TYPE;
   protected static final byte RENDER_TYPE;
   protected static final byte DOCINFO_TYPE;

   DocViewDisplayScreen$ExecuteObj(byte type) {
      this._type = type;
   }
}
