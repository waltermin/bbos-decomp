package net.rim.device.apps.internal.docview.gui;

final class DocViewDisplayScreen$ExecuteObj {
   String _domID;
   int _latestBookmarkRequestByRemoteLink = -1;
   int _latestChunkRequestByRemoteLink = -1;
   byte _type;
   protected static final byte EMBEDDEDOBJECT_TYPE = 0;
   protected static final byte TEXTLINK_TYPE = 1;
   protected static final byte ARBDOMID_TYPE = 2;
   protected static final byte RENDER_TYPE = 3;
   protected static final byte DOCINFO_TYPE = 4;

   DocViewDisplayScreen$ExecuteObj(byte type) {
      this._type = type;
   }
}
