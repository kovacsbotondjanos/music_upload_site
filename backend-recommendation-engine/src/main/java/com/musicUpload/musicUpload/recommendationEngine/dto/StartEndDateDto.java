package com.musicUpload.musicUpload.recommendationEngine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartEndDateDto {
    private Date startDate;
    private Date endDate;
}
