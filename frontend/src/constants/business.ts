export const statusRecord = {
    1: {
        label: 'common.enabled',
        type: 'success'
    },
    0: {
        label: 'common.disabled',
        type: 'warning'
    }
} as const satisfies Record<number, { label: App.I18n.I18nKey; type: NaiveUI.ThemeColor }>;

export const statusOptions = [
    {
        label: 'common.enabled',
        value: 1
    },
    {
        label: 'common.disabled',
        value: 0
    }
] as const satisfies ReadonlyArray<{ label: App.I18n.I18nKey; value: number }>;

export const genderOptions = [
    {
        label: 'page.user.genderUnknown',
        value: ''
    },
    {
        label: 'page.user.genderMale',
        value: 'MALE'
    },
    {
        label: 'page.user.genderFemale',
        value: 'FEMALE'
    }
] as const satisfies ReadonlyArray<{ label: App.I18n.I18nKey; value: string }>;

export const dataScopeOptions = [
    {
        label: 'page.role.dataScopeOptions.all',
        value: 'ALL'
    },
    {
        label: 'page.role.dataScopeOptions.tenant',
        value: 'TENANT'
    },
    {
        label: 'page.role.dataScopeOptions.department',
        value: 'DEPARTMENT'
    },
    {
        label: 'page.role.dataScopeOptions.departmentAndChildren',
        value: 'DEPARTMENT_AND_CHILDREN'
    },
    {
        label: 'page.role.dataScopeOptions.self',
        value: 'SELF'
    },
    {
        label: 'page.role.dataScopeOptions.custom',
        value: 'CUSTOM'
    }
] as const satisfies ReadonlyArray<{ label: App.I18n.I18nKey; value: Api.Role.DataScopeType }>;

export const menuTypeRecord = {
    DIRECTORY: {
        label: 'page.menu.typeDirectory',
        type: 'info'
    },
    MENU: {
        label: 'page.menu.typeMenu',
        type: 'success'
    },
    BUTTON: {
        label: 'page.menu.typeButton',
        type: 'warning'
    }
} as const satisfies Record<string, { label: App.I18n.I18nKey; type: NaiveUI.ThemeColor }>;

export const configTypeOptions = [
    {label: 'page.config.typeString', value: 'STRING'},
    {label: 'page.config.typeNumber', value: 'NUMBER'},
    {label: 'page.config.typeBoolean', value: 'BOOLEAN'},
    {label: 'page.config.typeJson', value: 'JSON'}
] as const satisfies ReadonlyArray<{ label: App.I18n.I18nKey; value: Api.Config.ConfigType }>;

export const dictTagTypeOptions = [
    {label: 'page.dictionary.tagDefault', value: 'default'},
    {label: 'page.dictionary.tagPrimary', value: 'primary'},
    {label: 'page.dictionary.tagInfo', value: 'info'},
    {label: 'page.dictionary.tagSuccess', value: 'success'},
    {label: 'page.dictionary.tagWarning', value: 'warning'},
    {label: 'page.dictionary.tagError', value: 'error'}
] as const satisfies ReadonlyArray<{ label: App.I18n.I18nKey; value: string }>;

export const messageTypeOptions = [
    {label: 'page.message.typeAnnouncement', value: 'ANNOUNCEMENT'},
    {label: 'page.message.typeNotice', value: 'NOTICE'},
    {label: 'page.message.typeReminder', value: 'REMINDER'}
] as const satisfies ReadonlyArray<{ label: App.I18n.I18nKey; value: Api.Message.MessageType }>;

export const recipientTypeOptions = [
    {label: 'page.message.recipientAll', value: 'ALL'},
    {label: 'page.message.recipientDepartment', value: 'DEPARTMENT'},
    {label: 'page.message.recipientRole', value: 'ROLE'},
    {label: 'page.message.recipientUser', value: 'USER'}
] as const satisfies ReadonlyArray<{ label: App.I18n.I18nKey; value: Api.Message.RecipientType }>;

export const messageStatusRecord = {
    DRAFT: {label: 'page.message.statusDraft', type: 'warning'},
    SENT: {label: 'page.message.statusSent', type: 'success'},
    WITHDRAWN: {label: 'page.message.statusWithdrawn', type: 'error'}
} as const satisfies Record<string, { label: App.I18n.I18nKey; type: NaiveUI.ThemeColor }>;

export const messageStatusOptions = [
    {label: 'page.message.statusDraft', value: 'DRAFT'},
    {label: 'page.message.statusSent', value: 'SENT'},
    {label: 'page.message.statusWithdrawn', value: 'WITHDRAWN'}
] as const satisfies ReadonlyArray<{ label: App.I18n.I18nKey; value: Api.Message.MessageStatus }>;

export const readStatusRecord = {
    0: {label: 'page.message.readUnread', type: 'warning'},
    1: {label: 'page.message.readRead', type: 'success'}
} as const satisfies Record<number, { label: App.I18n.I18nKey; type: NaiveUI.ThemeColor }>;

export const readStatusOptions = [
    {label: 'page.message.readUnread', value: 0},
    {label: 'page.message.readRead', value: 1}
] as const satisfies ReadonlyArray<{ label: App.I18n.I18nKey; value: number }>;
