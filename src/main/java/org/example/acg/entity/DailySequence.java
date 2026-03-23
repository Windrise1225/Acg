package org.example.acg.entity;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailySequence {
    private LocalDate dateKey;
    private Integer currentSeq;
}