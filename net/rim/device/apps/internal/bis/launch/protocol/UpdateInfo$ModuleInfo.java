package net.rim.device.apps.internal.bis.launch.protocol;

public final class UpdateInfo$ModuleInfo {
   private String _downloadURL;
   private byte[] _digest;
   private int _size;

   public final String getDownloadURL() {
      return this._downloadURL;
   }

   public final byte[] getDigest() {
      return this._digest;
   }

   public final int getSize() {
      return this._size;
   }

   public final void setDownloadURL(String downloadURL) {
      this._downloadURL = downloadURL;
   }

   public final void setDigest(byte[] digest) {
      this._digest = digest;
   }

   public final void setSize(int size) {
      this._size = size;
   }
}
