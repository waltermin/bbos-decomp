package net.rim.device.api.crypto.tls;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;

public class RecordProtocol implements RecordProtocolConstants {
   protected byte _entity = 1;
   protected byte[] _masterSecret;
   protected byte[] _clientRandom;
   protected byte[] _serverRandom;
   protected int _writeSequenceNumber;
   protected int _readSequenceNumber;

   public void connect() {
      throw null;
   }

   public int read(DataBuffer _1) {
      throw null;
   }

   public void write(int _1, DataBuffer _2) {
      throw null;
   }

   public void write(int type, byte[] data) {
      this.write(type, data, 0, data == null ? 0 : data.length);
   }

   public void write(int type, byte b) {
      this.write(type, new byte[]{b}, 0, 1);
   }

   public void write(int _1, byte[] _2, int _3, int _4) {
      throw null;
   }

   public void flush() {
      throw null;
   }

   public void close() {
      throw null;
   }

   public void closeOutput() {
      throw null;
   }

   public void closeInput() {
      throw null;
   }

   public byte getChangeCipherSpecConstant() {
      throw null;
   }

   public byte getApplicationProtocolConstant() {
      throw null;
   }

   public void changeCipherSpec() {
      throw null;
   }

   public OutputStream getOutputStream() {
      throw null;
   }

   public InputStream getInputStream() {
      throw null;
   }

   public byte[] getMasterSecret() {
      return Arrays.copy(this._masterSecret);
   }

   public byte[] getClientRandom() {
      return Arrays.copy(this._clientRandom);
   }

   public byte[] getServerRandom() {
      return Arrays.copy(this._serverRandom);
   }

   public AlertProtocolMethods getAlertProtocol() {
      throw null;
   }

   public ChangeCipherSpecProtocol getChangeCipherSpecProtocol() {
      throw null;
   }

   public void setMasterSecret(byte[] masterSecret) {
      this._masterSecret = Arrays.copy(masterSecret);
   }

   public void setClientRandom(byte[] clientRandom) {
      this._clientRandom = Arrays.copy(clientRandom);
   }

   public void setServerRandom(byte[] serverRandom) {
      this._serverRandom = Arrays.copy(serverRandom);
   }
}
