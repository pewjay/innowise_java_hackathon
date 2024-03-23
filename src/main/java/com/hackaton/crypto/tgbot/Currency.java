package com.hackaton.crypto.tgbot;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Currency {
    private String symbol;
    private Float price;
}
