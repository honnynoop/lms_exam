package com.lms.exam.mapper;

import com.lms.exam.model.QuestionStatistics;
import org.apache.ibatis.annotations.Mapper;
import java.util.Optional;

@Mapper
public interface QuestionStatisticsMapper {
    Optional<QuestionStatistics> findByQuestionId(Long questionId);
    void insert(QuestionStatistics statistics);
    void update(QuestionStatistics statistics);
    void incrementAttempts(Long questionId);
    void incrementCorrect(Long questionId);
    void incrementWrong(Long questionId);
}
