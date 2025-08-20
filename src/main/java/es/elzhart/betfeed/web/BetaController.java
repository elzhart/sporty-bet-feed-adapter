package es.elzhart.betfeed.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.elzhart.betfeed.queue.EventPublisher;
import es.elzhart.betfeed.service.Standardizer;
import es.elzhart.betfeed.web.dto.beta.BetaMsg;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/provider-beta")
public class BetaController {
    private static final Logger log = LoggerFactory.getLogger(BetaController.class);

    private final Standardizer<BetaMsg> std;
    private final EventPublisher eventPublisher;

    public BetaController(
            @Qualifier("betaStandardizer") Standardizer<BetaMsg> std,
            @Qualifier("inMemoryPublisher") EventPublisher eventPublisher
    ) {
        this.std = std;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/feed")
    public ResponseEntity<Void> feed(@Valid @RequestBody BetaMsg body) {
        // TODO add idempotency
        log.info("BETA received: type='{}' event_id='{}'", body.type(), body.eventId());
        var normalized = std.standardize(body);
        log.info("BETA normalized: eventId='{}' eventType='{}'", normalized.eventId(), normalized.eventType());
        eventPublisher.publish(normalized);
        return ResponseEntity.accepted().build();
    }
}
