package es.elzhart.betfeed.web.dto.beta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BetaOdds.class, name = "ODDS"),
        @JsonSubTypes.Type(value = BetaSettlement.class, name = "SETTLEMENT")
})
public sealed interface BetaMsg permits BetaOdds, BetaSettlement {
    @NotBlank
    @JsonProperty("msg_type")
    String type();

    @NotBlank
    @JsonProperty("event_id")
    String eventId();
}
