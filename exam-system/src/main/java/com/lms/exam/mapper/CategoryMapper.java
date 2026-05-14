package com.lms.exam.mapper;
import com.lms.exam.model.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface CategoryMapper {
    Optional<Category> findById(@Param("categoryId") Long categoryId);
    List<Category> findAll();
    List<Category> findAllWithStats();
    void insert(Category category);
    void update(Category category);
    void delete(@Param("categoryId") Long categoryId);
    Long count();
}
