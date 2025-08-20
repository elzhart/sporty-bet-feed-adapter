package es.elzhart.betfeed.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.elzhart.betfeed.model.CommonEvent;
import es.elzhart.betfeed.model.EventProvider;
import es.elzhart.betfeed.model.EventType;
import es.elzhart.betfeed.model.Odds;
import es.elzhart.betfeed.model.Result;
import es.elzhart.betfeed.web.dto.alpha.AlphaMsg;
import es.elzhart.betfeed.web.dto.alpha.AlphaOdds;
import es.elzhart.betfeed.web.dto.alpha.AlphaOddsValues;
import es.elzhart.betfeed.web.dto.alpha.AlphaSettlement;

public class AlphaStandardizer extends AbstractStandardizer<AlphaMsg> {

    private static final Logger log = LoggerFactory.getLogger(AlphaStandardizer.class);

    @Override
    protected EventProvider provider() {
        return EventProvider.ALPHA;
    }

    @Override
    public CommonEvent standardize(AlphaMsg msg) {
        if (msg instanceof AlphaOdds oddsMsg) {
            AlphaOddsValues v = oddsMsg.values();
            if (v == null) {
                log.warn("ALPHA odds_update: values is null (event_id={})", oddsMsg.eventId());
            }
            Odds odds = new Odds(v != null ? v.one() : null, v != null ? v.draw() : null, v != null ? v.two() : null);
            log.debug("ALPHA map odds: 1={} X={} 2={} -> home={} draw={} away={}",
                    v != null ? v.one() : null, v != null ? v.draw() : null, v != null ? v.two() : null,
                    odds.home(), odds.draw(), odds.away());
            return build(oddsMsg.eventId(), EventType.ODDS, odds, null);
        }
        if (msg instanceof AlphaSettlement stl) {
            Result res = switch (stl.outcome()) {
                case "1" -> Result.HOME;
                case "X", "x" -> Result.DRAW;
                case "2" -> Result.AWAY;
                default -> {
                    log.error("ALPHA settlement: unknown outcome '{}' (event_id={})", stl.outcome(), stl.eventId());
                    throw new IllegalArgumentException("Unknown alpha outcome: " + stl.outcome());
                }
            };
            log.debug("ALPHA map result: '{}' -> {}", stl.outcome(), res);
            return build(stl.eventId(), EventType.SETTLEMENT, null, res);
        }
        log.error("ALPHA: unsupported message class {}", msg.getClass().getSimpleName());
        throw new IllegalArgumentException("Unsupported Alpha message: " + msg.getClass().getSimpleName());
    }
}
