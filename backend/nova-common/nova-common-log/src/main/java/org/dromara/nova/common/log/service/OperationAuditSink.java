package org.dromara.nova.common.log.service;

import org.dromara.nova.common.log.model.OperationAuditEvent;

/**
 * 业务模块实现该 Sink，将审计事件写入持久层。
 */
public interface OperationAuditSink {
    void save(OperationAuditEvent event);
}
