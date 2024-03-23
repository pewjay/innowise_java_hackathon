package com.hackaton.crypto.tgbot;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StartCommand implements Command {
    @Override
    public SendMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText("List of available currencies");
        addKeyboard(sendMessage);
        return sendMessage;
    }

    public void addKeyboard(SendMessage sendMessage) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Currency>> response = restTemplate.exchange(
                "https://api.mexc.com/api/v3/ticker/price",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        List<Currency> jsonObjects = response.getBody();
        List<String> symbols = jsonObjects.stream().limit(5).map(Currency::getSymbol).toList();
        InlineKeyboardMarkup replyKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        for (var cur : symbols) {
            InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
            keyboardButton.setText(cur);
            keyboardButton.setCallbackData(cur);
            keyboardButtonsRow.add(keyboardButton);
        }
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);
        replyKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }
}
