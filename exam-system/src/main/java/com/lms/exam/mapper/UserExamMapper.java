package com.lms.exam.mapper;
import com.lms.exam.dto.request.PageRequest;
import com.lms.exam.model.UserAnswer;
import com.lms.exam.model.UserExam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface UserExamMapper {
    Optional<UserExam> findById(@Param("userExamId") Long userExamId);
    Optional<UserExam> findByUserIdAndExamId(@Param("userId") Long userId, @Param("examId") Long examId);
    List<UserExam> findByUserId(@Param("userId") Long userId, PageRequest pageRequest);
    void insert(UserExam userExam);
    void update(UserExam userExam);
    List<UserAnswer> findAnswersByUserExamId(@Param("userExamId") Long userExamId);
    void insertAnswer(UserAnswer answer);
}
