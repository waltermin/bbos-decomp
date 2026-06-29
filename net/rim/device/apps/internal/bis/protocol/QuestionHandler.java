package net.rim.device.apps.internal.bis.protocol;

import java.util.Hashtable;
import net.rim.device.apps.internal.bis.data.SecretQuestion;
import net.rim.device.apps.internal.bis.utils.ArgUtils;
import net.rim.device.apps.internal.bis.utils.xml.XMLToObjectHandler;

final class QuestionHandler extends XMLToObjectHandler implements BISServiceConstants {
   private static final String[] REQUIRED_ELEMENTS = new String[]{"id", "value"};

   public QuestionHandler() {
      super("question", REQUIRED_ELEMENTS, true);
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      String value = ArgUtils.getStringValue(elementToValueMap, "value");
      int id = Integer.parseInt(ArgUtils.getStringValue(elementToValueMap, "id"));
      return new SecretQuestion(id, value);
   }
}
