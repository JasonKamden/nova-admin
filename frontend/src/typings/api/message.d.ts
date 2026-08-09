declare namespace Api {
    namespace Message {
        type MessageType = 'ANNOUNCEMENT' | 'NOTICE' | 'REMINDER';
        type RecipientType = 'ALL' | 'DEPARTMENT' | 'ROLE' | 'USER';
        type MessageStatus = 'DRAFT' | 'SENT' | 'WITHDRAWN';

        interface Item {
            id: number;
            title: string;
            messageType: MessageType;
            recipientType: RecipientType;
            status: MessageStatus;
            recipientCount: number;
            readCount: number;
            readRate: number;
            createBy: number | null;
            createTime: string;
            sendTime: string | null;
        }

        interface Detail {
            id: number;
            title: string;
            messageType: MessageType;
            status: MessageStatus;
            recipientType: RecipientType;
            recipientRuleJson: string;
            recipientCount: number;
            readCount: number;
            unreadCount: number;
            readRate: number;
            createBy: number | null;
            createTime: string;
            sendTime: string | null;
            contentHtml: string;
            fileIds: number[];
        }

        interface RecipientItem {
            userId: number;
            username: string;
            nickname: string;
            departmentId: number | null;
            departmentName: string | null;
            readStatus: number;
            receiveTime: string | null;
            readTime: string | null;
        }

        interface RecipientSummary {
            total: number;
            read: number;
            unread: number;
            readRate: number;
        }

        interface PageParams extends Api.Common.PageParams {
            title: string | null;
            messageType: MessageType | null;
            status: MessageStatus | null;
            creator: string | null;
            startTime: string | null;
            endTime: string | null;
        }

        interface RecipientPageParams extends Api.Common.PageParams {
            user: string | null;
            departmentId: number | null;
            readStatus: number | null;
        }

        interface RecipientRule {
            recipientType: RecipientType;
            departmentIds: number[];
            includeChildren: boolean;
            roleIds: number[];
            userIds: number[];
        }

        interface CreateReq {
            title: string;
            messageType: MessageType;
            contentHtml: string;
            recipient: RecipientRule;
            fileIds: number[];
        }

        interface UpdateReq extends CreateReq {
        }
    }

    namespace MessageCenter {
        type ReadStatus = 0 | 1;

        interface Item {
            messageId: number;
            title: string;
            messageType: Api.Message.MessageType;
            summary: string;
            readStatus: ReadStatus;
            receiveTime: string | null;
            readTime: string | null;
            sendTime: string | null;
        }

        interface Detail {
            messageId: number;
            title: string;
            messageType: Api.Message.MessageType;
            contentHtml: string;
            sendTime: string | null;
            fileIds: number[];
        }

        interface PageParams extends Api.Common.PageParams {
            readStatus: ReadStatus | null;
        }

        type SseEventType =
            | 'CONNECTED'
            | 'MESSAGE_CREATED'
            | 'MESSAGE_WITHDRAWN'
            | 'UNREAD_COUNT_CHANGED'
            | 'HEARTBEAT';
    }
}
