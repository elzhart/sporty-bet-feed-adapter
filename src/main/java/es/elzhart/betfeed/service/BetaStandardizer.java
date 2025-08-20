package es.elzhart.betfeed.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Locale;

import es.elzhart.betfeed.model.CommonEvent;
import es.elzhart.betfeed.model.EventProvider;
import es.elzhart.betfeed.model.EventType;
import es.elzhart.betfeed.model.Odds;
import es.elzhart.betfeed.model.Result;
import es.elzhart.betfeed.web.dto.beta.BetaMsg;
import es.elzhart.betfeed.web.dto.beta.BetaOdds;
import es.elzhart.betfeed.web.dto.beta.BetaOddsPayload;
import es.elzhart.betfeed.web.dto.beta.BetaSettlement;

@Service("betaStandardizer")
public class BetaStandardizer extends AbstractStandardizer<BetaMsg> {

    private static final Logger log = LoggerFactory.getLogger(BetaStandardizer.class);

    @Override
    protected EventProvider provider() {
        return EventProvider.BETA;
    }

    @Override
    public CommonEvent standardize(BetaMsg msg) {
        if (msg instanceof BetaOdds oddsMsg) {
            BetaOddsPayload o = oddsMsg.odds();
            if (o == null) log.warn("BETA odds: payload is null (event_id={})", oddsMsg.eventId());
            Odds odds = new Odds(o.home(), o.draw(), o.away());
            log.debug("BETA map odds: home={} draw={} away={}", odds.home(), odds.draw(), odds.away());
            return build(oddsMsg.eventId(), EventType.ODDS, odds, null);
        }
        if (msg instanceof BetaSettlement stl) {
            String raw = stl.result();
            Result res = switch (raw == null ? "" : raw.toLowerCase(Locale.ROOT)) {
                case "home" -> Result.HOME;
                case "draw" -> Result.DRAW;
                case "away" -> Result.AWAY;
                default -> {
                    log.error("BETA settlement: unknown result '{}' (event_id={})", raw, stl.eventId());
                    throw new IllegalArgumentException("Unknown beta result: " + raw);
                }
            };
            log.debug("BETA map result: '{}' -> {}", raw, res);
            return build(stl.eventId(), EventType.SETTLEMENT, null, res);
        }
        log.error("BETA: unsupported message class {}", msg.getClass().getSimpleName());
        throw new IllegalArgumentException("Unsupported Beta message: " + msg.getClass().getSimpleName());
    }
}