package com.szhq.iemp.device.api.service;

import com.szhq.iemp.device.api.model.TversionFunction;

public interface VersionFunctionService {

    TversionFunction findByFwVersion(String fwVersion);

}
