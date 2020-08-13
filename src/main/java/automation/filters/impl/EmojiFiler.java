package automation.filters.impl;

import automation.filters.MessageFilter;
import com.vdurmont.emoji.EmojiParser;

public class EmojiFiler implements MessageFilter {

    @Override
    public String apply(String message) {
        return EmojiParser.parseToUnicode(message);
    }
}
