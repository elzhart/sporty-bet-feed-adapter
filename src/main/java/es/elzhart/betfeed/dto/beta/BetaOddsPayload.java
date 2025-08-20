package es.elzhart.betfeed.dto.beta;

import jakarta.validation.constraints.NotNull;

public record BetaOddsPayload(
        @NotNull Double home,
        @NotNull Double draw,
        @NotNull Double away
) {
}