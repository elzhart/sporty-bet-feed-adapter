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
import es.elzhart.betfeed.web.dto.alpha.AlphaMsg;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/provider-alpha")
public class AlphaController {
    private static final Logger log = LoggerFactory.getLogger(AlphaController.class);

    private final Standardizer<AlphaMsg> std;
    private final EventPublisher eventPublisher;

    public AlphaController(
            @Qualifier("alphaStandardizer") Standardizer<AlphaMsg> std,
            @Qualifier("inMemoryPublisher")EventPublisher eventPublisher
    ) {
        this.std = std;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/feed")
    public ResponseEntity<Void> feed(@Valid @RequestBody AlphaMsg body) {
        // TODO add idempotency
        log.info("ALPHA received: type='{}' event_id='{}'", body.msgType(), body.eventId());
        var normalized = std.standardize(body);
        log.info("ALPHA normalized: eventId='{}' eventType='{}'", normalized.eventId(), normalized.eventType());
        eventPublisher.publish(normalized);
        return ResponseEntity.accepted().build();
    }
}
