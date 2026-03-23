package org.example.acg.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.acg.entity.DailySequence;
import java.time.LocalDate;

@Mapper
public interface DailySequenceMapper {

    DailySequence findByDateKey(@Param("dateKey") LocalDate dateKey);

    void insert(DailySequence sequence);

    int updateSeq(@Param("dateKey") LocalDate dateKey, @Param("oldSeq") Integer oldSeq, @Param("newSeq") Integer newSeq);
}