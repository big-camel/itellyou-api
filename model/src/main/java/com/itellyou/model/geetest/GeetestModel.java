package com.itellyou.model.geetest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeetestModel implements Serializable {
    private String key;
    private String challenge;
    private String gt;
    private Integer success;
    private Integer newCaptcha;
}
