package vn.id.quanghuydevfs.drcomputer.controller.admin.upload.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class ImageResponse {
    @JsonProperty("public_id")
    private String public_id;
    @JsonProperty("url")
    private String url;

}
