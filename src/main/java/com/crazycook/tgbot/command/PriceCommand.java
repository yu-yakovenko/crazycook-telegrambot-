package com.crazycook.tgbot.command;

import com.crazycook.tgbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.crazycook.tgbot.bot.Buttons.startButton;
import static com.crazycook.tgbot.bot.Buttons.createOrderButton;
import static com.crazycook.tgbot.Utils.getChatId;

public class PriceCommand implements CrazyCookTGCommand {

    private final SendBotMessageService sendBotMessageService;

    public PriceCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    public final static String PRICE_MESSAGE = """
            Маємо в асортименті три типи боксів:\s
            Бокс S містить 8 макаронів, 280 грн;\s
            Бокс M містить 12 макаронів, 420 грн;\s
            Бокс L містить 18 макаронів, 630 грн;""";

    @Override
    public void execute(Update update) {
        Long chatId = getChatId(update);

        List<InlineKeyboardButton> buttonRow = new ArrayList<>();
        buttonRow.add(startButton());
        buttonRow.add(createOrderButton());

        sendBotMessageService.sendMessage(chatId, PRICE_MESSAGE, List.of(buttonRow));
    }
}
