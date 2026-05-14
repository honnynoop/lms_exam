package com.lms.exam.dto.response;
import com.lms.exam.dto.request.PageRequest;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private Integer page;
    private Integer size;
    private Long total;
    private Integer totalPages;
    
    public static <T> PageResponse<T> of(List<T> content, PageRequest pageRequest, Long total) {
        return PageResponse.<T>builder()
                .content(content)
                .page(pageRequest.getPage())
                .size(pageRequest.getSize())
                .total(total)
                .totalPages((int) Math.ceil((double) total / pageRequest.getSize()))
                .build();
    }
}
