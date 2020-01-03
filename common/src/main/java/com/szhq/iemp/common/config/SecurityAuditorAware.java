package com.szhq.iemp.common.config;

import com.szhq.iemp.common.util.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityAuditorAware implements AuditorAware<String> {
    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditorAware.class);
    @Autowired
    private SecurityUtils securityUtils;

    @Override
    public Optional<String> getCurrentAuditor() {
        String userId = securityUtils.getCurrentUserId();
        if (StringUtils.isEmpty(userId)) {
            return Optional.of("");
        }
        logger.debug("getCurrentAuditor,current userId is: " + userId);
        return Optional.of(userId);
    }
}
