package kurpatow.YokoTelegramBot.service;

import kurpatow.YokoTelegramBot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    static final String HELP_TEXT = """
            Этот бот создан для демонстрации возможностей Spring.

            Вы можете выполнять команды из главного меню слева или введя команду:

            Введите /start, чтобы увидеть приветственное сообщение

            Введите /myData, чтобы просмотреть данные, хранящиеся о вас

            Введите /help, чтобы снова увидеть это сообщение""";

    static final String YES_BUTTON = "YES_BUTTON";
    static final String NO_BUTTON = "NO_BUTTON";

    static final String ERROR_TEXT = "Error occurred: ";

    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Приветсвенное сообщение"));
        listOfCommands.add(new BotCommand("/mydata", "Информация о пользователе"));
        listOfCommands.add(new BotCommand("/deletedata", "Удалить информацию о пользователе"));
        listOfCommands.add(new BotCommand("/help", "Как использовать бота"));
        listOfCommands.add(new BotCommand("/settings", "Настройки"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotToken() {return config.getToken();}

    @Override
    public String getBotUsername() {return config.getBotName();}

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                default:
                    sendMessage(chatId, "Прости, такую команду пока не знаю :(");
            }
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Рад приветсвовать тебя, " + name + "!";
        log.info("Ответил пользователю " + name);

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Возникла ошибка: " + e.getMessage());
        }
    }
}
