package com.crazycook.tgbot.command;

import com.crazycook.tgbot.entity.Cart;
import com.crazycook.tgbot.service.CartService;
import com.crazycook.tgbot.service.SendBotMessageService;
import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.crazycook.tgbot.Utils.getChatId;
import static com.crazycook.tgbot.Utils.getMessage;
import static com.crazycook.tgbot.Utils.getUserName;
import static com.crazycook.tgbot.bot.Buttons.completeCartButton;
import static com.crazycook.tgbot.bot.Messages.COMMENT_ADDED;
import static com.crazycook.tgbot.entity.CartStatus.IN_PROGRESS;

@AllArgsConstructor
public class CommentCommand implements CrazyCookTGCommand {
    private final SendBotMessageService sendBotMessageService;
    private final CartService cartService;

    @Override
    public void execute(Update update) {
        Long customerChatId = getChatId(update);
        String customerUsername = getUserName(update);
        String message = getMessage(update);
        Cart cart = cartService.findCart(customerChatId, customerUsername);
        cart.setComment(message);
        cart.setStatus(IN_PROGRESS);
        cartService.save(cart);

        sendBotMessageService.sendMessage(customerChatId, COMMENT_ADDED, List.of(List.of(completeCartButton())));
    }
}