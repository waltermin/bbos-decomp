package net.rim.device.apps.internal.docview.gui;

public final class DocViewTrackChange {
   public final String _trackChangeAuthor;
   public final String _trackChangeDateTime;
   public int _trackChangeStartOffset = -1;
   public int _trackChangeEndOffset = -1;
   public byte _trackChangeType = 0;
   public static final byte DOCVIEWTRACKCHANGE_UNKNOWN = 0;
   public static final byte DOCVIEWTRACKCHANGE_DELETED = 1;
   public static final byte DOCVIEWTRACKCHANGE_INSERTED = 2;

   DocViewTrackChange(String trackChangeAuthor, String trackChangeDateTime) {
      this._trackChangeAuthor = trackChangeAuthor;
      this._trackChangeDateTime = trackChangeDateTime;
   }

   final DocViewTrackChange cloneObject() {
      DocViewTrackChange cloneObj = new DocViewTrackChange(this._trackChangeAuthor, this._trackChangeDateTime);
      cloneObj._trackChangeStartOffset = this._trackChangeStartOffset;
      cloneObj._trackChangeEndOffset = this._trackChangeEndOffset;
      cloneObj._trackChangeType = this._trackChangeType;
      return cloneObj;
   }

   public final boolean valid() {
      return this._trackChangeStartOffset != -1 && this._trackChangeEndOffset != -1 && this._trackChangeType != 0;
   }

   public final boolean identical(DocViewTrackChange compareTrackChange) {
      if (compareTrackChange == null) {
         return false;
      }

      if (this._trackChangeType != compareTrackChange._trackChangeType) {
         return false;
      }

      if (this._trackChangeAuthor == null) {
         if (compareTrackChange._trackChangeAuthor != null) {
            return false;
         }
      } else {
         if (compareTrackChange._trackChangeAuthor == null) {
            return false;
         }

         if (this._trackChangeAuthor.compareTo(compareTrackChange._trackChangeAuthor) != 0) {
            return false;
         }
      }

      if (this._trackChangeDateTime == null) {
         if (compareTrackChange._trackChangeDateTime != null) {
            return false;
         }
      } else {
         if (compareTrackChange._trackChangeDateTime == null) {
            return false;
         }

         if (this._trackChangeDateTime.compareTo(compareTrackChange._trackChangeDateTime) != 0) {
            return false;
         }
      }

      return true;
   }
}
