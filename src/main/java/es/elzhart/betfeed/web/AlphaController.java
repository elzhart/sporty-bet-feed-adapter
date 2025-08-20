package es.elzhart.betfeed.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.elzhart.betfeed.web.dto.alpha.AlphaMsg;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/provider-alpha")
public class AlphaController {
    private static final Logger log = LoggerFactory.getLogger(AlphaController.class);

    @PostMapping("/feed")
    public ResponseEntity<Void> feed(@Valid @RequestBody AlphaMsg body) {
        log.info("ALPHA received: type='{}' event_id='{}'", body.msgType(), body.eventId());

        return ResponseEntity.accepted().build();
    }
}
