package es.elzhart.betfeed.web.dto.beta;

import jakarta.validation.constraints.NotNull;

public record BetaOdds(
        String type,
        String eventId,
        @NotNull BetaOddsPayload odds
) implements BetaMsg {
}
