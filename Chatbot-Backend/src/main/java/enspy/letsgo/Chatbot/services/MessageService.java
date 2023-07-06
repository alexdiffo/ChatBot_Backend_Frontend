package enspy.letsgo.Chatbot.services;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import enspy.letsgo.Chatbot.models.Message;
import enspy.letsgo.Chatbot.repositories.MessageRepository;
import enspy.letsgo.Chatbot.services.ChatbotUtils.BotResponse;

@Service
public class MessageService {

    @Autowired
	private MessageRepository messageRepository;

    

    public  String categorize(Message message) {
 
        BotResponse botResponse = new BotResponse();        
        
        String category ="";

        try {
            category = botResponse.findCategory(message.getMessageBody());
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return  category;         
    }


    public Message saveMessage(Message message, String responsebody){

        message.setMessageId(UUID.randomUUID().toString().split("-")[0]);
        message.setMessageBody(message.getMessageBody());
        message.setEvent_time(LocalDateTime.now());
        //message.setCategory(category);
        message.setSenderUsername(message.getSenderUsername());
        message.setResponseBody(responsebody);
        messageRepository.save(message);
        System.out.println("Message categorized and saved!!");

        return message;

    }


    public List<Message> getAllMessage(){
        
        return messageRepository.findAll();
        
    }


    
}




    
    
