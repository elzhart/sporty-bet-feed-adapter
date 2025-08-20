package es.elzhart.betfeed.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.elzhart.betfeed.web.dto.beta.BetaMsg;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/provider-beta")
public class BetaController {
    private static final Logger log = LoggerFactory.getLogger(BetaController.class);

    @PostMapping("/feed")
    public ResponseEntity<Void> feed(@Valid @RequestBody BetaMsg body) {
        log.info("BETA received: type='{}' event_id='{}'", body.type(), body.eventId());

        return ResponseEntity.accepted().build();
    }
}
