package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import java.util.Vector;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import org.xml.sax.Attributes;

class GranularPolicyExpressionGroup extends GranularPolicyElement implements GranularPolicyEvaluateProvider {
   private int _groupOperator;
   public static final int UNKNOWN = -1;
   public static final int GROUP_OPERATOR_ALL = 0;
   public static final int GROUP_OPERATOR_ANY = 1;
   public static final int GROUP_OPERATOR_NOT = 2;
   public static final int GROUP_OPERATOR_TRUE = 3;

   public GranularPolicyExpressionGroup(String xmlTag, Attributes attributes, GranularPolicyElement parentElement) {
      super(xmlTag, parentElement);
      int numAttributes = attributes.getLength();

      for (int i = 0; i < numAttributes; i++) {
         if (StringUtilities.strEqualIgnoreCase(attributes.getLocalName(i), "operator")) {
            String operator = attributes.getValue(i);
            if (StringUtilities.strEqualIgnoreCase(operator, "all")) {
               this._groupOperator = 0;
            } else if (StringUtilities.strEqualIgnoreCase(operator, "any")) {
               this._groupOperator = 1;
            } else if (StringUtilities.strEqualIgnoreCase(operator, "not")) {
               this._groupOperator = 2;
            } else if (StringUtilities.strEqualIgnoreCase(operator, "true")) {
               this._groupOperator = 3;
            } else {
               this._groupOperator = -1;
            }
         }
      }
   }

   @Override
   public GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      if (StringUtilities.strEqualIgnoreCase(localName, "group")) {
         return new GranularPolicyExpressionGroup(localName, attributes, this);
      } else {
         return StringUtilities.strEqualIgnoreCase(localName, "exp")
            ? new GranularPolicyExpression(localName, this)
            : super.handleStartElement(localName, attributes);
      }
   }

   @Override
   public boolean evaluate(String recipientAddress, ServiceRecord serviceRecord, EmailMessageModel emailMessageModel) {
      if (this._groupOperator == 3) {
         return true;
      }

      Vector childElements = this.getChildElements();
      int numChildElements = childElements.size();

      for (int i = 0; i < numChildElements; i++) {
         Object currentChildElement = childElements.elementAt(i);
         if (currentChildElement instanceof GranularPolicyEvaluateProvider) {
            GranularPolicyEvaluateProvider granularPolicyEvaluateProvider = (GranularPolicyEvaluateProvider)currentChildElement;
            boolean currentResult = granularPolicyEvaluateProvider.evaluate(recipientAddress, serviceRecord, emailMessageModel);
            switch (this._groupOperator) {
               case -1:
                  break;
               case 0:
               default:
                  if (!currentResult) {
                     return false;
                  }
                  break;
               case 1:
                  if (currentResult) {
                     return true;
                  }
                  break;
               case 2:
                  if (currentResult) {
                     return false;
                  }
            }
         }
      }

      switch (this._groupOperator) {
         case 0:
         case 2:
            return true;
         default:
            return false;
      }
   }

   @Override
   public String getDebugPrintInfo() {
      StringBuffer info = new StringBuffer();
      info.append("Expression Group");
      switch (this._groupOperator) {
         case -1:
            info.append(" (?)");
            break;
         case 0:
         default:
            info.append(" (All)");
            break;
         case 1:
            info.append(" (Any)");
            break;
         case 2:
            info.append(" (Not)");
            break;
         case 3:
            info.append(" (True)");
      }

      return info.toString();
   }
}
