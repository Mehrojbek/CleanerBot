package uz.pdp.demo.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private HashSet<Long> hashSet;
    private SendMessage sendMessage;
}
