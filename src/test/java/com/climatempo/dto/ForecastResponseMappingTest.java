
package com.climatempo.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ForecastResponseMappingTest {
    @Test
    void deserializaRain3h() throws Exception {
        String json = "{ \"list\": [ { \"dt\": 1694090400, \"main\": { \"temp\": 25.3, \"feels_like\": 25.1, \"humidity\": 40 }, \"wind\": { \"speed\": 3.0 }, \"pop\": 0.2, \"rain\": { \"3h\": 1.42 } } ] }";
        ObjectMapper om = new ObjectMapper();
        ForecastResponse fr = om.readValue(json, ForecastResponse.class);
        var item = fr.list().get(0);
        assertThat(item.rain().threeH()).isEqualTo(1.42);
    }
}
