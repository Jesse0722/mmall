package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by jesse on 2017/6/4.
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
