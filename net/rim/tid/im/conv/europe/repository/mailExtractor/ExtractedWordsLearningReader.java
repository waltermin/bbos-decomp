package net.rim.tid.im.conv.europe.repository.mailExtractor;

import net.rim.tid.data.LearningDataManager;
import net.rim.tid.im.conv.europe.repository.WordLearningReader;

class ExtractedWordsLearningReader extends WordLearningReader {
   @Override
   public String getFilename(String aLocaleStr) {
      return LearningDataManager.constructKey("mailExtractor", aLocaleStr, (byte)1);
   }
}
