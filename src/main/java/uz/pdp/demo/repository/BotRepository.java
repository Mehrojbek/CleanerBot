package uz.pdp.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.pdp.demo.entity.UserOfBot;


import java.util.List;

@Repository
public interface BotRepository extends JpaRepository<UserOfBot,Integer> {


    @Query(value = "select chat_id from user_of_bot",nativeQuery = true)
    List<Long> getAllChatId();

    @Query(value = "select * from users",nativeQuery = true)
    List<Long> getAllOldUsers();

    boolean existsByChatId(Long chatId);


    @Query(value = "delete from user_of_bot where chat_id=:chatId",nativeQuery = true)
    void deleteByChatId(@Param("chatId") Long chatId);
}
