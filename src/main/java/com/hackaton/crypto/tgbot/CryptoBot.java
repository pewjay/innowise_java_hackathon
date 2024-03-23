package com.hackaton.crypto.tgbot;

import com.hackaton.crypto.model.TelegramUser;
import com.hackaton.crypto.repository.CryptoRepository;
import com.hackaton.crypto.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
@EnableScheduling
public class CryptoBot extends TelegramLongPollingBot {
    public final BotConfig config;
    public final CommandsHandler commandsHandler;
    public final CryptoRepository cryptoRepository;
    public final UserRepository userRepository;
    @Override
    public String getBotUsername() {return config.getName();}
    @Override
    public String getBotToken() {return config.getToken();}
    @Override
    public void onUpdateReceived(@NotNull Update update) {
        if(update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            SendMessage message = new SendMessage();
            message.setChatId(update.getCallbackQuery().getMessage().getChatId());
            message.setText(callbackData);
            sendMessage(message);
            subscribeUser(update);
        }
        else if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            if (update.getMessage().getText().startsWith("/")) {
                sendMessage(commandsHandler.handleCommands(update));
            }
            else if(Objects.equals(update.getMessage().getText(), "Hello")) {
                sendMessage(new SendMessage(chatId, "Hello"));
            }
            else {
                sendMessage(new SendMessage(chatId, "Consts.CANT_UNDERSTAND"));
            }
        }
    }
    private void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void subscribeUser(Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(update.getCallbackQuery().getMessage().getChatId());
        message.setText("subscribing process");
        sendMessage(message);
        TelegramUser user = new TelegramUser();
        user.setChatId(update.getCallbackQuery().getMessage().getChatId());
        user.setCurrency(update.getCallbackQuery().getData());
        userRepository.save(user);
    }

    @Scheduled(initialDelay = 1000, fixedRate = 20000)
	public void runEvery20Seconds() {
		List<TelegramUser> users = userRepository.findAll();
        for(TelegramUser user : users) {
            SendMessage message = new SendMessage();
            message.setChatId(user.getChatId());
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Currency> response = restTemplate.exchange(
                    "https://api.mexc.com/api/v3/ticker/price?symbol="+user.getCurrency(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            Currency currency = response.getBody();
            message.setText("Now price of "+currency.getSymbol()+" is "+currency.getPrice().toString());
            sendMessage(message);
        }
	}
}


