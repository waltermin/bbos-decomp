package net.rim.device.cldc.io.ssl;

import javax.microedition.io.SecurityInfo;
import javax.microedition.pki.Certificate;

public class ProxySecurityInfo implements SecurityInfo {
   private String _cipherSuite;
   private String _protocolName;
   private String _protocolVersion;
   private Certificate _certificate;

   public ProxySecurityInfo(String cipherSuite, String protocolName, String protocolVersion, Certificate certificate) {
      this._cipherSuite = cipherSuite;
      this._protocolName = protocolName;
      this._protocolVersion = protocolVersion;
      this._certificate = certificate;
   }

   @Override
   public String getCipherSuite() {
      return this._cipherSuite;
   }

   @Override
   public String getProtocolName() {
      return this._protocolName;
   }

   @Override
   public String getProtocolVersion() {
      return this._protocolVersion;
   }

   @Override
   public Certificate getServerCertificate() {
      return this._certificate;
   }
}
