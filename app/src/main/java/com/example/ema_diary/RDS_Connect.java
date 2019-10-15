package com.example.ema_diary;

import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;

public class RDS_Connect {

    private ApiClientFactory factory = new ApiClientFactory();
    final EMADiaryAPIClient client = factory.build(EMADiaryAPIClient.class);
}
