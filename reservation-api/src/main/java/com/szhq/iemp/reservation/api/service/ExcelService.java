package com.szhq.iemp.reservation.api.service;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {
    /**
     * 批量导入
     */
     boolean batchImport(String fileName, MultipartFile file);
}
