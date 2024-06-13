package vn.id.quanghuydevfs.drcomputer.util.upload.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dlhjzufdj",
                "api_key", "344655146293534",
                "api_secret", "ZX6B1Vdw7hGiqRnewuJk0soiygw",
                "secure", true
        ));
    }
}
