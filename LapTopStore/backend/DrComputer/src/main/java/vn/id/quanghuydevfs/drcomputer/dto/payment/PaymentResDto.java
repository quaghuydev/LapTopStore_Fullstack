package vn.id.quanghuydevfs.drcomputer.dto.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class PaymentResDto implements Serializable {
    private String status;
    private String message;
    private String url;
}
