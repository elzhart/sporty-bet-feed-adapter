package es.elzhart.betfeed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import es.elzhart.betfeed.model.CommonEvent;
import es.elzhart.betfeed.queue.InMemoryEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class QueueEventPublishingIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    InMemoryEventPublisher publisher;

    @BeforeEach
    void clearQueue() {
        publisher.clear();
    }

    @Test
    public void alpha_event_feed() throws Exception {
        String body = """
                {"msg_type":"odds_update","event_id":"evQ1","values":{"1":2.0,"X":3.1,"2":3.8}}
                """;

        mockMvc.perform(post("/provider-alpha/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());

        assertThat(publisher.size()).isEqualTo(1);
        CommonEvent e = publisher.snapshot().get(0);
        assertThat(e.eventId()).isEqualTo("evQ1");
        assertThat(e.odds().home()).isEqualTo(2.0);
    }

    @Test
    public void beta_event_feed() throws Exception {
        String body = """
                   {"type":"SETTLEMENT","event_id":"evQ2","result":"away"}
                """;

        mockMvc.perform(post("/provider-beta/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());

        assertThat(publisher.size()).isEqualTo(1);
        CommonEvent e = publisher.snapshot().get(0);
        assertThat(e.eventId()).isEqualTo("evQ2");
        assertThat(e.result().name()).isEqualTo("AWAY");
    }
}
