package com.prepzone.request;

import lombok.Data;

@Data
public class TextExtractRequest {
    private String imageBase64;
    private String mimeType;
}
