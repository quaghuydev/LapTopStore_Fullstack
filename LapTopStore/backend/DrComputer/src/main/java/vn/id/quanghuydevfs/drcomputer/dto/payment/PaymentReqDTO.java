package vn.id.quanghuydevfs.drcomputer.dto.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentReqDTO {
    private long amount;
    private String orderInfo;

}
