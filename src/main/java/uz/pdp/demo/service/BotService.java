package uz.pdp.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import uz.pdp.demo.entity.UserOfBot;
import uz.pdp.demo.repository.BotRepository;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@Service
public class BotService {
    @Autowired
    BotRepository botRepository;


    //    O'zgaruvchilar -->
    SendMessage sendMessage=new SendMessage();
    MessageEntity entity = new MessageEntity();
    MessageEntity entity1 = new MessageEntity();

//    O'zgaruvchilar <--

    //    Salomlashish           -->
    public  SendMessage isStart(Message message) {
        System.out.println("start bosdi");
        sendMessage.setText("Assalomu alaykum\n" +
                "Botga xush kelibsiz\n Bu bot guruhlarni \n" +
                "\uD83D\uDC49 reklama \n" +
                "\uD83D\uDC49 link\n" +
                "\uD83D\uDC49 Arabcha yozuvlar\n" +
                "\uD83D\uDC49 Join va Left"+
                "lardan tozalaydi\n" +
                "\uD83D\uDC49Admin bo'lmagan a'zolarning guruhga bot qo'shishiga yo'l qo'ymaydi\n"+
                "Buning uchun botni guruhga qo'shing va adminlikni bering\n\n" +
                "Ассаламу алейкум\n" +
                "Добро пожаловать в бота\n" +
                " Это группы ботов\n" +
                "\uD83D\uDC49 Реклама\n" +
                "\uD83D\uDC49 Ссылка на сайт\n" +
                "\uD83D\uDC49 Арабское письмо\n" +
                "\uD83D\uDC49 добавиль и покинуль"+
                "сообшение удаляет\n" +
                "\uD83D\uDC49Запрещает участникам, не являющимся администраторами, добавлять ботов в группу\n"+
                "Для этого нужно добавить бота в группу и дать ему админ");
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        return sendMessage;
    }
//     Salomlashish           <--

    //     Delete Sticker         -->
    public DeleteMessage deleteSticker(Message message) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(message.getChatId()));
        deleteMessage.setMessageId(message.getMessageId());
        return deleteMessage;
    }
//    Delete Sticker         <--

    //    Delete link and arabian -->
    public  DeleteMessage deleteLink(Message message){
        DeleteMessage deleteMessage=new DeleteMessage();
        deleteMessage.setMessageId(message.getMessageId());
        deleteMessage.setChatId(String.valueOf(message.getChatId()));
        return deleteMessage;
    }
//    Delete link and arabian <--

    //    SendWarning message     -->
    public  SendMessage sendWarning(Message message){
        entity.setText(message.getFrom().getFirstName());
        entity.setType("text_mention");
        entity.setUrl(null);
        entity.setOffset(2);
        entity.setLength(message.getFrom().getFirstName().length());
        entity.setUser(message.getFrom());
        entity.setLanguage(null);

        entity1.setText(message.getFrom().getFirstName());
        entity1.setType("bold");
        entity1.setUrl(null);
        entity1.setOffset(5+message.getFrom().getFirstName().length());
        entity1.setLength(26);
        entity1.setUser(message.getFrom());
        entity1.setLanguage(null);
        ArrayList<MessageEntity> entities = new ArrayList<>();
        entities.add(entity);
        entities.add(entity1);

        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setEntities(entities);
        sendMessage.setText("❗ " + message.getFrom().getFirstName() + "   iltimos reklama tarqatmang!!!");
        return sendMessage;
    }
//    SendWarning message     <--

    //    Check is Admin          -->
    public GetChatMember getChatMemberStatus(Long chat_id, Long user_id) {
        GetChatMember getChatMember=new GetChatMember();
        getChatMember.setChatId(String.valueOf(chat_id));
        getChatMember.setUserId(user_id);
        return getChatMember;
    }
//    Check is Admin          <--



//    Return data
    public HashSet<Long> returnData() {
        HashSet<Long> allChatId=new HashSet<>();
        List<UserOfBot> botList = botRepository.findAll();
        for (UserOfBot userOfBot : botList) {
            allChatId.add(userOfBot.getChatId());
        }
        return allChatId;
    }

//    Delete user
    public void deleteUser(Long chatId){
        try {
            botRepository.deleteByChatId(chatId);
        }catch (Exception e){ }

    }

//    New user
    public SendMessage newUser(Long chatId, String userName, long memberCount){
        sendMessage.setChatId("1668408330");
        boolean exists = botRepository.existsByChatId(chatId);
        if (exists){
            sendMessage.setText("avval bor ekan");
            return sendMessage;
        }

        UserOfBot userOfBot=new UserOfBot(chatId,userName,memberCount);
        try {
            UserOfBot save = botRepository.save(userOfBot);
            sendMessage.setText("qo'shti "+save.getId());
            return sendMessage;
        }catch (Exception e){
            sendMessage.setText(e.getMessage());
            sendMessage.setChatId("1668408330");
            return sendMessage;
        }
    }



}
