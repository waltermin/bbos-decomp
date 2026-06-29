package net.rim.tid.im.conv.europe.repository.mailExtractor;

import net.rim.tid.data.LearningDataManager;
import net.rim.tid.im.conv.europe.repository.WordLearningReader;

class EmailAddrAndURLAddrLearningReader extends WordLearningReader {
   @Override
   public String getFilename(String aLocaleStr) {
      return LearningDataManager.constructKey("emailAndUrl", aLocaleStr, (byte)1);
   }
}
