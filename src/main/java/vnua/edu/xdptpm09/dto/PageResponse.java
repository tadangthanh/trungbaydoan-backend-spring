package vnua.edu.xdptpm09.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class  PageResponse<T> implements Serializable {
    private int totalItems;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private T items;
}
