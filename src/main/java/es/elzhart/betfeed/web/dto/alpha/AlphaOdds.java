package es.elzhart.betfeed.web.dto.alpha;

import jakarta.validation.constraints.NotNull;

public record AlphaOdds(
        String msgType,
        String eventId,
        @NotNull AlphaOddsValues values
) implements AlphaMsg {
}
