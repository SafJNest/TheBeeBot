package com.safjnest.Utilities;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;


/**
 * This class contains all the methods to use the OpenAI API and to get the response from it.
 * @see <a href="https://beta.openai.com/docs/api-reference/completions/create">OpenAI API</a>
 * @author <a href="https://github.com/NeutronSun">NeutronSun</a>
 */
public class OpenAIHandler {
    /**
     * The main object for make requests and get responses from the OpenAI API.
     */
    private static OpenAiService service;
    /**
     * The maximum number of tokens to generate.
     */
    private static String maxTokens;
    /**
     * The model to use for completion.
     */
    private static String model;

    /**
     * Constructor of the class.
     * @param key the key to use the API
     * @param maxTokens the maximum number of tokens to generate
     * @param model the model to use for completion
     */
    public OpenAIHandler(String key, String maxTokens, String model){
        OpenAIHandler.service = new OpenAiService(key);
        OpenAIHandler.maxTokens = maxTokens;
        OpenAIHandler.model = model;
        System.out.println("[OpenAI] INFO Connection Successful!");
    }

     /**
    * Useless method but {@link <a href="https://github.com/NeutronSun">NeutronSun</a>} is one
    * of the biggest bellsprout ever made
    */
	public void doSomethingSoSunxIsNotHurtBySeeingTheFuckingThingSayItsNotUsed() {
        return;
	}

    public static CompletionRequest getCompletionRequest(String args){
        return CompletionRequest.builder()
        .prompt(args)
        .model(model)
        .maxTokens(Integer.valueOf(maxTokens))
        .topP(1.0)
        .frequencyPenalty(0.0)
        .presencePenalty(0.0)
        .bestOf(1)
        .echo(true)
        .build();
    }

    public static OpenAiService getAiService(){
        return service;
    }




}
