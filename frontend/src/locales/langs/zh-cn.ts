const local: App.I18n.Schema = {
    system: {
        title: 'Nova 管理系统',
        updateTitle: '系统版本更新通知',
        updateContent: '检测到系统有新版本发布，是否立即刷新页面？',
        updateConfirm: '立即刷新',
        updateCancel: '稍后再说'
    },
    common: {
        action: '操作',
        add: '新增',
        addSuccess: '添加成功',
        backToHome: '返回首页',
        batchDelete: '批量删除',
        cancel: '取消',
        close: '关闭',
        check: '勾选',
        selectAll: '全选',
        expandColumn: '展开列',
        columnSetting: '列设置',
        config: '配置',
        confirm: '确认',
        export: '导出',
        import: '导入',
        detail: '详情',
        delete: '删除',
        deleteSuccess: '删除成功',
        disable: '停用',
        disabled: '禁用',
        enable: '启用',
        enabled: '启用',
        confirmDelete: '确认删除吗？',
        edit: '编辑',
        warning: '警告',
        error: '错误',
        index: '序号',
        keywordSearch: '请输入关键词搜索',
        logout: '退出登录',
        logoutConfirm: '确认退出登录吗？',
        lookForward: '敬请期待',
        modify: '修改',
        modifySuccess: '修改成功',
        more: '更多',
        noData: '无数据',
        operate: '操作',
        pleaseCheckValue: '请检查输入的值是否合法',
        refresh: '刷新',
        reset: '重置',
        search: '搜索',
        switch: '切换',
        tip: '提示',
        trigger: '触发',
        update: '更新',
        updateSuccess: '更新成功',
        userCenter: '个人中心',
        viewAll: '查看全部',
        readAll: '全部已读',
        yesOrNo: {
            yes: '是',
            no: '否'
        }
    },
    request: {
        logout: '请求失败后登出用户',
        logoutMsg: '用户状态失效，请重新登录',
        logoutWithModal: '请求失败后弹出模态框再登出用户',
        logoutWithModalMsg: '用户状态失效，请重新登录',
        refreshToken: '请求的token已过期，刷新token',
        tokenExpired: 'token已过期'
    },
    theme: {
        themeDrawerTitle: '主题配置',
        tabs: {
            appearance: '外观',
            layout: '布局',
            general: '通用',
            preset: '预设'
        },
        appearance: {
            themeSchema: {
                title: '主题模式',
                light: '亮色模式',
                dark: '暗黑模式',
                auto: '跟随系统'
            },
            grayscale: '灰色模式',
            colourWeakness: '色弱模式',
            themeColor: {
                title: '主题颜色',
                primary: '主色',
                info: '信息色',
                success: '成功色',
                warning: '警告色',
                error: '错误色',
                followPrimary: '跟随主色'
            },
            themeRadius: {
                title: '主题圆角'
            },
            recommendColor: '应用推荐算法的颜色',
            recommendColorDesc: '推荐颜色的算法参照',
            preset: {
                title: '主题预设',
                apply: '应用',
                applySuccess: '预设应用成功',
                default: {
                    name: '默认预设',
                    desc: 'Soybean 默认主题预设'
                },
                dark: {
                    name: '暗色预设',
                    desc: '适用于夜间使用的暗色主题预设'
                },
                compact: {
                    name: '紧凑型',
                    desc: '适用于小屏幕的紧凑布局预设'
                },
                azir: {
                    name: 'Azir的预设',
                    desc: '是 Azir 比较喜欢的莫兰迪色系冷淡风'
                }
            }
        },
        layout: {
            layoutMode: {
                title: '布局模式',
                vertical: '左侧菜单模式',
                'vertical-mix': '左侧菜单混合模式',
                'vertical-hybrid-header-first': '左侧混合-顶部优先',
                horizontal: '顶部菜单模式',
                'top-hybrid-sidebar-first': '顶部混合-侧边优先',
                'top-hybrid-header-first': '顶部混合-顶部优先',
                vertical_detail: '左侧菜单布局，菜单在左，内容在右。',
                'vertical-mix_detail': '左侧双菜单布局，一级菜单在左侧深色区域，二级菜单在左侧浅色区域。',
                'vertical-hybrid-header-first_detail':
                    '左侧混合布局，一级菜单在顶部，二级菜单在左侧深色区域，三级菜单在左侧浅色区域。',
                horizontal_detail: '顶部菜单布局，菜单在顶部，内容在下方。',
                'top-hybrid-sidebar-first_detail': '顶部混合布局，一级菜单在左侧，二级菜单在顶部。',
                'top-hybrid-header-first_detail': '顶部混合布局，一级菜单在顶部，二级菜单在左侧。'
            },
            tab: {
                title: '标签栏设置',
                visible: '显示标签栏',
                cache: '标签栏信息缓存',
                cacheTip: '离开页面后仍然保留标签栏信息',
                height: '标签栏高度',
                mode: {
                    title: '标签栏风格',
                    slider: '滑块风格',
                    chrome: '谷歌风格',
                    button: '按钮风格'
                },
                closeByMiddleClick: '鼠标中键关闭标签页',
                closeByMiddleClickTip: '启用后可以使用鼠标中键点击标签页进行关闭'
            },
            header: {
                title: '头部设置',
                height: '头部高度',
                breadcrumb: {
                    visible: '显示面包屑',
                    showIcon: '显示面包屑图标'
                }
            },
            sider: {
                title: '侧边栏设置',
                inverted: '深色侧边栏',
                width: '侧边栏宽度',
                collapsedWidth: '侧边栏折叠宽度',
                mixWidth: '混合布局侧边栏宽度',
                mixCollapsedWidth: '混合布局侧边栏折叠宽度',
                mixChildMenuWidth: '混合布局子菜单宽度',
                autoSelectFirstMenu: '自动选择第一个子菜单',
                autoSelectFirstMenuTip: '点击一级菜单时，自动选择并导航到第一个子菜单的最深层级'
            },
            footer: {
                title: '底部设置',
                visible: '显示底部',
                fixed: '固定底部',
                height: '底部高度',
                right: '底部居右'
            },
            content: {
                title: '内容区域设置',
                scrollMode: {
                    title: '滚动模式',
                    tip: '主题滚动仅 main 部分滚动，外层滚动可携带头部底部一起滚动',
                    wrapper: '外层滚动',
                    content: '主体滚动'
                },
                page: {
                    animate: '页面切换动画',
                    mode: {
                        title: '页面切换动画类型',
                        'fade-slide': '滑动',
                        fade: '淡入淡出',
                        'fade-bottom': '底部消退',
                        'fade-scale': '缩放消退',
                        'zoom-fade': '渐变',
                        'zoom-out': '闪现',
                        none: '无'
                    }
                },
                fixedHeaderAndTab: '固定头部和标签栏'
            }
        },
        general: {
            title: '通用设置',
            businessFormMode: {
                title: '业务表单打开方式',
                drawer: '侧边弹窗',
                modal: '直接弹窗'
            },
            watermark: {
                title: '水印设置',
                visible: '显示全屏水印',
                text: '自定义水印文本',
                enableUserName: '启用用户名水印',
                enableTime: '显示当前时间',
                timeFormat: '时间格式'
            },
            multilingual: {
                title: '多语言设置',
                visible: '显示多语言按钮'
            },
            globalSearch: {
                title: '全局搜索设置',
                visible: '显示全局搜索按钮'
            }
        },
        configOperation: {
            copyConfig: '复制配置',
            copySuccessMsg: '复制成功，请替换 src/theme/settings.ts 中的变量 themeSettings',
            resetConfig: '重置配置',
            resetSuccessMsg: '重置成功'
        }
    },
    route: {
        login: '登录',
        403: '无权限',
        404: '页面不存在',
        500: '服务器错误',
        file: '文件管理',
        'iframe-page': '外链页面',
        monitor: '系统监控',
        monitor_cache: '缓存管理',
        monitor_login: '登录监控',
        monitor_login_log: '登录日志',
        monitor_online: '在线用户',
        monitor_operation: '操作监控',
        monitor_operation_log: '操作日志',
        message: '消息',
        message_center: '消息中心',
        platform: '平台管理',
        system: '系统管理',
        'route-view': '业务页面',
        platform_tenant: 'Tenant 管理',
        system_config: '参数管理',
        system_department: 'Department 管理',
        system_dictionary: '字典管理',
        system_message: '消息管理',
        system_user: '用户管理',
        system_role: '角色管理',
        system_menu: '菜单管理',
        profile: '个人中心',
        home: '首页'
    },
    page: {
        login: {
            common: {
                loginOrRegister: '登录 / 注册',
                userNamePlaceholder: '请输入用户名',
                phonePlaceholder: '请输入手机号',
                codePlaceholder: '请输入验证码',
                passwordPlaceholder: '请输入密码',
                confirmPasswordPlaceholder: '请再次输入密码',
                codeLogin: '验证码登录',
                confirm: '确定',
                back: '返回',
                validateSuccess: '验证成功',
                loginSuccess: '登录成功',
                welcomeBack: '欢迎回来，{userName} ！'
            },
            pwdLogin: {
                title: '密码登录',
                rememberMe: '记住我',
                captchaPlaceholder: '请输入图形验证码',
                refreshCaptcha: '点击刷新验证码',
                submit: '登录',
                forgetPassword: '忘记密码？',
                register: '注册账号',
                otherAccountLogin: '其他账号登录',
                otherLoginMode: '其他登录方式',
                superAdmin: '超级管理员',
                admin: '管理员',
                user: '普通用户'
            },
            codeLogin: {
                title: '验证码登录',
                getCode: '获取验证码',
                reGetCode: '{time}秒后重新获取',
                sendCodeSuccess: '验证码发送成功',
                imageCodePlaceholder: '请输入图片验证码'
            },
            register: {
                title: '注册账号',
                agreement: '我已经仔细阅读并接受',
                protocol: '《用户协议》',
                policy: '《隐私权政策》'
            },
            resetPwd: {
                title: '重置密码'
            },
            bindWeChat: {
                title: '绑定微信'
            }
        },
        home: {
            branchDesc:
                '为了方便大家开发和更新合并，我们对main分支的代码进行了精简，只保留了首页菜单，其余内容已移至example分支进行维护。预览地址显示的内容即为example分支的内容。',
            greeting: '早安，{userName}, 今天又是充满活力的一天!',
            platformDesc: '当前为平台上下文，首页展示平台级租户与登录统计。',
            tenantDesc: '当前为 {tenantName} 上下文，首页展示真实 Tenant 聚合数据。',
            currentSpace: '当前空间',
            currentDepartment: '当前 Department',
            userCount: '用户数量',
            departmentCount: 'Department 数量',
            roleCount: '角色数量',
            onlineUserCount: '在线用户',
            tenantCount: 'Tenant 总数',
            enabledTenantCount: '启用 Tenant',
            disabledTenantCount: '停用 Tenant',
            platformUserCount: '平台用户',
            todayLoginCount: '今日登录',
            loginTrend: '登录趋势',
            userStatus: '用户状态分布',
            recentOperations: '最近操作',
            operator: '操作人',
            platformOverview: '平台概览',
            platformSummary: '平台汇总',
            weatherDesc: '今日多云转晴，20℃ - 25℃!',
            projectCount: '项目数',
            todo: '待办',
            message: '消息',
            downloadCount: '下载量',
            registerCount: '注册量',
            schedule: '作息安排',
            study: '学习',
            work: '工作',
            rest: '休息',
            entertainment: '娱乐',
            visitCount: '访问量',
            turnover: '成交额',
            dealCount: '成交量',
            projectNews: {
                title: '项目动态',
                moreNews: '更多动态',
                desc1: 'Soybean 在2021年5月28日创建了开源项目 soybean-admin!',
                desc2: 'Yanbowe 向 soybean-admin 提交了一个bug，多标签栏不会自适应。',
                desc3: 'Soybean 准备为 soybean-admin 的发布做充分的准备工作!',
                desc4: 'Soybean 正在忙于为soybean-admin写项目说明文档！',
                desc5: 'Soybean 刚才把工作台页面随便写了一些，凑合能看了！'
            },
            creativity: '创意'
        },
        profile: {
            basicTitle: '基本资料',
            securityTitle: '账号安全',
            accountTitle: '账号信息',
            identityTitle: '身份概览',
            currentContext: '当前 Context',
            currentTenant: '当前 Tenant',
            currentDepartment: '当前 Department',
            userId: '用户 ID',
            platformAdmin: '平台管理员',
            tenantUser: 'Tenant 用户',
            uploadAvatar: '上传头像',
            oldPassword: '当前密码',
            newPassword: '新密码',
            confirmPassword: '确认新密码',
            updatePassword: '更新密码',
            passwordTip: '修改密码成功后，当前会话会立即失效并返回登录页。',
            passwordUpdated: '密码修改成功，请重新登录。',
            form: {
                nickname: '请输入用户昵称',
                oldPassword: '请输入当前密码',
                newPassword: '请输入新密码',
                passwordRule: '密码长度需为 8-64 位'
            }
        },
        messageCenter: {
            title: '消息中心',
            subtitle: '处理当前用户在当前 Context 下收到的消息',
            all: '全部',
            unread: '未读',
            read: '已读',
            empty: '暂无消息',
            openDetail: '查看详情',
            refreshList: '刷新消息',
            readAllTip: '将当前 Context 下消息全部标记为已读',
            latest: '最近 10 条'
        },
        tenant: {
            keyword: '关键词',
            tenantCode: 'Tenant 编码',
            tenantName: 'Tenant 名称',
            contactName: '联系人',
            contactPhone: '联系电话',
            contactEmail: '联系邮箱',
            expireAt: '到期日期',
            status: '状态',
            remark: '备注',
            createTime: '创建时间',
            addTitle: '新增 Tenant',
            editTitle: '编辑 Tenant',
            detailTitle: 'Tenant 详情',
            adminUsername: '初始管理员账号',
            adminNickname: '初始管理员昵称',
            adminPassword: '初始管理员密码',
            statusConfirm: '确认{action} Tenant“{name}”吗？',
            deleteConfirm: '确认删除 Tenant“{name}”吗？',
            form: {
                tenantCode: '请输入 Tenant 编码',
                tenantName: '请输入 Tenant 名称',
                adminUsername: '请输入初始管理员账号',
                adminNickname: '请输入初始管理员昵称',
                adminPassword: '请输入初始管理员密码',
                adminPasswordRule: '密码长度需为 8-64 位'
            }
        },
        department: {
            keyword: '关键词',
            departmentCode: '部门编码',
            departmentName: '部门名称',
            parentDepartment: '上级部门',
            leaderUser: '负责人',
            phone: '联系电话',
            email: '邮箱',
            sort: '排序',
            status: '状态',
            addRoot: '新增根部门',
            addChild: '新增子级',
            addTitle: '新增部门',
            editTitle: '编辑部门',
            detailTitle: '部门详情',
            statusConfirm: '确认{action}部门“{name}”吗？',
            deleteConfirm: '确认删除部门“{name}”吗？',
            form: {
                departmentCode: '请输入部门编码',
                departmentName: '请输入部门名称'
            }
        },
        user: {
            username: '登录账号',
            nickname: '用户昵称',
            phone: '手机号',
            email: '邮箱',
            department: '所属 Department',
            role: '角色',
            gender: '性别',
            genderUnknown: '未设置',
            genderMale: '男',
            genderFemale: '女',
            bio: '个人简介',
            status: '状态',
            lastLoginTime: '最近登录时间',
            lastLoginIp: '最近登录 IP',
            createTime: '创建时间',
            initialPassword: '初始密码',
            resetPassword: '重置密码',
            roleSetting: '分配角色',
            addTitle: '新增用户',
            editTitle: '编辑用户',
            detailTitle: '用户详情',
            roleTitle: '分配用户角色',
            passwordTitle: '重置用户密码',
            statusConfirm: '确认{action}用户“{name}”吗？',
            deleteConfirm: '确认删除用户“{name}”吗？',
            passwordConfirm: '确认将用户“{name}”的密码重置为新密码吗？',
            form: {
                username: '请输入登录账号',
                nickname: '请输入用户昵称',
                initialPassword: '请输入初始密码',
                newPassword: '请输入新密码'
            }
        },
        role: {
            keyword: '关键词',
            roleCode: '角色编码',
            roleName: '角色名称',
            dataScope: '数据范围',
            builtIn: '内置角色',
            sort: '排序',
            status: '状态',
            remark: '备注',
            createTime: '创建时间',
            customDepartments: '自定义 Department',
            menuSetting: '菜单授权',
            addTitle: '新增角色',
            editTitle: '编辑角色',
            detailTitle: '角色详情',
            menuTitle: '角色菜单授权',
            statusConfirm: '确认{action}角色“{name}”吗？',
            deleteConfirm: '确认删除角色“{name}”吗？',
            form: {
                roleCode: '请输入角色编码',
                roleName: '请输入角色名称'
            },
            dataScopeOptions: {
                all: '全部数据',
                tenant: '当前 Tenant 全部数据',
                department: '本 Department 数据',
                departmentAndChildren: '本 Department 及子级',
                self: '仅本人数据',
                custom: '自定义 Department'
            }
        },
        menu: {
            keyword: '关键词',
            menuName: '菜单名称',
            parentMenu: '上级菜单',
            menuType: '菜单类型',
            routeName: '路由名称',
            routePath: '路由路径',
            componentPath: '组件路径',
            permissionCode: '权限编码',
            icon: '图标',
            i18nKey: '国际化 Key',
            sort: '排序',
            status: '状态',
            visible: '显示',
            keepAlive: '缓存',
            externalLink: '外链',
            addTitle: '新增菜单',
            editTitle: '编辑菜单',
            detailTitle: '菜单详情',
            statusConfirm: '确认{action}菜单“{name}”吗？',
            deleteConfirm: '确认删除菜单“{name}”吗？',
            platformOnlyTip: 'Tenant 上下文只读，切换到 PLATFORM 后可维护菜单',
            form: {
                menuType: '请选择菜单类型',
                menuName: '请输入菜单名称'
            },
            typeDirectory: '目录',
            typeMenu: '菜单',
            typeButton: '按钮'
        },
        dictionary: {
            typeName: '字典名称',
            typeCode: '字典编码',
            dataCount: '数据量',
            dataLabel: '字典标签',
            dataValue: '字典值',
            tagType: '标签样式',
            sort: '排序',
            status: '状态',
            remark: '备注',
            typeTitle: '字典类型',
            dataTitle: '字典数据',
            addTypeTitle: '新增字典类型',
            editTypeTitle: '编辑字典类型',
            addDataTitle: '新增字典数据',
            editDataTitle: '编辑字典数据',
            deleteTypeConfirm: '确认删除字典类型“{name}”吗？',
            deleteDataConfirm: '确认删除字典数据“{name}”吗？',
            form: {
                dictName: '请输入字典名称',
                dictCode: '请输入字典编码',
                dictLabel: '请输入字典标签',
                dictValue: '请输入字典值'
            },
            tagDefault: '默认',
            tagPrimary: '主要',
            tagInfo: '信息',
            tagSuccess: '成功',
            tagWarning: '警告',
            tagError: '危险'
        },
        config: {
            keyword: '关键词',
            configName: '参数名称',
            configCode: '参数编码',
            configValue: '参数值',
            configType: '参数类型',
            sensitive: '敏感参数',
            builtIn: '内置参数',
            status: '状态',
            remark: '备注',
            updateTime: '更新时间',
            addTitle: '新增参数',
            editTitle: '编辑参数',
            detailTitle: '参数详情',
            deleteConfirm: '确认删除参数“{name}”吗？',
            form: {
                configName: '请输入参数名称',
                configCode: '请输入参数编码',
                configValue: '请输入参数值'
            },
            typeString: '字符串',
            typeNumber: '数值',
            typeBoolean: '布尔值',
            typeJson: 'JSON'
        },
        message: {
            title: '消息标题',
            messageType: '消息类型',
            recipientType: '接收范围',
            status: '消息状态',
            creator: '创建人',
            recipientCount: '接收人数',
            readCount: '已读人数',
            unreadCount: '未读人数',
            readRate: '已读率',
            createTime: '创建时间',
            sendTime: '发送时间',
            contentHtml: '消息正文',
            recipientRule: '接收规则',
            includeChildren: '包含下级 Department',
            departments: 'Department',
            roles: '角色',
            users: '用户',
            previewRecipients: '预览接收',
            send: '发送',
            withdraw: '撤回',
            recipientDetail: '接收明细',
            addTitle: '新增消息草稿',
            editTitle: '编辑消息草稿',
            detailTitle: '消息详情',
            recipientTitle: '消息接收明细',
            deleteConfirm: '确认删除消息“{name}”吗？',
            sendConfirm: '确认发送消息“{name}”吗？',
            withdrawConfirm: '确认撤回消息“{name}”吗？',
            form: {
                title: '请输入消息标题',
                contentHtml: '请输入消息正文'
            },
            typeAnnouncement: '公告',
            typeNotice: '通知',
            typeReminder: '提醒',
            recipientAll: '全部用户',
            recipientDepartment: '按 Department',
            recipientRole: '按角色',
            recipientUser: '按用户',
            statusDraft: '草稿',
            statusSent: '已发送',
            statusWithdrawn: '已撤回',
            readUnread: '未读',
            readRead: '已读'
        },
        monitor: {
            keyword: '关键词',
            username: '用户名',
            ip: 'IP',
            status: '状态',
            loginType: '登录类型',
            loginTime: '登录时间',
            failureReason: '失败原因',
            module: '模块',
            operationType: '操作类型',
            operator: '操作人',
            requestMethod: '请求方法',
            requestUri: '请求地址',
            requestIp: '请求 IP',
            durationMs: '耗时(ms)',
            operationTime: '操作时间',
            detailTitle: '日志详情',
            sessionId: '会话标识',
            tenantName: 'Tenant',
            departmentName: 'Department',
            userAgent: 'User-Agent',
            lastActivityTime: '最近活动时间',
            kickConfirm: '确认强制下线用户“{name}”吗？',
            cacheCode: '缓存编码',
            cacheName: '缓存名称',
            cacheType: '缓存类型',
            cacheModule: '所属模块',
            cacheScope: '作用域',
            cacheStatus: '缓存状态',
            defaultTtlSeconds: 'TTL(秒)',
            clearable: '可清理',
            refreshable: '可刷新',
            description: '说明',
            redisStatus: 'Redis 状态',
            clear: '清理',
            refreshCache: '刷新缓存',
            clearConfirm: '确认清理缓存“{name}”吗？',
            refreshConfirm: '确认刷新缓存“{name}”吗？'
        },
        file: {
            fileName: '文件名',
            contentType: '类型',
            storageType: '存储类型',
            status: '状态',
            fileSize: '大小',
            sha256: 'SHA-256',
            ownerUserId: '所属用户',
            createTime: '创建时间',
            upload: '上传文件',
            preview: '预览',
            download: '下载',
            deleteConfirm: '确认删除文件“{name}”吗？',
            batchDeleteConfirm: '确认批量删除已选文件吗？'
        }
    },
    form: {
        required: '不能为空',
        userName: {
            required: '请输入用户名',
            invalid: '用户名格式不正确'
        },
        phone: {
            required: '请输入手机号',
            invalid: '手机号格式不正确'
        },
        pwd: {
            required: '请输入密码',
            invalid: '密码格式不正确，需为 6-32 位且不能包含空白字符'
        },
        confirmPwd: {
            required: '请输入确认密码',
            invalid: '两次输入密码不一致'
        },
        code: {
            required: '请输入验证码',
            invalid: '验证码格式不正确'
        },
        email: {
            required: '请输入邮箱',
            invalid: '邮箱格式不正确'
        }
    },
    dropdown: {
        closeCurrent: '关闭',
        closeOther: '关闭其它',
        closeLeft: '关闭左侧',
        closeRight: '关闭右侧',
        closeAll: '关闭所有',
        pin: '固定标签',
        unpin: '取消固定'
    },
    icon: {
        themeConfig: '主题配置',
        themeSchema: '主题模式',
        lang: '切换语言',
        fullscreen: '全屏',
        fullscreenExit: '退出全屏',
        reload: '刷新页面',
        collapse: '折叠菜单',
        expand: '展开菜单',
        pin: '固定',
        unpin: '取消固定'
    },
    datatable: {
        itemCount: '共 {total} 条',
        fixed: {
            left: '左固定',
            right: '右固定',
            unFixed: '取消固定'
        }
    }
};

export default local;
