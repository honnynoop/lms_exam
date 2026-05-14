package com.lms.exam.dto.request;
import lombok.Data;

@Data
public class PageRequest {
    private Integer page = 1;
    private Integer size = 10;
    private String sortBy = "createdAt";
    private String sortOrder = "DESC";
    
    public Integer getOffset() {
        return (page - 1) * size;
    }
}
