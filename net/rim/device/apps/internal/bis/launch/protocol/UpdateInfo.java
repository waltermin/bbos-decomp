package net.rim.device.apps.internal.bis.launch.protocol;

import net.rim.device.api.util.Arrays;

public final class UpdateInfo {
   private boolean _upToDate;
   private String _version;
   private String[] _downloadUrls = new Object[0];
   private byte[][][] _digests = new byte[0][][];
   private int _downloadSize;
   private boolean _mandatory;

   public final boolean getUpToDateStatus() {
      return this._upToDate;
   }

   public final void setUpToDateStatus(boolean upToDate) {
      this._upToDate = upToDate;
   }

   public final void setVersion(String version) {
      this._version = version;
   }

   public final void addModule(UpdateInfo$ModuleInfo module) {
      Arrays.add(this._downloadUrls, module.getDownloadURL());
      Arrays.add(this._digests, module.getDigest());
      this._downloadSize = this._downloadSize + module.getSize();
   }

   public final boolean isMandatory() {
      return this._mandatory;
   }

   public final void setMandatory(boolean mandatory) {
      this._mandatory = mandatory;
   }

   public final String[] getDownloadUrls() {
      return this._downloadUrls;
   }

   public final byte[][][] getDigests() {
      return this._digests;
   }

   public final int getDownloadSize() {
      return this._downloadSize;
   }
}
