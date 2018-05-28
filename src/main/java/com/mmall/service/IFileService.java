package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by wangliyong on 2018/5/28.
 */
public interface IFileService {
    String upload (MultipartFile file, String path);
}
