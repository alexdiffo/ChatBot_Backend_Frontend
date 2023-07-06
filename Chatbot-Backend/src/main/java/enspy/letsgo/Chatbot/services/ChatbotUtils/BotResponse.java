package enspy.letsgo.Chatbot.services.ChatbotUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
//import java.nio.file.FileSystemNotFoundException;
import org.springframework.stereotype.Component;

import enspy.letsgo.Chatbot.controllers.MessageController;
import enspy.letsgo.Chatbot.repositories.MessageRepository;
import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;

@Component
public class BotResponse {   




    
    // public String[] detectSentences(String sentence) throws Exception{

        
    //     //Instantiating the SentenceDetectorME class 
    //     SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel(loadModel("nlpmodels/en-sent.bin")));

    //     //Detecting the sentence
    //     String[] sentences = detector.sentDetect(sentence);


    //     //Printing the sentences 
    //     System.out.println("++++++++++++++++++++++++Detecting the sentences++++++++++++++++++++\n\n");
    //     for (String sent : sentences) {
    //         System.out.println("\n\n"+sent+"\n");
    //     }

    //     return sentences;
    // }


    public String[] tokenize( String sentence, TokenizerME tokenizer)  throws Exception {     


    //Instantiating the tokenizing class
    //TokenizerME tokenizer = new TokenizerME(new TokenizerModel(loadModel("nlpmodels/en-token.bin")));

    //Tokenizing the sentences
    String[] tokens = tokenizer.tokenize(sentence);

    //Printing the tokens
    System.out.println("++++++++++++++++++printing the tokens++++++++++++++++++++\n\n");
    for (String tok : tokens) {
        System.out.println("\n\n"+tok+"\n");
    }
    return tokens; 
   
}



// load model

//    public static InputStream loadModel(String modelName){

//     ClassLoader classloader= Thread.currentThread().getContextClassLoader();
//     InputStream inputStream = classloader.getResourceAsStream(modelName);
//     return inputStream;
//    }


//    public static Span[][] recognizeEntities(String message) throws Exception{

//     //instantiating TokenNameFinder.class
//     TokenNameFinderModel modelDate = new TokenNameFinderModel(loadModel("nlpmodels/en-ner-date.bin"));
//     TokenNameFinderModel modelTime = new TokenNameFinderModel(loadModel("nlpmodels/en-ner-time.bin"));
//     TokenNameFinderModel modelLocation = new TokenNameFinderModel(loadModel("nlpmodels/en-ner-location.bin"));
//     TokenNameFinderModel modelMoney = new TokenNameFinderModel(loadModel("nlpmodels/en-ner-money.bin"));

//     //instantiating NameFinderMe.class
//     NameFinderME nameFinderDate = new NameFinderME(modelDate);
//     NameFinderME nameFinderTime = new NameFinderME(modelTime);
//     NameFinderME nameFinderLocation= new NameFinderME(modelLocation);
//     NameFinderME nameFinderMoney = new NameFinderME(modelMoney);

//     //Find occurrences of each entities in the text
//     Span[][] entities = new Span[4][];
    
//     String[] words = message.split(" ");
//     entities[0]=nameFinderLocation.find(words);
//     entities[1]=nameFinderDate.find(words);
//     entities[2]=nameFinderTime.find(words);
//     entities[3]=nameFinderMoney.find(words);

//     return entities;    

//    }

   public String[] postag(String[] tokens, POSTaggerME tagger) throws Exception {

    //Instatiating PosModel.class
    //POSModel model = new POSModel(loadModel("nlpmodels/en-pos-maxent.bin"));
    

    //Instatiating POSTaggerME.class
    //POSTaggerME tagger = new POSTaggerME(model);
    

    //Generating pos tags using tag() Method
    String[] tags = tagger.tag(tokens);

    return tags;

   }

   public String[] lemmatize(String[] tokens, String[] postags, DictionaryLemmatizer lemmatizer ) throws Exception{

    //DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(loadModel("nlpmodels/en-lemmatizer.dict"));

    // LemmatizerModel model = new LemmatizerModel(loadModel("nlpmodels/en-lemmatizer.bin"));
    // LemmatizerME categorizer = new LemmatizerME(model);
    // String[] lemmaTokens = categorizer.lemmatize(tokens,postags);
    String[] lemmas = lemmatizer.lemmatize(tokens, postags);

    return lemmas;
   }

//    public DoccatModel trainCategorizerModel() throws  IOException{

      
//       InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File("categories.txt"));
//       ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
//       ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

//       DoccatFactory factory = new DoccatFactory(new FeatureGenerator[] { new BagOfWordsFeatureGenerator()} );

//       TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
//       params.put(TrainingParameters.CUTOFF_PARAM,0);

//       DoccatModel model = DocumentCategorizerME.train("en", sampleStream, params, factory);
//       return model;
//    }

   public static String categorize(DoccatModel model, String[] tokens) throws IOException
   {
    DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);

    double[] probabilitiesOfOutcomes = myCategorizer.categorize(tokens);
    String category = myCategorizer.getBestCategory(probabilitiesOfOutcomes);
    System.out.println("\n\n\nThe Category seems to be:    " + category+"\n\n\n");
    return category;
   }

//    public void modelsInitialization(){

//     try {

//         this.sentenceModel = new SentenceModel(loadModel("nlpmodels/en-sent.bin"));
//         this.tokenizeModel = new TokenizerModel(loadModel("nlpmodels/fr-token.bin"));
//         System.out.println("models initialized correctly!");

//     } catch (IOException e) {
//         // TODO Auto-generated catch block
//         e.printStackTrace();
//     }    

//    }

   public String findCategory(String Message){  
    
    String category ="";

    try {

    // Sentence detection    
    // String[] sentences = detectSentences( Message);
    // for(String sent: sentences){
    //     tokenize( sent);
    //     // suite du pipeline de traitement        
    // }

    //tokenization
    String[] tokens = tokenize(Message, MessageController.tokenizerMe);

    //Recognizing entities
    // Span [][] myEntities = recognizeEntities(Message);


    // for (Span[] ent : myEntities) {
       
    //     for (int i = 0; i < ent.length; i++) {

    //         System.out.println(ent[i] + " - " +
            
    //         tokens[ent[i].getStart()]);
            
    //         }
    // }

    //Pos tagging
    String[] posTags = postag(tokens, MessageController.pOSTaggerME);
    System.out.println("POSTagging successfully done!");

    //lemmatization
    String[] lemmas = lemmatize(tokens, posTags, MessageController.dictionaryLemmatizer) ;
    System.out.println("Lemmatizing successfully done!");

    //Train custum categorize model
    //DoccatModel model = trainCategorizerModel();
    //System.out.println("\n\ntrainning customized model successfully done!");

    //Categorize chunking
    category = categorize(MessageController.doccatModel, lemmas);
    System.out.println("Categorizing successfully done!");

    
        
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

    return category;
   }        
        
}

