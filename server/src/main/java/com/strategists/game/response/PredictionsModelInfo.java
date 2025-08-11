package com.strategists.game.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PredictionsModelInfo {

    private String modelId;
    private String modelName;
    private int modelVersion;

}
