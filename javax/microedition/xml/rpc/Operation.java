package javax.microedition.xml.rpc;

import javax.xml.namespace.QName;
import net.rim.device.api.xml.jaxrpc.OperationImpl;

public class Operation {
   public static final String SOAPACTION_URI_PROPERTY;

   protected Operation() {
   }

   public static Operation newInstance(QName qname, Element input, Element output) {
      return new OperationImpl(qname, input, output, null);
   }

   public static Operation newInstance(QName qname, Element input, Element output, FaultDetailHandler faultDetailHandler) {
      return new OperationImpl(qname, input, output, faultDetailHandler);
   }

   public Object invoke(Object input) {
      return null;
   }

   public void setProperty(String name, String value) {
   }
}
