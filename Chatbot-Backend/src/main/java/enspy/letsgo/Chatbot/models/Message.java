package enspy.letsgo.Chatbot.models;

import java.time.LocalDateTime;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("message")
public class Message {

    
    @PrimaryKey
    String messageId;
    String messageBody;
    String responseBody;
    String SenderUsername;
    String category;
    LocalDateTime event_time;
}
