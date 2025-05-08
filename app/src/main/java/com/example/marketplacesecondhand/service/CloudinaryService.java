package com.example.marketplacesecondhand.service;

import android.net.Uri;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryService {
    public static Cloudinary cloudinary;

    public static void initCloudinary() {
        if (cloudinary == null) {
            Map config = new HashMap();
            config.put("cloud_name", "dfqf9wjji");
            config.put("api_key", "858621571969466");
            config.put("api_secret", "BPMW1rZAexoe15H4i8pmBTsm9Qo");
            config.put("secure", true);
            cloudinary = new Cloudinary(config);
        }
    }

    public static Cloudinary getCloudinary() {
        if (cloudinary == null) {
            initCloudinary();
        }
        return cloudinary;
    }

}
