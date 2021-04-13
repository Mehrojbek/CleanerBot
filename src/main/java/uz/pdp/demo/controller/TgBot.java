package uz.pdp.demo.controller;


import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMembersCount;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.demo.repository.BotRepository;
import uz.pdp.demo.service.BotService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;


@Component
public class TgBot extends TelegramLongPollingBot {
    @Autowired
    BotService botService;
    @Autowired
    BotRepository botRepository;

    Set<Long> chatIdSet=new HashSet<>();
    ArrayList<String> arabian = new ArrayList<>(67);
    HashMap<Long,Integer> adminMap = new HashMap<>();

    BufferedReader reader;

    {
        try {
            reader = new BufferedReader(new FileReader("src/main/resources/arabian.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                arabian.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @Override
    public String getBotUsername() {
        return "Cleaner_groups_bot";
    }

    @Override
    public String getBotToken() {
        return "1677029522:AAEzoaD4yVg0-zJabMTLpaHmODdBrUXFfRI";
    }


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage=new SendMessage();

        sendMessage.setText("Nima gap ozi");
        sendMessage.setChatId("1668408330");
        execute(sendMessage);

        DeleteMessage deleteMessage = new DeleteMessage();
        if (chatIdSet.isEmpty()) {
            chatIdSet = botService.returnData();
            sendMessage.setText("size="+chatIdSet);
            sendMessage.setChatId("1668408330");
            execute(sendMessage);
        }
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            Long userId = message.getFrom().getId();
            String userName = message.getFrom().getUserName();
            Integer messageId = message.getMessageId();


            String check="";
            GetChatMember chatMemberStatus = botService.getChatMemberStatus(chatId, userId);
            try {
                check=execute(chatMemberStatus).getStatus();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            boolean checkAdmin = check.equals("creator") || check.equals("administrator");

//      Join and Left Hider -->
            if (message.getLeftChatMember() != null) {
                if (message.getLeftChatMember().getId() == 1677029522) {
                    botService.deleteUser(chatId);
                    chatIdSet=botService.returnData();
                    return;
//       remove user
                } else {
                    deleteMessage.setChatId(String.valueOf(message.getChatId()));
                    deleteMessage.setMessageId(message.getMessageId());

                    execute(deleteMessage);

                }
            }
//            Add memmber
            if (message.getNewChatMembers().size() > 0) {
                if (message.getNewChatMembers().get(0).getId() == 1677029522) {
                    if (chatId<0){
                        GetChatMembersCount count=new GetChatMembersCount(String.valueOf(chatId));
                        String memberCount = execute(count).toString();
                        botService.newUser(chatId,userName,Long.parseLong(memberCount));
                    }else {
                        botService.newUser(chatId,userName,1l);
                    }
                    chatIdSet= botService.returnData();
                    return;
                } else {
                    if (message.getNewChatMembers().get(0).getIsBot() && !checkAdmin) {
                        User user = message.getNewChatMembers().get(0);
                        KickChatMember kickChatMember=new KickChatMember();
                        kickChatMember.setChatId(String.valueOf(chatId));
                        kickChatMember.setUserId(user.getId());
                        execute(kickChatMember);
                    }
                    deleteMessage.setChatId(String.valueOf(message.getChatId()));
                    deleteMessage.setMessageId(message.getMessageId());
                    try {
                        execute(deleteMessage);
                    }catch (Exception e){}

                    return;
                }
            }

//      Join and Left Hider <--
//      Stickerni uchirish -->
            if (message.hasSticker() && message.getCaptionEntities()!=null) {
                if (!checkAdmin) {
                    execute(botService.deleteSticker(message));
                    return;
                }
            }

            //CHECK HAS TEXT
            if (message.hasText()) {
                String text = message.getText();

//                ADMIN PANEL LOGIN
                if (text.equals("exit")){
                    adminMap.clear();
                }

                if (text.equals("login")){
                    adminMap.put(chatId,1);
                    sendMessage.setText("parolni kiriting");
                    sendMessage.setChatId(String.valueOf(chatId));
                    execute(sendMessage);
                    return;
                }
                // PASSWORD
                if (text.equals("970599")){
                    if (adminMap.containsKey(chatId))
                        adminMap.put(chatId,2);
                    deleteMessage.setMessageId(messageId);
                    deleteMessage.setChatId(String.valueOf(chatId));
                    execute(deleteMessage);

                    sendMessage.setText("xush kelibsiz\nrestore\nsendAll");
                    sendMessage.setChatId(String.valueOf(chatId));
                    execute(sendMessage);
                    return;
                }
                //RESTORE
                if (text.equals("restore")){
                    if (adminMap.get(chatId)==2){
                        GetChatMembersCount count=new GetChatMembersCount();
                        List<Long> oldUsers = botRepository.getAllOldUsers();
                        for (Long oldUser : oldUsers) {
                            count.setChatId(String.valueOf(oldUser));
                            if (oldUser<0){

                                String members;
                                try {
                                    members=execute(count).toString();
                                }catch (Exception e){
                                    members=null;
                                }

                                long membersCount=0l;
                                if (members!=null)
                                    membersCount=Long.parseLong(members);

                                botService.newUser(oldUser,"null",membersCount);
                            }else {
                                botService.newUser(oldUser,"null",1l);
                            }

                        }
                    }
                }
                //SEND ALL
                if (text.equals("sendAll")){
                    adminMap.put(chatId,3);
                    sendMessage.setChatId(String.valueOf(chatId));
                    sendMessage.setText("postni jo'nating");
                    execute(sendMessage);
                }
                //IS OK
                if (text.equals("ok")){
                    if (adminMap.get(chatId)==3){
                        ForwardMessage copyMessage = new ForwardMessage();
                        copyMessage.setFromChatId(String.valueOf(chatId));
                        copyMessage.setMessageId(messageId-1);

                        ArrayList<Long> set = new ArrayList<>(botService.returnData());

                        for (int i = 0; i < set.size(); i++) {
                            if ((i+2)%30==0){
                                LocalTime time=LocalTime.now();
                                while (Math.abs(Duration.between(time,LocalTime.now()).toMillis())<1200){}
                            }
                            copyMessage.setChatId(String.valueOf(set.get(i)));
                            try {
                                execute(copyMessage);
                            }catch (TelegramApiException e){
                                if (e.toString().contains("bot was blocked by the user")) {
                                    botService.deleteUser(set.get(i));
                                }
                            }

                        }
                        sendMessage.setText("Yuborildi");
                        sendMessage.setChatId(String.valueOf(chatId));
                        execute(sendMessage);
                        return;
                    }
                }



                //CHECK NEW MEMBER
                if (!chatIdSet.contains(chatId)) {
                    sendMessage.setText("yangi chiqti");
                    sendMessage.setChatId("1668408330");
                    execute(sendMessage);
                    if (chatId<0){
                        GetChatMembersCount count=new GetChatMembersCount(String.valueOf(chatId));
                        String memberCount = execute(count).toString();
                        sendMessage = botService.newUser(chatId, userName, Long.parseLong(memberCount));
                    }else {
                        sendMessage = botService.newUser(chatId, userName, 1l);
                    }
                    execute(sendMessage);
                    chatIdSet= botService.returnData();
                }
//      Salomlashish -->
                if (text.equals("/start")) {
                    execute(botService.isStart(message));
//      Salomlashish <--

                } else {
                    if (message.getChatId() < 0) {
//      Linkni va Arab yozuvni uchirish -->

                        //CHECK IS ADMIN
                        if (!checkAdmin) {
                            System.out.println("text -> " + text);
                            String type = "";
                            if (message.hasEntities()) {
                                List<MessageEntity> entity = message.getEntities();
                                for (int i = 0; i < entity.size(); i++) {
                                    type += entity.get(i).getType();
                                }
                            }

                            //CHECK HAVE ARABIAN
                            for (int i = 0; i < arabian.size(); i++) {
                                if (text.contains(arabian.get(i))) {
                                    type = "url";
                                }
                            }

                            //CHECK CAPTION URL
                            if (message.getCaptionEntities()!=null){
                                List<MessageEntity> captionEntities = message.getCaptionEntities();
                                for (MessageEntity captionEntity : captionEntities) {
                                    if (captionEntity.getType().equals("mention") || captionEntity.getType().equals("url")) {
                                        type="url";
                                    }
                                }
                            }

                            //CHECK HAVE URL
                            if (type.contains("url") || type.contains("text_link") || type.contains("mention")) {
                                try {
                                    execute(botService.deleteLink(message));
                                    execute(botService.sendWarning(message));
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            }
//      Linkni va Arab yozuvni uchirish <--
                        }
                    }
                }
            }
        }
    }
}

