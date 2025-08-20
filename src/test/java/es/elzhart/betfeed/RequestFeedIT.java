package es.elzhart.betfeed;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class RequestFeedIT {
    @Autowired
    MockMvc mockMvc;

    @Test
    public void alpha_event_feed() throws Exception {
        String body = """
                {"msg_type":"odds_update","event_id":"evQ1","values":{"1":2.0,"X":3.1,"2":3.8}}
                """;

        mockMvc.perform(post("/provider-alpha/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());
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
    }
}
