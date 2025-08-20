package es.elzhart.betfeed.web.dto.alpha;

import jakarta.validation.constraints.NotNull;

public record AlphaSettlement(
        String msgType,
        String eventId,
        @NotNull String outcome
) implements AlphaMsg {
}
