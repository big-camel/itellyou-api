package com.itellyou.model.geetest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeetestResultModel {
    @NotBlank
    private String key;
    @NotBlank
    private String challenge;
    @NotBlank
    private String validate;
    @NotBlank
    private String seccode;
}
