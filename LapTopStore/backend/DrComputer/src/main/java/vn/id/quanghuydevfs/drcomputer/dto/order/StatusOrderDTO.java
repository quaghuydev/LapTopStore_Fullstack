package vn.id.quanghuydevfs.drcomputer.dto.order;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusOrderDTO {
    private String status;
    private String description;

}