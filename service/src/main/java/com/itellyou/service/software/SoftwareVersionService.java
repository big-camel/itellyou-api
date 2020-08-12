package com.itellyou.service.software;

import com.itellyou.model.software.SoftwareVersionModel;

public interface SoftwareVersionService {
    int insert(SoftwareVersionModel versionModel);

    int update(SoftwareVersionModel versionModel);

    int updateVersion(Long softwareId, Integer version, Long ip, Long user);

    int updateVersion(Long softwareId, Integer version, Boolean isPublished, Long ip, Long user);

    int updateVersion(Long softwareId, Integer version, Integer draft, Boolean isPublished, Long time, Long ip, Long user);

    int updateVersion(SoftwareVersionModel versionModel);

    int updateDraft(Long softwareId, Integer version, Boolean isPublished, Long time, Long ip, Long user);

    int updateDraft(Long softwareId, Integer version, Long time, Long ip, Long user);

    int updateDraft(SoftwareVersionModel versionModel);
}
