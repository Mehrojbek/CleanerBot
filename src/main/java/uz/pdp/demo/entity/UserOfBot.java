package uz.pdp.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class UserOfBot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private Long chatId;

    private String userName;

    private Long numberOfUsers;


    public UserOfBot(Long chatId, String userName, Long numberOfUsers) {
        this.chatId = chatId;
        this.userName = userName;
        this.numberOfUsers = numberOfUsers;
    }
}
