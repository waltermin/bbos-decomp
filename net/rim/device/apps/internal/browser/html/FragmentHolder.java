package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.Arrays;

final class FragmentHolder {
   private String _uri;
   private HttpHeaders _headers;
   private byte[] _data;
   private int _responseCode;
   private int[] _trimOffsets;
   private int[] _trimLengths;
   private byte[] _mac;
   private int _fragmentCount;

   public FragmentHolder(int fragmentCount, String uri, HttpHeaders headers, byte[] data, int responseCode) {
      this._fragmentCount = fragmentCount;
      this._responseCode = responseCode;
      this._data = data;
      this._headers = headers;
      this._uri = uri;
      this._trimOffsets = new int[0];
      this._trimLengths = new int[0];
   }

   public final int getFragmentCount() {
      return this._fragmentCount;
   }

   public final String getUri() {
      return this._uri;
   }

   public final int getDataLength() {
      return this._data.length;
   }

   public final byte[] getData() {
      return this._data;
   }

   public final HttpHeaders getHeaders() {
      return this._headers;
   }

   public final int getResponseCode() {
      return this._responseCode;
   }

   public final void addTrimSegment(int offset, int length) {
      Arrays.add(this._trimOffsets, offset);
      Arrays.add(this._trimLengths, length);
   }

   public final void appendData(byte[] data) {
      Arrays.append(this._data, data);
   }

   public final byte[] getStoredMAC() {
      return this._mac;
   }

   public final void setStoredMAC(byte[] mac) {
      this._mac = mac;
   }

   public final int getTrimSegments() {
      return this._trimOffsets.length;
   }

   public final int[] getTrimOffsets() {
      return this._trimOffsets;
   }

   public final int[] getTrimLengths() {
      return this._trimLengths;
   }
}
