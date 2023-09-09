package com.crazycook.tgbot.command;

import com.crazycook.tgbot.entity.Box;
import com.crazycook.tgbot.entity.BoxSize;
import com.crazycook.tgbot.entity.Cart;
import com.crazycook.tgbot.service.BoxService;
import com.crazycook.tgbot.service.CartService;
import com.crazycook.tgbot.service.SendBotMessageService;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.crazycook.tgbot.Utils.getChatId;
import static com.crazycook.tgbot.Utils.getUserName;
import static com.crazycook.tgbot.bot.Buttons.addMoreButton;
import static com.crazycook.tgbot.bot.Buttons.chooseDeliveryButton;
import static com.crazycook.tgbot.bot.Buttons.chooseFlavorsLongButton;
import static com.crazycook.tgbot.bot.Messages.BOLD_END;
import static com.crazycook.tgbot.bot.Messages.BOLD_START;
import static com.crazycook.tgbot.bot.Messages.FOUR_SPACES;
import static com.crazycook.tgbot.bot.Messages.IN_YOUR_CART;
import static com.crazycook.tgbot.bot.Messages.LINE_END;
import static com.crazycook.tgbot.bot.Messages.ONE_SPACE;
import static com.crazycook.tgbot.bot.Messages.RED_DIAMOND;
import static com.crazycook.tgbot.bot.Messages.YOUR_CART_IS_EMPTY;

@AllArgsConstructor
public class ShowCartCommand implements CrazyCookTGCommand {

    private final SendBotMessageService sendBotMessageService;
    private final CartService cartService;
    private final BoxService boxService;

    @Override
    @Transactional
    public void execute(Update update) {
        Long chatId = getChatId(update);
        String username = getUserName(update);
        Cart cart = cartService.findCart(chatId, username);

        StringBuilder message = new StringBuilder(IN_YOUR_CART);
        Set<Box> boxes = cartService.getBoxesForCart(cart.getId());

        int filledSNumber = getBoxNumber(boxes, BoxSize.S);
        int filledMNumber = getBoxNumber(boxes, BoxSize.M);
        int filledLNumber = getBoxNumber(boxes, BoxSize.L);

        boolean emptyBoxes = addMessageForEmptyBoxes(message, filledSNumber, cart.getSNumber(), BoxSize.S)
                || addMessageForEmptyBoxes(message, filledMNumber, cart.getMNumber(), BoxSize.M)
                || addMessageForEmptyBoxes(message, filledLNumber, cart.getLNumber(), BoxSize.L);

        message.append(cartService.flavorMixToString(cart));

        List<String> flavorDescription = boxes.stream().map(boxService::flavorQuantitiesToString).collect(Collectors.toList());

        for (String s : flavorDescription) {
            message.append(s);
        }


        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(List.of(addMoreButton()));
        if (emptyBoxes) {
            buttons.add(List.of(chooseFlavorsLongButton()));
        } else {
            buttons.add(List.of(chooseDeliveryButton()));
        }

        if (IN_YOUR_CART.equals(message.toString())) {
            message.append(FOUR_SPACES).append(YOUR_CART_IS_EMPTY);
        }

        sendBotMessageService.sendMessage(getChatId(update), message.toString(), buttons);
    }

    private boolean addMessageForEmptyBoxes(StringBuilder message, int filledSNumber, int thisSizeNumber, BoxSize size) {
        boolean emptyBoxes = false;
        if (thisSizeNumber > filledSNumber) {
            message.append(RED_DIAMOND).append(ONE_SPACE).append(BOLD_START)
                    .append(thisSizeNumber - filledSNumber).append(" пустих ").append(size).append(" боксів")
                    .append(BOLD_END).append(LINE_END);
            emptyBoxes = true;
        }
        return emptyBoxes;
    }

    private int getBoxNumber(Set<Box> boxes, BoxSize size) {
        return (int) boxes.stream().filter(b -> size.equals(b.getBoxSize())).count();
    }
}
