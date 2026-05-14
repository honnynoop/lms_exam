package com.lms.exam.mapper;
import com.lms.exam.dto.request.PageRequest;
import com.lms.exam.model.Option;
import com.lms.exam.model.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface QuestionMapper {
    Optional<Question> findById(@Param("questionId") Long questionId);
    List<Question> findAll(PageRequest pageRequest);
    List<Question> findByCategoryId(@Param("categoryId") Long categoryId, PageRequest pageRequest);
    Long count();
    Long countByCategoryId(@Param("categoryId") Long categoryId);
    void insert(Question question);
    void update(Question question);
    void delete(@Param("questionId") Long questionId);
    List<Option> findOptionsByQuestionId(@Param("questionId") Long questionId);
    void insertOption(Option option);
    void deleteOptionsByQuestionId(@Param("questionId") Long questionId);
}
