package uz.pdp.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.pdp.demo.controller.TgBot;

@SpringBootApplication
public class DemoApplication  {
    @Autowired
    TgBot tgBot;
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
    TelegramBotsApi botsApi;
    {
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(tgBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
