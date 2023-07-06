package enspy.letsgo.Chatbot.repositories;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import enspy.letsgo.Chatbot.models.Message;

@Repository
public interface MessageRepository extends CassandraRepository<Message, String> {
    
}
