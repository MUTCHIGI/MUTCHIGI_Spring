package com.CAUCSD.MUTCHIGI.quizSong.answer;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class GPTRequest {

    private String model;
    private List<OpenAPIMessageDTO> messages;
    private Map<String, Object> response_format;

    public GPTRequest(String model, String prompt, Map<String, Object> response_format) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new OpenAPIMessageDTO("user", prompt));
        this.response_format = response_format;
    }
}
