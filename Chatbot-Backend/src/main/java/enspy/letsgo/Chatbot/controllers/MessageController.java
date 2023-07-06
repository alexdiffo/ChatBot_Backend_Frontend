package enspy.letsgo.Chatbot.controllers;
//package com.projetreseau.chatbot.controllers.utils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.databind.ObjectMapper;


import enspy.letsgo.Chatbot.models.Admin;
import enspy.letsgo.Chatbot.models.Message;
import enspy.letsgo.Chatbot.services.MessageService;
import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;

@CrossOrigin("http://localhost:3000")
@RequestMapping
@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Value("${admusr}")
    private String adminUsername;

    @Value("${admpass}")
    private String adminPassword;

    @Value("${BookingPage}")
    private String BookingPage;

    @Value("${PaymentInfoPage}")
    private String PaymentInfoPage;

    @Value("${AboutPage}")
    private String AboutPage;

    @Value("${PricingPage}")
    private String PricingPage;

    @Value("${RegistrationPage}")
    private String RegistrationPage;

    @Value("${termsandconditions}")
    private String termsandconditions;

    @Value("${FAQ}")
    private String FAQ;

    @Value("${PromotionsPage}")
    private String PromotionsPage;

    @Value("${hotels}")
    private String hotels;

    @Value("${marketplaces}")
    private String marketplaces;




    // static String activateReservationForm = "no";
    // static String reservationConfirm ="no";

    // static String destinationReservation;
    // static String departureReservationForm;


    // public static  void setActivateReservationform(String value){
    //     activateReservationForm = value;
    // }

    // public static String getActivateReservationForm(){
    //     return activateReservationForm;
    // }

    // public static  void setReservationConfirm (String value){
    //     reservationConfirm = value;
    // }

    // public static  void setDestinationReservation (String value){
    //     destinationReservation = value;
    // }


    //load model

   public InputStream loadModel(String modelName){

    ClassLoader classloader= Thread.currentThread().getContextClassLoader();
    InputStream inputStream = classloader.getResourceAsStream(modelName);
    return inputStream;
   }


    public static TokenizerME tokenizerMe;
    public static POSTaggerME pOSTaggerME;
    public static DictionaryLemmatizer dictionaryLemmatizer;
    public static DoccatModel doccatModel;
    public static DocumentCategorizerME documentCategorizerME;




    


    @PostConstruct
    public void trainmodels(){

        

        try {

            

            //Loading trained models
            System.out.println("\n\nLoading Pre-trained models\n\n");
            tokenizerMe = new TokenizerME(new TokenizerModel(loadModel("nlpmodels/en-token.bin")));
            POSModel model = new POSModel(loadModel("nlpmodels/en-pos-maxent.bin"));
            pOSTaggerME = new POSTaggerME(model);
            dictionaryLemmatizer = new DictionaryLemmatizer(loadModel("nlpmodels/en-lemmatizer.dict"));

            //trainning categorizer model
            System.out.println("\n\nTrainning categorizer model\n\n");
            InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File("categories.txt"));
            ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
            ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
            DoccatFactory factory = new DoccatFactory(new FeatureGenerator[] { new BagOfWordsFeatureGenerator()} );
            TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
            params.put(TrainingParameters.CUTOFF_PARAM,0);
            doccatModel = DocumentCategorizerME.train("en", sampleStream, params, factory);



        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("\n\n All pretrained models loaded and  categorizer's model trained successfully\n\n"); 

    }

    @GetMapping("/api/messages-all")
    public List<Message> getAllMessage(){

           return messageService.getAllMessage();
       
    }


    @PostMapping("/api/login")
    public ResponseEntity<String>  login(@RequestBody Admin admin){

        if (admin.getAdminUsername().equals(adminUsername) && admin.getAdminPassword().equals(adminPassword)){
           return ResponseEntity.status(200).body("OK");
        }
        return ResponseEntity.status(400).body("Bad login");
        
    }


    @PostMapping("/api/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public Message HandleMessage (@RequestBody Message message){ 

        
        // if (reservationConfirm==message.getMessageBody()){
        //     setActivateReservationform("yes");

        //     message.setMessageBody("great! what is your destination?");
        //     return message;
            
        // }else{

            
        
        

            String category = messageService.categorize(message);
                     
            //Message replyMessage = messageService.saveMessage(message, category);
            //Message replyMessage = new Message();
            message.setCategory(category);
            
            switch(message.getCategory()){

                case "greetings":

                    message.setResponseBody("Hello! How can I help you?");
                    messageService.saveMessage(message, message.getResponseBody());
                    return message;
                
                case "booking-inquiry" :
                    
                message.setResponseBody("We have several destinations available for you at any selected comfort. To book a car for your trip, please follow this link: "+BookingPage);
                messageService.saveMessage(message, message.getResponseBody());
                return message;
                    
                case "payment-inquiry":
                    //lien requis la page de reservation
                    message.setResponseBody("We accept mobile or credit card payment in a trustworthy and highly secured gateway. for more informations about how to process payment, please follow this link: "+PaymentInfoPage);
                    messageService.saveMessage(message, message.getResponseBody());
                    return message;
                case "pricing-inquiry":
                    //lien requis la page de reservation
                    message.setResponseBody("We have affordable prices for any selected comfort. To know more about our pricing policy, please follow this link "+PricingPage);
                    messageService.saveMessage(message, message.getResponseBody());                                 
                    return message;

                
                case "discount-inquiry":
                    //lien requis la page de reservation
                    message.setResponseBody("check the promotions available to  benefit from any discount "+PromotionsPage);
                    messageService.saveMessage(message, message.getResponseBody());                                 
                    return message;

                case "contact-inquiry":
                    //lien requis la page de contact
                    message.setResponseBody("for more informations about how to contact us through our phone number, email or whatsapp, please follow this link: "+AboutPage);
                    messageService.saveMessage(message, message.getResponseBody());
                    return message;
                case "location-inquiry":
                    //lien requis la page de contact
                    message.setResponseBody("for more informations about our location or our geolocalisation, please follow this link: "+AboutPage);
                    messageService.saveMessage(message, message.getResponseBody());
                    return message;
                case "disappointment-inquiry":
                    message.setResponseBody("We are sorry for inconvenience, please always rate your experience with our services so that we could improve for your next trip ");
                    messageService.saveMessage(message, message.getResponseBody());
                    return message;
                case "help-inquiry":
                    message.setResponseBody("We are here to help you! let us know what you need...");
                    messageService.saveMessage(message, message.getResponseBody());
                    return message;
                case "lost-object-inquiry":
                    message.setResponseBody("If you have lost an object, you can report the loss of an object by sending an e-mail via the contact form specifying the following information: date of trip, object, description of the object (color, size, brand , etc.) ).For any other information, please write us");
                    messageService.saveMessage(message, message.getResponseBody());
                    return message;
                // case "recommendation-inquiry":
                //     message.setResponseBody("For any recommendation please follow the recommendation page or the comment section  ");
                //     messageService.saveMessage(message, message.getResponseBody());
                //     return message;
                case "car-standing-inquiry":
                    //lien requis la page de reservation
                    message.setResponseBody("We have comfortable cars for all standing including many great features you may want like wifi, music, air-conditioned. Please follow the booking page for more informations"+BookingPage);
                    messageService.saveMessage(message, message.getResponseBody());
                    return message;
                case "handicap-care-inquiry":
                    message.setResponseBody("Your disability is not a problem for us, you can book a trip despite your disability ");
                    messageService.saveMessage(message, message.getResponseBody());
                    return message;
                case "cancellation-inquiry":
                    //lien requis la page de confidentialité
                    message.setResponseBody("For any procedure of cancellation , please refers to our confidentiality terms page through this link :"+ termsandconditions);
                    return message ;
                case "advantages-inquiry":
                    message.setResponseBody("Using our application comes with several benefits.Firstly, it simplifies the booking process and allows users to reserve transportation services quickly and conveniently. This can save time and reduce the stress of planning travel arrangements. Secondly, it can help to reduce travel costs by providing access to exclusive deals and discounts. Thirdly, it offers 24/7 convenience, allowing users to book and manage their reservations at any time. Additionally, for small businesses in the travel industry, using a travel reservation app can increase profitability by streamlining operations and improving the customer experience. Overall, a travel reservation app can streamline the travel booking process, save money, and improve the customer experience.");
                    messageService.saveMessage(message, message.getResponseBody());
                    return message;
                case "availability-inquiry":
                    //lien requis la page de reservation
                    message.setResponseBody("For more information about the availability of our services, please follow this link: "+AboutPage);
                    messageService.saveMessage(message, message.getResponseBody());
                    return message;
                case "account-Troubleshooting-inquiry":
                    //lien requis la page d'authentification
                    message.setResponseBody("To create and account or troubleshoot any issue about your credentials please refers to the following page :"+RegistrationPage);
                    messageService.saveMessage(message, message.getResponseBody());
                    return message;
                // case "category-Travel":
                //     //lien requis la page de reservation
                //     message.setResponseBody("For any information about your trip , please follow the booking page");
                //     messageService.saveMessage(message, message.getResponseBody());
                //     return message;
                // case "application-issue-inquiry":
                //     message.setResponseBody("Our application is available for download at google playstore. for any encountered issue with our application, please contact us for assistance or refers to FAQ page to troubleshoot your issue through the following link: "+ FAQ);
                //     messageService.saveMessage(message, message.getResponseBody());
                //     return message;
                // case "category-Suggestion":
                //     //lien requis la page d'evaluation
                //     message.setResponseBody("For any suggestion please write them in the evaluation section thank you ");
                //     return message;
                

                case "hotel-inquiry":
                message.setResponseBody("We can suggest you some good hotel at your destination place please follow this link for suggestion"+ hotels);
                messageService.saveMessage(message, message.getResponseBody());
                return message;

                case "market-inquiry":
                message.setResponseBody("We can suggest you some nice market place or supermarket at your destination place please follow this link for suggestion"+ marketplaces);
                messageService.saveMessage(message, message.getResponseBody());
                return message;

                case "confidentiality-inquiry":
                message.setResponseBody(" Dont worry dear, We secure our clients private informations and we are attached to our confidentiality policy. you can have a look at out confidentiality terms through the following link : "+ termsandconditions);
                messageService.saveMessage(message, message.getResponseBody());
                return message;

                case "insurance-inquiry":
                message.setResponseBody("We are attached to our insurance policy available through the following link "+ termsandconditions);
                messageService.saveMessage(message, message.getResponseBody());
                return message;

                case "approval":
                    message.setResponseBody("I am happy to help you. feel free to ask more questions...");
                    messageService.saveMessage(message, message.getResponseBody());
                    return message;

                case "conversation-complete":
                    message.setResponseBody("goodbye and have a nice day!");
                    messageService.saveMessage(message, message.getResponseBody());
                    return message;

//                case "Catégorie-Réservation" :
//
//                    message.setResponseBody("super! Quelle est votre destination?");
//                    setActivateReservationform("yes");
//                    return message;
//                case "Catégorie-Paiement":
//                    message.setResponseBody("Cependant, il est important de noter que les seuls échanges financiers entre le ou les passagers et le conducteur se limitent au partage des coûts du transport : il s’agit principalement des frais de carburant, des éventuels péages ou frais d’assurance, et si les passagers et le conducteur ont été mis en relation par une plateforme, la commission de cette dernière .Pour toute information sur la politique de  paiement, veuillez nous contacter merci ");
//                    return message;
//                case "Catégorie-Prix":
//                    message.setResponseBody("Pour toute information sur les tarifs, veuillez suivre le lien de réservation merci");
//                    return message;
//                case "Catégorie-Contacts":
//                    message.setResponseBody(
//                            "Pour plus d'informations sur cette application et son propriétaire, veuillez suivre le lien  'nous contacter' merci");
//                    return message;
//                case "Catégorie-Localisation":
//                    message.setResponseBody("Pour plus d'informations sur notre emplacement, veuillez suivre le lien  'nous contacter' merci");
//                    return message;
//                case "Catégorie-Déception":
//                    message.setResponseBody("Nous sommes désolés pour votre mauvaise expérience, veuillez écrire quelques commentaires dans notre page d'avis pour améliorer votre prochain voyage");
//                    return message;
//                case "Catégorie-Demande-aide":
//                    message.setResponseBody("S'il vous plaît contactez-nous, nous vous aiderons");
//                    return message;
//                case "Enquête-objet-perdu":
//                    message.setResponseBody("Si vous avez perdu un objet , vous pouvez signaler la perte d’objet  en envoyant un e-mail via le formulaire de contact en précisant les informations suivantes : date de trajet, objet, description de l’objet (couleur, taille, marque…).Pour toute information autre information , merci de nous contacter ");
//                    return message;
//                case "recommandation":
//                    message.setResponseBody("Pour toute recommandation, veuillez suivre le lien de recommandation ou la section des commentaires ");
//                    return message;
//                case "Catégorie-Préférence":
//                    message.setResponseBody("If you want to customize your trip please follow the booking link");
//                    return message;
//                case "enquête-soins-handicap":
//                    message.setResponseBody("Votre handicap n'est pas un problème pour nous, vous pouvez réserver un voyage malgré votre handicap\n" +
//                            " Si vous avez besoin de plus d'informations s'il vous plaît contactez-nous merci");
//                    return message;
//                case "Catégorie-Demande-annulation":
//                    message.setResponseBody("Pour toute procédure d'annulation, veuillez suivre le lien de réservation\n" +
//                            " Si vous n'êtes pas satisfait s'il vous plaît contactez-nous merci");
//                    return message;
//                case "Catégorie-Avantages":
//                    message.setResponseBody("L'utilisation de notre application présente plusieurs avantages. Premièrement, elle simplifie le processus de réservation et permet aux utilisateurs de réserver des services de transport rapidement et facilement. Cela peut faire gagner du temps et réduire le stress lié à la planification des préparatifs de voyage. Deuxièmement, cela peut aider à réduire les frais de voyage en donnant accès à des offres et des réductions exclusives. Troisièmement, il offre une commodité 24 heures sur 24, 7 jours sur 7, permettant aux utilisateurs de réserver et de gérer leurs réservations à tout moment. De plus, pour les petites entreprises de l'industrie du voyage, l'utilisation d'une application de réservation de voyage peut augmenter la rentabilité en rationalisant les opérations et en améliorant l'expérience client. Dans l'ensemble, une application de réservation de voyage peut rationaliser le processus de réservation de voyage, économiser de l'argent et améliorer l'expérience client.");
//                    return message;
//                case "Catégorie-Demande-de-disponibilité":
//                    message.setResponseBody("Pour plus d'informations sur la disponibilité de nos services, veuillez suivre le lien de réservation merci");
//                    return message;
//                case "compte-dépannage-enquête":
//                    message.setResponseBody("Pour tout problème que vous pourriez avoir avec votre compte, veuillez suivre le lien d'authentification merci");
//                    return message;
//                case "Catégorie-Voyage":
//                    message.setResponseBody("Pour toute information concernant votre réservation, veuillez suivre le lien de réservation");
//                    return message;
//                case "Catégorie-Site-Application":
//                    message.setResponseBody("Pour toute difficulté que vous pourriez avoir avec notre application, veuillez nous contacter");
//                    return message;
//                case "Catégorie-Suggestion":
//                    message.setResponseBody(" Pour toute suggestion s'il vous plaît écrivez-les dans la section d'évaluation merci");
//                    return message;
//                case "catégorie-Salutations ":
//
//                    message.setResponseBody("Salut! Comment puis-je t'aider?");
//                    return message;
//
//                case "destination-sauvegarder":
//                    if (destinationReservation==null){
//                        message.setResponseBody("Non, vous ne m'avez pas encore indiqué votre destination. Où voulez vous aller?");
//                        setActivateReservationform("yes");
//                        return message;
//                    }else{
//                        message.setResponseBody("Oui, je me souviens de votre destination. Vous voulez aller à "+destinationReservation);
//                        return message;
//                    }
//
//                case "Catégorie-Compte-Authentification-Connexion":
//                    message.setResponseBody("Pour tout problème que vous pourriez avoir avec votre compte, veuillez suivre le lien d'authentification ou contactez nous  merci");
//                    return message;
//
//                case "Catégorie-Conversation-achevée":
//                    message.setResponseBody("Ce fût un plaisir de vous aider, bon voyage 😊!");
//                    return message;
                default :
                message.setResponseBody("I do not understand. Please try be more concise ");
                messageService.saveMessage(message, message.getResponseBody());
                return message;
            } 
            
        }
        

    }
        
        
       

    

    




