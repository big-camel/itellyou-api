DROP TABLE IF EXISTS `WORKER_NODE`;
CREATE TABLE `WORKER_NODE` (
                               `ID` bigint NOT NULL AUTO_INCREMENT COMMENT 'auto increment id',
                               `HOST_NAME` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'host name',
                               `PORT` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'port',
                               `TYPE` int NOT NULL COMMENT 'node type: ACTUAL or CONTAINER',
                               `LAUNCH_DATE` date NOT NULL COMMENT 'launch date',
                               `MODIFIED` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'modified time',
                               `CREATED` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created time',
                               PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='DB WorkerID Assigner for UID Generator';

SET FOREIGN_KEY_CHECKS = 1;