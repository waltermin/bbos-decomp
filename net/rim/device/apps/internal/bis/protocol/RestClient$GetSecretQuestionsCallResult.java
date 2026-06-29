package net.rim.device.apps.internal.bis.protocol;

import net.rim.device.apps.internal.bis.data.SecretQuestion;

public final class RestClient$GetSecretQuestionsCallResult extends RestClient$RESTCallResult {
   private SecretQuestion[] _secretQuestions;

   RestClient$GetSecretQuestionsCallResult(long restStatusCode, SecretQuestion[] secretQuestions) {
      super(restStatusCode);
      this._secretQuestions = secretQuestions;
   }

   public final SecretQuestion[] getSecretQuestions() {
      return this._secretQuestions;
   }
}
