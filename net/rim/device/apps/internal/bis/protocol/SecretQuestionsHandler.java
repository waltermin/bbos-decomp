package net.rim.device.apps.internal.bis.protocol;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;
import net.rim.device.apps.internal.bis.data.SecretQuestion;
import net.rim.device.apps.internal.bis.utils.xml.XMLToObjectHandler;

final class SecretQuestionsHandler extends XMLToObjectHandler implements BISServiceConstants {
   private SAXParser _parser;
   private static final String[] REQUIRED_ELEMENTS = new Object[0];

   public SecretQuestionsHandler() {
      super("secretQuestions", REQUIRED_ELEMENTS, true);
      this.setElementHandler("question", new QuestionHandler());
   }

   public final SecretQuestion[] loadFromXML(InputStream istream) {
      if (this._parser == null) {
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         this._parser = parserFactory.newSAXParser();
         this._parser.setAllowUndefinedNamespaces(true);
      }

      this._parser.parse(istream, this);
      return (SecretQuestion[])this.getResult();
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      Vector questions = (Vector)elementToValueMap.get("question");
      SecretQuestion[] arrQuestions = new SecretQuestion[questions.size()];

      for (int i = 0; i < arrQuestions.length; i++) {
         arrQuestions[i] = (SecretQuestion)questions.elementAt(i);
      }

      return arrQuestions;
   }
}
