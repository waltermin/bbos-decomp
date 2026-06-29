package net.rim.device.apps.internal.mms.service;

import java.io.InputStream;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.model.MMSAttachmentImpl;

public final class DRMConverter {
   private MMSAttachment _originalAttachment;
   private byte[] _data;
   private int _currentOffset;
   private int _maximumOffset;
   private String _contentType = "text/plain";
   private String _contentTransferEncoding;
   private String _charset;

   private DRMConverter(MMSAttachment attachment, byte[] data) {
      this._originalAttachment = attachment;
      this._data = data;
      this._maximumOffset = this._data.length;
   }

   public static final MMSAttachment wrap(MMSAttachment attachment) {
      return new DRMAttachment(attachment);
   }

   public static final MMSAttachment unwrap(MMSAttachment drm) {
      byte[] data = MMSUtilities.decrypt(drm.getData());
      return data == null ? null : new DRMConverter(drm, data).unwrap();
   }

   private final MMSAttachment unwrap() {
      this.skipBoundary();
      this.readHeaders();
      return this.readContent();
   }

   private final void skipBoundary() {
      int boundaryState = 1;

      while (true) {
         char b = this.readNextChar();
         if (b == -1) {
            return;
         }

         if (b == '-') {
            if (boundaryState < 4 && boundaryState > 0) {
               boundaryState++;
            }
         } else if (b == '\n') {
            if (boundaryState >= 4) {
               return;
            }

            boundaryState = 1;
         } else if (boundaryState == 3 && b != '\r') {
            boundaryState = 4;
         } else if (boundaryState < 4) {
            boundaryState = 0;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void readHeaders() {
      String line = null;

      while ((line = this.readNextLine()) != null) {
         if (line.length() == 0) {
            return;
         }

         boolean var6 = false /* VF: Semaphore variable */;

         try {
            var6 = true;
            int e = line.indexOf(58);
            if (e != -1) {
               String key = line.substring(0, e).trim();
               String value = line.substring(e + 1).trim();
               if (StringUtilities.compareToIgnoreCase(key, "Content-Type", 1701707776) == 0) {
                  this._contentType = value;
                  var6 = false;
               } else if (StringUtilities.compareToIgnoreCase(key, "Content-Transfer-Encoding", 1701707776) == 0) {
                  this._contentTransferEncoding = value;
                  var6 = false;
               } else if (StringUtilities.compareToIgnoreCase(key, "charset", 1701707776) == 0) {
                  this._charset = value;
                  var6 = false;
               } else {
                  var6 = false;
               }
            } else {
               var6 = false;
            }
         } finally {
            if (var6) {
               return;
            }
         }
      }
   }

   private final MMSAttachment readContent() {
      int startOffset = this._currentOffset;
      int endOffset = startOffset;

      for (int i = this._maximumOffset - 6; i >= startOffset; i--) {
         if (this._data[i] == 10 && this._data[i + 1] == 45 && this._data[i + 2] == 45) {
            if (i >= 1 && this._data[i - 1] == 13) {
               i--;
            }

            endOffset = i;
            break;
         }
      }

      if (endOffset <= startOffset) {
         return null;
      }

      byte[] actualContent = new byte[endOffset - startOffset];
      System.arraycopy(this._data, startOffset, actualContent, 0, endOffset - startOffset);
      if (this._contentTransferEncoding != null && StringUtilities.strEqualIgnoreCase(this._contentTransferEncoding, "base64", 1701707776)) {
         try {
            actualContent = IOUtilities.streamToBytes((InputStream)(new Object((InputStream)(new Object(actualContent)))));
         } finally {
            return new MMSAttachmentImpl(this._originalAttachment.getName(), MMSUtilities.getMIMEType(this._contentType), actualContent, this._charset);
         }
      }

      return new MMSAttachmentImpl(this._originalAttachment.getName(), MMSUtilities.getMIMEType(this._contentType), actualContent, this._charset);
   }

   private final char readNextChar() {
      return this._currentOffset >= this._maximumOffset ? '\uffff' : (char)this._data[this._currentOffset++];
   }

   private final String readNextLine() {
      if (this._currentOffset >= this._maximumOffset) {
         return null;
      }

      int startOffset = this._currentOffset;
      int length = this._maximumOffset - startOffset;

      while (this._currentOffset < this._maximumOffset) {
         byte b = this._data[this._currentOffset++];
         if (b == 13 || b == 10) {
            length = this._currentOffset - startOffset - 1;
            if (this._currentOffset < this._maximumOffset && this._data[this._currentOffset] + b == 23) {
               this._currentOffset++;
            }
            break;
         }
      }

      return (String)(new Object(this._data, startOffset, length));
   }
}
