package com.example.wealthwise_api.DTO;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@NonNull
public class ExpensesDateRangeRequest {
    private Date endDate;
    private Date startDate;
    private String categoryName;
}
