package es.elzhart.betfeed.unit.beta;


import org.junit.jupiter.api.Test;

import es.elzhart.betfeed.model.CommonEvent;
import es.elzhart.betfeed.model.EventProvider;
import es.elzhart.betfeed.model.EventType;
import es.elzhart.betfeed.model.Result;
import es.elzhart.betfeed.service.BetaStandardizer;
import es.elzhart.betfeed.web.dto.beta.BetaOdds;
import es.elzhart.betfeed.web.dto.beta.BetaOddsPayload;
import es.elzhart.betfeed.web.dto.beta.BetaSettlement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BetaStandardizerTest {

    private final BetaStandardizer std = new BetaStandardizer();

    @Test
    void odds_happy_path_maps_payload() {
        var dto = new BetaOdds("ODDS", "evB1", new BetaOddsPayload(1.95, 3.2, 4.0));

        CommonEvent e = std.standardize(dto);

        assertThat(e.provider()).isEqualTo(EventProvider.BETA);
        assertThat(e.eventId()).isEqualTo("evB1");
        assertThat(e.eventType()).isEqualTo(EventType.ODDS);
        assertThat(e.odds().home()).isEqualTo(1.95);
        assertThat(e.odds().draw()).isEqualTo(3.2);
        assertThat(e.odds().away()).isEqualTo(4.0);
        assertThat(e.result()).isNull();
    }

    @Test
    void settlement_happy_path_maps_result() {
        var dto = new BetaSettlement("SETTLEMENT", "evB2", "draw");

        CommonEvent e = std.standardize(dto);

        assertThat(e.eventType()).isEqualTo(EventType.SETTLEMENT);
        assertThat(e.result()).isEqualTo(Result.DRAW);
        assertThat(e.odds()).isNull();
    }

    @Test
    void settlement_unhappy_unknown_result_throws() {
        var bad = new BetaSettlement("SETTLEMENT", "evB3", "weird");

        assertThatThrownBy(() -> std.standardize(bad))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown beta result");
    }

    @Test
    void odds_unhappy_null_payload_currently_causes_npe() {
        var bad = new BetaOdds("ODDS", "evB4", null);

        assertThatThrownBy(() -> std.standardize(bad)).isInstanceOf(NullPointerException.class);
    }
}