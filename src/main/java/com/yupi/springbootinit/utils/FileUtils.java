package com.yupi.springbootinit.utils;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * @author wzc
 * 2024/2/3
 */
public class FileUtils {
    public static Boolean fileValid(MultipartFile file) throws BusinessException {
        //文件大小不超过2MB
        if (file.getSize() > CommonConstant.maxAvatarSize*2) {
            throw new BusinessException(ErrorCode.FILE_OVER_SIZE);
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "文件类型不正确");
        }
        return true;
    }
}
