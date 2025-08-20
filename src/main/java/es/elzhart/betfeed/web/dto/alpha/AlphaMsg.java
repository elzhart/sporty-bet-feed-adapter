package es.elzhart.betfeed.web.dto.alpha;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "msg_type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AlphaOdds.class, name = "odds_update"),
        @JsonSubTypes.Type(value = AlphaSettlement.class, name = "settlement")
})
public sealed interface AlphaMsg permits AlphaOdds, AlphaSettlement {
    @NotBlank
    @JsonProperty("msg_type")
    String msgType();

    @NotBlank
    @JsonProperty("event_id")
    String eventId();
}
