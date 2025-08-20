package es.elzhart.betfeed.service;


import org.junit.jupiter.api.Test;

import es.elzhart.betfeed.model.CommonEvent;
import es.elzhart.betfeed.model.EventProvider;
import es.elzhart.betfeed.model.EventType;
import es.elzhart.betfeed.model.Result;
import es.elzhart.betfeed.web.dto.alpha.AlphaOdds;
import es.elzhart.betfeed.web.dto.alpha.AlphaOddsValues;
import es.elzhart.betfeed.web.dto.alpha.AlphaSettlement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlphaStandardizerTest {

    private final AlphaStandardizer std = new AlphaStandardizer();

    @Test
    void odds_happy_path_maps_1_X_2_to_home_draw_away() {
        var dto = new AlphaOdds("odds_update", "evA1", new AlphaOddsValues(2.0, 3.1, 3.8));

        CommonEvent e = std.standardize(dto);

        assertThat(e.provider()).isEqualTo(EventProvider.ALPHA);
        assertThat(e.eventId()).isEqualTo("evA1");
        assertThat(e.eventType()).isEqualTo(EventType.ODDS);
        assertThat(e.odds().home()).isEqualTo(2.0);
        assertThat(e.odds().draw()).isEqualTo(3.1);
        assertThat(e.odds().away()).isEqualTo(3.8);
        assertThat(e.result()).isNull();
        assertThat(e.timestamp()).isNotNull();
    }

    @Test
    void settlement_happy_path_maps_outcome_to_enum() {
        var dto = new AlphaSettlement("settlement", "evA2", "2");

        CommonEvent e = std.standardize(dto);

        assertThat(e.eventType()).isEqualTo(EventType.SETTLEMENT);
        assertThat(e.result()).isEqualTo(Result.AWAY);
        assertThat(e.odds()).isNull();
    }

    @Test
    void settlement_unhappy_unknown_outcome_throws() {
        var bad = new AlphaSettlement("settlement", "evA3", "Z");

        assertThatThrownBy(() -> std.standardize(bad))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown alpha outcome");
    }
}