package com.multithreading.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MyPayload {
    private String payloadId;
    private String data;
}

