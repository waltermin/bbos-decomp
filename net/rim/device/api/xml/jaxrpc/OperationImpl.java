package net.rim.device.api.xml.jaxrpc;

import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.MarshalException;
import java.rmi.ServerException;
import java.util.Hashtable;
import javax.microedition.io.HttpConnection;
import javax.microedition.xml.rpc.ComplexType;
import javax.microedition.xml.rpc.Element;
import javax.microedition.xml.rpc.FaultDetailHandler;
import javax.microedition.xml.rpc.Operation;
import javax.microedition.xml.rpc.Type;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.internal.io.RIMConnector;
import net.rim.vm.TraceBack;

public final class OperationImpl extends Operation {
   private Element _inputDescription;
   private Element _outputDescription;
   private SoapMessageDecoder _decoder;
   private FaultDetailHandler _faultDetailHandler;
   private Hashtable _properties;
   static boolean processingFault = false;
   private static final int PROPERTY_TABLE_INITIAL_SIZE = 7;

   public OperationImpl(QName qname, Element inputDescription, Element outputDescription, FaultDetailHandler faultDetailHandler) {
      this._inputDescription = inputDescription;
      this._outputDescription = outputDescription;
      this._faultDetailHandler = faultDetailHandler;
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Object invoke(Object inputValue) {
      HttpConnection connection = null;
      InputStream inputStream = null;
      OutputStream outputStream = null;
      String wbxmlProperty = this.getProperty("net.rim.device.api.xml.jaxrpc.compression.wbxml");
      boolean compress = wbxmlProperty != null && wbxmlProperty.toLowerCase().equals("true");
      String codebookProperty = this.getProperty("net.rim.device.api.xml.jaxrpc.compression.wbxml.codebook");
      SoapWbxmlCodebook codebook = null;
      if (compress && codebookProperty != null) {
         label128:
         try {
            codebook = (SoapWbxmlCodebook)Class.forName(codebookProperty).newInstance();
         } finally {
            break label128;
         }
      }

      try {
         boolean var19 = false /* VF: Semaphore variable */;

         Object var11;
         try {
            var19 = true;
            int e = TraceBack.getCallingModule(0);
            connection = (HttpConnection)RIMConnector.open(e, this.getProperty("javax.xml.rpc.service.endpoint.address"));
            outputStream = this.setupRequest(connection, compress);
            SoapMessageEncoder.encode(outputStream, this._inputDescription, inputValue, "UTF-8", compress, codebook);
            inputStream = this.setupResponse(connection);
            Object outputValue = null;
            this._decoder = new SoapMessageDecoder(compress, codebook);
            if (processingFault) {
               this._outputDescription = this.defineSOAPFaultElement();
               outputValue = this._decoder.decode(inputStream, this._outputDescription, connection.getEncoding(), this._faultDetailHandler);
               processingFault = false;
            } else if (this._outputDescription != null) {
               outputValue = this._decoder.decode(inputStream, this._outputDescription, connection.getEncoding(), null);
            }

            var11 = outputValue;
            var19 = false;
         } finally {
            if (var19) {
               processingFault = false;
               closeOutputStream(outputStream);
               closeInputStream(inputStream);
               closeConnection(connection);
            }
         }

         processingFault = false;
         closeOutputStream(outputStream);
         closeInputStream(inputStream);
         closeConnection(connection);
         return var11;
      } catch (Throwable var24) {
         if (!(var24 instanceof JAXRPCException)) {
            if (!(var24 instanceof MarshalException) && !(var24 instanceof ServerException)) {
               throw new JAXRPCException(var24.toString());
            } else {
               throw new JAXRPCException(var24);
            }
         } else {
            throw (JAXRPCException)var24;
         }
      }
   }

   private static final void closeConnection(HttpConnection connection) {
      try {
         connection.close();
      } finally {
         return;
      }
   }

   private static final void closeInputStream(InputStream inputStream) {
      try {
         inputStream.close();
      } finally {
         return;
      }
   }

   private static final void closeOutputStream(OutputStream outputStream) {
      try {
         outputStream.close();
      } finally {
         return;
      }
   }

   private final Element defineSOAPFaultElement() {
      ComplexType faultArrayType = new ComplexType();
      faultArrayType.elements = new Element[4];
      faultArrayType.elements[0] = new Element(new QName("", "faultcode"), Type.STRING);
      faultArrayType.elements[1] = new Element(new QName("", "faultstring"), Type.STRING);
      faultArrayType.elements[2] = new Element(new QName("", "faultactor"), Type.STRING, 0, 1, false);
      faultArrayType.elements[3] = new Element(new QName("", "detail"), Type.STRING, 0, 1, false);
      return new Element(new QName("http://schemas.xmlsoap.org/soap/envelope/", "Fault"), faultArrayType);
   }

   private final String getProperty(String key) {
      return (String)(this._properties != null ? this._properties.get(key) : null);
   }

   @Override
   public final void setProperty(String name, String value) {
      if (name == null) {
         throw new Object("The name of the property must not be null.");
      }

      if (value == null) {
         throw new Object("The value of the property must not be null.");
      }

      if (!name.equals("javax.xml.rpc.soap.http.soapaction.uri")
         && !name.equals("javax.xml.rpc.service.endpoint.address")
         && !name.equals("javax.xml.rpc.security.auth.password")
         && !name.equals("javax.xml.rpc.security.auth.username")
         && !name.equals("javax.xml.rpc.session.maintain")
         && !name.equals("net.rim.device.api.xml.jaxrpc.compression.wbxml")
         && !name.equals("net.rim.device.api.xml.jaxrpc.compression.wbxml.codebook")) {
         throw new Object();
      }

      if (this._properties == null) {
         this._properties = (Hashtable)(new Object(7));
      }

      this._properties.put(name, value);
   }

   private final OutputStream setupRequest(HttpConnection connection, boolean compress) {
      connection.setRequestMethod("POST");
      connection.setRequestProperty("User-Agent", "RIM JSR172/1.0");
      connection.setRequestProperty("Content-Language", "en-US");
      String contentType = "text/xml";
      if (compress) {
         contentType = "application/vnd.wap.wbxml";
      }

      connection.setRequestProperty("Content-Type", contentType);
      String soapAction = this.getProperty("javax.xml.rpc.soap.http.soapaction.uri");
      if (soapAction == null) {
         soapAction = "\"\"";
      } else {
         if (soapAction.charAt(0) != '"') {
            soapAction = ((StringBuffer)(new Object("\""))).append(soapAction).toString();
         }

         if (soapAction.charAt(soapAction.length() - 1) != '"') {
            soapAction = ((StringBuffer)(new Object())).append(soapAction).append("\"").toString();
         }
      }

      connection.setRequestProperty("SOAPAction", soapAction);
      String userName = this.getProperty("javax.xml.rpc.security.auth.username");
      String password = this.getProperty("javax.xml.rpc.security.auth.password");
      if (userName != null) {
         byte[] authData = ((StringBuffer)(new Object())).append(userName).append(":").append(password == null ? "" : password).toString().getBytes();
         String encodedAuthData = Base64OutputStream.encodeAsString(authData, 0, authData.length, false, false);
         connection.setRequestProperty("Authorization", ((StringBuffer)(new Object("Basic "))).append(encodedAuthData).toString());
      }

      return connection.openOutputStream();
   }

   private final InputStream setupResponse(HttpConnection connection) {
      InputStream inputStream = connection.openInputStream();
      int response = connection.getResponseCode();
      if (response != 200) {
         processingFault = true;
      }

      return inputStream;
   }
}
