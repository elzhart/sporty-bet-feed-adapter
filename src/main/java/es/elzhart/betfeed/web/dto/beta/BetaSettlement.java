package es.elzhart.betfeed.web.dto.beta;

import jakarta.validation.constraints.NotBlank;

public record BetaSettlement(
        String type,
        String eventId,
        @NotBlank String result // "home" | "draw" | "away"
) implements BetaMsg {
}
