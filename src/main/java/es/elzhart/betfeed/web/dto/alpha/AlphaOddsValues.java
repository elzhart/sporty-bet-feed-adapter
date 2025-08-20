package es.elzhart.betfeed.web.dto.alpha;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AlphaOddsValues(
        @JsonProperty("1") Double one,
        @JsonProperty("X") Double draw,
        @JsonProperty("2") Double two
) {
}
