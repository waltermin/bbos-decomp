package javax.microedition.xml.rpc;

import javax.xml.namespace.QName;

public class FaultDetailException extends Exception {
   private Object faultDetail;
   private QName detailName;

   public FaultDetailException(QName detailName, Object faultDetail) {
      this.faultDetail = faultDetail;
      this.detailName = detailName;
   }

   public Object getFaultDetail() {
      return this.faultDetail;
   }

   public QName getFaultDetailName() {
      return this.detailName;
   }
}
