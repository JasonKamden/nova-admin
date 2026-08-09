const local: App.I18n.Schema = {
    system: {
        title: 'Nova Admin System',
        updateTitle: 'System Version Update Notification',
        updateContent: 'A new version of the system has been detected. Do you want to refresh the page immediately?',
        updateConfirm: 'Refresh immediately',
        updateCancel: 'Later'
    },
    common: {
        action: 'Action',
        add: 'Add',
        addSuccess: 'Add Success',
        backToHome: 'Back to home',
        batchDelete: 'Batch Delete',
        cancel: 'Cancel',
        close: 'Close',
        check: 'Check',
        selectAll: 'Select All',
        expandColumn: 'Expand Column',
        columnSetting: 'Column Setting',
        config: 'Config',
        confirm: 'Confirm',
        detail: 'Detail',
        delete: 'Delete',
        deleteSuccess: 'Delete Success',
        disable: 'Disable',
        disabled: 'Disabled',
        enable: 'Enable',
        enabled: 'Enabled',
        confirmDelete: 'Are you sure you want to delete?',
        edit: 'Edit',
        warning: 'Warning',
        error: 'Error',
        index: 'Index',
        keywordSearch: 'Please enter keyword',
        logout: 'Logout',
        logoutConfirm: 'Are you sure you want to log out?',
        lookForward: 'Coming soon',
        modify: 'Modify',
        modifySuccess: 'Modify Success',
        noData: 'No Data',
        operate: 'Operate',
        pleaseCheckValue: 'Please check whether the value is valid',
        refresh: 'Refresh',
        reset: 'Reset',
        search: 'Search',
        switch: 'Switch',
        tip: 'Tip',
        trigger: 'Trigger',
        update: 'Update',
        updateSuccess: 'Update Success',
        userCenter: 'User Center',
        yesOrNo: {
            yes: 'Yes',
            no: 'No'
        }
    },
    request: {
        logout: 'Logout user after request failed',
        logoutMsg: 'User status is invalid, please log in again',
        logoutWithModal: 'Pop up modal after request failed and then log out user',
        logoutWithModalMsg: 'User status is invalid, please log in again',
        refreshToken: 'The requested token has expired, refresh the token',
        tokenExpired: 'The requested token has expired'
    },
    theme: {
        themeDrawerTitle: 'Theme Configuration',
        tabs: {
            appearance: 'Appearance',
            layout: 'Layout',
            general: 'General',
            preset: 'Preset'
        },
        appearance: {
            themeSchema: {
                title: 'Theme Schema',
                light: 'Light',
                dark: 'Dark',
                auto: 'Follow System'
            },
            grayscale: 'Grayscale',
            colourWeakness: 'Colour Weakness',
            themeColor: {
                title: 'Theme Color',
                primary: 'Primary',
                info: 'Info',
                success: 'Success',
                warning: 'Warning',
                error: 'Error',
                followPrimary: 'Follow Primary'
            },
            themeRadius: {
                title: 'Theme Radius'
            },
            recommendColor: 'Apply Recommended Color Algorithm',
            recommendColorDesc: 'The recommended color algorithm refers to',
            preset: {
                title: 'Theme Presets',
                apply: 'Apply',
                applySuccess: 'Preset applied successfully',
                default: {
                    name: 'Default Preset',
                    desc: 'Default theme preset with balanced settings'
                },
                dark: {
                    name: 'Dark Preset',
                    desc: 'Dark theme preset for night time usage'
                },
                compact: {
                    name: 'Compact Preset',
                    desc: 'Compact layout preset for small screens'
                },
                azir: {
                    name: "Azir's Preset",
                    desc: 'It is a cold and elegant preset that Azir likes'
                }
            }
        },
        layout: {
            layoutMode: {
                title: 'Layout Mode',
                vertical: 'Vertical Mode',
                horizontal: 'Horizontal Mode',
                'vertical-mix': 'Vertical Mix Mode',
                'vertical-hybrid-header-first': 'Left Hybrid Header-First',
                'top-hybrid-sidebar-first': 'Top-Hybrid Sidebar-First',
                'top-hybrid-header-first': 'Top-Hybrid Header-First',
                vertical_detail: 'Vertical menu layout, with the menu on the left and content on the right.',
                'vertical-mix_detail':
                    'Vertical mix-menu layout, with the primary menu on the dark left side and the secondary menu on the lighter left side.',
                'vertical-hybrid-header-first_detail':
                    'Left hybrid layout, with the primary menu at the top, the secondary menu on the dark left side, and the tertiary menu on the lighter left side.',
                horizontal_detail: 'Horizontal menu layout, with the menu at the top and content below.',
                'top-hybrid-sidebar-first_detail':
                    'Top hybrid layout, with the primary menu on the left and the secondary menu at the top.',
                'top-hybrid-header-first_detail':
                    'Top hybrid layout, with the primary menu at the top and the secondary menu on the left.'
            },
            tab: {
                title: 'Tab Settings',
                visible: 'Tab Visible',
                cache: 'Tag Bar Info Cache',
                cacheTip: 'Keep the tab bar information after leaving the page',
                height: 'Tab Height',
                mode: {
                    title: 'Tab Mode',
                    slider: 'Slider',
                    chrome: 'Chrome',
                    button: 'Button'
                },
                closeByMiddleClick: 'Close Tab by Middle Click',
                closeByMiddleClickTip: 'Enable closing tabs by clicking with the middle mouse button'
            },
            header: {
                title: 'Header Settings',
                height: 'Header Height',
                breadcrumb: {
                    visible: 'Breadcrumb Visible',
                    showIcon: 'Breadcrumb Icon Visible'
                }
            },
            sider: {
                title: 'Sider Settings',
                inverted: 'Dark Sider',
                width: 'Sider Width',
                collapsedWidth: 'Sider Collapsed Width',
                mixWidth: 'Mix Sider Width',
                mixCollapsedWidth: 'Mix Sider Collapse Width',
                mixChildMenuWidth: 'Mix Child Menu Width',
                autoSelectFirstMenu: 'Auto Select First Submenu',
                autoSelectFirstMenuTip:
                    'When a first-level menu is clicked, the first submenu is automatically selected and navigated to the deepest level'
            },
            footer: {
                title: 'Footer Settings',
                visible: 'Footer Visible',
                fixed: 'Fixed Footer',
                height: 'Footer Height',
                right: 'Right Footer'
            },
            content: {
                title: 'Content Area Settings',
                scrollMode: {
                    title: 'Scroll Mode',
                    tip: 'The theme scroll only scrolls the main part, the outer scroll can carry the header and footer together',
                    wrapper: 'Wrapper',
                    content: 'Content'
                },
                page: {
                    animate: 'Page Animate',
                    mode: {
                        title: 'Page Animate Mode',
                        fade: 'Fade',
                        'fade-slide': 'Slide',
                        'fade-bottom': 'Fade Zoom',
                        'fade-scale': 'Fade Scale',
                        'zoom-fade': 'Zoom Fade',
                        'zoom-out': 'Zoom Out',
                        none: 'None'
                    }
                },
                fixedHeaderAndTab: 'Fixed Header And Tab'
            }
        },
        general: {
            title: 'General Settings',
            watermark: {
                title: 'Watermark Settings',
                visible: 'Watermark Full Screen Visible',
                text: 'Custom Watermark Text',
                enableUserName: 'Enable User Name Watermark',
                enableTime: 'Show Current Time',
                timeFormat: 'Time Format'
            },
            multilingual: {
                title: 'Multilingual Settings',
                visible: 'Display multilingual button'
            },
            globalSearch: {
                title: 'Global Search Settings',
                visible: 'Display GlobalSearch button'
            }
        },
        configOperation: {
            copyConfig: 'Copy Config',
            copySuccessMsg: 'Copy Success, Please replace the variable "themeSettings" in "src/theme/settings.ts"',
            resetConfig: 'Reset Config',
            resetSuccessMsg: 'Reset Success'
        }
    },
    route: {
        login: 'Login',
        403: 'No Permission',
        404: 'Page Not Found',
        500: 'Server Error',
        file: 'File Management',
        'iframe-page': 'Iframe',
        monitor: 'Monitor',
        monitor_cache: 'Cache Management',
        monitor_login: 'Login Monitor',
        monitor_login_log: 'Login Logs',
        monitor_online: 'Online Users',
        monitor_operation: 'Operation Monitor',
        monitor_operation_log: 'Operation Logs',
        platform: 'Platform',
        system: 'System',
        'route-view': 'Business Page',
        platform_tenant: 'Tenant Management',
        system_config: 'Config Management',
        system_department: 'Department Management',
        system_dictionary: 'Dictionary Management',
        system_message: 'Message Management',
        system_user: 'User Management',
        system_role: 'Role Management',
        system_menu: 'Menu Management',
        profile: 'Profile',
        home: 'Home'
    },
    page: {
        login: {
            common: {
                loginOrRegister: 'Login / Register',
                userNamePlaceholder: 'Please enter user name',
                phonePlaceholder: 'Please enter phone number',
                codePlaceholder: 'Please enter verification code',
                passwordPlaceholder: 'Please enter password',
                confirmPasswordPlaceholder: 'Please enter password again',
                codeLogin: 'Verification code login',
                confirm: 'Confirm',
                back: 'Back',
                validateSuccess: 'Verification passed',
                loginSuccess: 'Login successfully',
                welcomeBack: 'Welcome back, {userName} !'
            },
            pwdLogin: {
                title: 'Password Login',
                rememberMe: 'Remember me',
                captchaPlaceholder: 'Please enter captcha',
                refreshCaptcha: 'Click to refresh captcha',
                submit: 'Login',
                forgetPassword: 'Forget password?',
                register: 'Register',
                otherAccountLogin: 'Other Account Login',
                otherLoginMode: 'Other Login Mode',
                superAdmin: 'Super Admin',
                admin: 'Admin',
                user: 'User'
            },
            codeLogin: {
                title: 'Verification Code Login',
                getCode: 'Get verification code',
                reGetCode: 'Reacquire after {time}s',
                sendCodeSuccess: 'Verification code sent successfully',
                imageCodePlaceholder: 'Please enter image verification code'
            },
            register: {
                title: 'Register',
                agreement: 'I have read and agree to',
                protocol: '《User Agreement》',
                policy: '《Privacy Policy》'
            },
            resetPwd: {
                title: 'Reset Password'
            },
            bindWeChat: {
                title: 'Bind WeChat'
            }
        },
        home: {
            branchDesc:
                'For the convenience of everyone in developing and updating the merge, we have streamlined the code of the main branch, only retaining the homepage menu, and the rest of the content has been moved to the example branch for maintenance. The preview address displays the content of the example branch.',
            greeting: 'Good morning, {userName}, today is another day full of vitality!',
            platformDesc: 'You are in platform context. The dashboard shows platform-level tenant and login statistics.',
            tenantDesc: 'You are in the {tenantName} context. The dashboard shows real tenant aggregate data.',
            currentSpace: 'Current Space',
            currentDepartment: 'Current Department',
            userCount: 'Users',
            departmentCount: 'Departments',
            roleCount: 'Roles',
            onlineUserCount: 'Online Users',
            tenantCount: 'Tenants',
            enabledTenantCount: 'Enabled Tenants',
            disabledTenantCount: 'Disabled Tenants',
            platformUserCount: 'Platform Users',
            todayLoginCount: 'Today Logins',
            loginTrend: 'Login Trend',
            userStatus: 'User Status',
            recentOperations: 'Recent Operations',
            operator: 'Operator',
            platformOverview: 'Platform Overview',
            platformSummary: 'Platform Summary',
            weatherDesc: 'Today is cloudy to clear, 20℃ - 25℃!',
            projectCount: 'Project Count',
            todo: 'Todo',
            message: 'Message',
            downloadCount: 'Download Count',
            registerCount: 'Register Count',
            schedule: 'Work and rest Schedule',
            study: 'Study',
            work: 'Work',
            rest: 'Rest',
            entertainment: 'Entertainment',
            visitCount: 'Visit Count',
            turnover: 'Turnover',
            dealCount: 'Deal Count',
            projectNews: {
                title: 'Project News',
                moreNews: 'More News',
                desc1: 'Soybean created the open source project soybean-admin on May 28, 2021!',
                desc2: 'Yanbowe submitted a bug to soybean-admin, the multi-tab bar will not adapt.',
                desc3: 'Soybean is ready to do sufficient preparation for the release of soybean-admin!',
                desc4: 'Soybean is busy writing project documentation for soybean-admin!',
                desc5: 'Soybean just wrote some of the workbench pages casually, and it was enough to see!'
            },
            creativity: 'Creativity'
        },
        profile: {
            basicTitle: 'Basic Profile',
            securityTitle: 'Security',
            currentContext: 'Current Context',
            platformAdmin: 'Platform Admin',
            tenantUser: 'Tenant User',
            uploadAvatar: 'Upload Avatar',
            oldPassword: 'Current Password',
            newPassword: 'New Password',
            confirmPassword: 'Confirm New Password',
            updatePassword: 'Update Password',
            passwordTip: 'After the password is changed successfully, the current session will be invalidated immediately.',
            passwordUpdated: 'Password updated successfully. Please log in again.',
            form: {
                nickname: 'Please enter nickname',
                oldPassword: 'Please enter current password',
                newPassword: 'Please enter new password',
                passwordRule: 'Password length must be between 8 and 64 characters'
            }
        },
        tenant: {
            keyword: 'Keyword',
            tenantCode: 'Tenant Code',
            tenantName: 'Tenant Name',
            contactName: 'Contact Name',
            contactPhone: 'Contact Phone',
            contactEmail: 'Contact Email',
            expireAt: 'Expire At',
            status: 'Status',
            remark: 'Remark',
            createTime: 'Create Time',
            addTitle: 'Add Tenant',
            editTitle: 'Edit Tenant',
            detailTitle: 'Tenant Detail',
            adminUsername: 'Initial Admin Username',
            adminNickname: 'Initial Admin Nickname',
            adminPassword: 'Initial Admin Password',
            statusConfirm: 'Are you sure you want to {action} tenant "{name}"?',
            deleteConfirm: 'Are you sure you want to delete tenant "{name}"?',
            form: {
                tenantCode: 'Please enter tenant code',
                tenantName: 'Please enter tenant name',
                adminUsername: 'Please enter initial admin username',
                adminNickname: 'Please enter initial admin nickname',
                adminPassword: 'Please enter initial admin password',
                adminPasswordRule: 'Password length must be between 8 and 64 characters'
            }
        },
        department: {
            keyword: 'Keyword',
            departmentCode: 'Department Code',
            departmentName: 'Department Name',
            parentDepartment: 'Parent Department',
            leaderUser: 'Leader',
            phone: 'Phone',
            email: 'Email',
            sort: 'Sort',
            status: 'Status',
            addRoot: 'Add Root Department',
            addChild: 'Add Child',
            addTitle: 'Add Department',
            editTitle: 'Edit Department',
            detailTitle: 'Department Detail',
            statusConfirm: 'Are you sure you want to {action} department "{name}"?',
            deleteConfirm: 'Are you sure you want to delete department "{name}"?',
            form: {
                departmentCode: 'Please enter department code',
                departmentName: 'Please enter department name'
            }
        },
        user: {
            username: 'Username',
            nickname: 'Nickname',
            phone: 'Phone',
            email: 'Email',
            department: 'Department',
            role: 'Role',
            gender: 'Gender',
            genderUnknown: 'Unknown',
            genderMale: 'Male',
            genderFemale: 'Female',
            bio: 'Bio',
            status: 'Status',
            lastLoginTime: 'Last Login Time',
            lastLoginIp: 'Last Login IP',
            createTime: 'Create Time',
            initialPassword: 'Initial Password',
            resetPassword: 'Reset Password',
            roleSetting: 'Assign Roles',
            addTitle: 'Add User',
            editTitle: 'Edit User',
            detailTitle: 'User Detail',
            roleTitle: 'Assign User Roles',
            passwordTitle: 'Reset User Password',
            statusConfirm: 'Are you sure you want to {action} user "{name}"?',
            deleteConfirm: 'Are you sure you want to delete user "{name}"?',
            passwordConfirm: 'Are you sure you want to reset password for user "{name}"?',
            form: {
                username: 'Please enter username',
                nickname: 'Please enter nickname',
                initialPassword: 'Please enter initial password',
                newPassword: 'Please enter new password'
            }
        },
        role: {
            keyword: 'Keyword',
            roleCode: 'Role Code',
            roleName: 'Role Name',
            dataScope: 'Data Scope',
            builtIn: 'Built-in',
            sort: 'Sort',
            status: 'Status',
            remark: 'Remark',
            createTime: 'Create Time',
            customDepartments: 'Custom Departments',
            menuSetting: 'Menu Grant',
            addTitle: 'Add Role',
            editTitle: 'Edit Role',
            detailTitle: 'Role Detail',
            menuTitle: 'Role Menu Grant',
            statusConfirm: 'Are you sure you want to {action} role "{name}"?',
            deleteConfirm: 'Are you sure you want to delete role "{name}"?',
            form: {
                roleCode: 'Please enter role code',
                roleName: 'Please enter role name'
            },
            dataScopeOptions: {
                all: 'All Data',
                tenant: 'All Tenant Data',
                department: 'Current Department',
                departmentAndChildren: 'Department And Children',
                self: 'Self Only',
                custom: 'Custom Departments'
            }
        },
        menu: {
            keyword: 'Keyword',
            menuName: 'Menu Name',
            parentMenu: 'Parent Menu',
            menuType: 'Menu Type',
            routeName: 'Route Name',
            routePath: 'Route Path',
            componentPath: 'Component Path',
            permissionCode: 'Permission Code',
            icon: 'Icon',
            i18nKey: 'I18n Key',
            sort: 'Sort',
            status: 'Status',
            visible: 'Visible',
            keepAlive: 'Keep Alive',
            externalLink: 'External Link',
            addTitle: 'Add Menu',
            editTitle: 'Edit Menu',
            detailTitle: 'Menu Detail',
            statusConfirm: 'Are you sure you want to {action} menu "{name}"?',
            deleteConfirm: 'Are you sure you want to delete menu "{name}"?',
            platformOnlyTip: 'Menu maintenance is read-only in tenant context. Switch to PLATFORM to manage menus.',
            form: {
                menuType: 'Please select menu type',
                menuName: 'Please enter menu name'
            },
            typeDirectory: 'Directory',
            typeMenu: 'Menu',
            typeButton: 'Button'
        },
        dictionary: {
            typeName: 'Dictionary Name',
            typeCode: 'Dictionary Code',
            dataCount: 'Data Count',
            dataLabel: 'Label',
            dataValue: 'Value',
            tagType: 'Tag Type',
            sort: 'Sort',
            status: 'Status',
            remark: 'Remark',
            typeTitle: 'Dictionary Types',
            dataTitle: 'Dictionary Data',
            addTypeTitle: 'Add Dictionary Type',
            editTypeTitle: 'Edit Dictionary Type',
            addDataTitle: 'Add Dictionary Data',
            editDataTitle: 'Edit Dictionary Data',
            deleteTypeConfirm: 'Are you sure you want to delete dictionary type "{name}"?',
            deleteDataConfirm: 'Are you sure you want to delete dictionary data "{name}"?',
            form: {
                dictName: 'Please enter dictionary name',
                dictCode: 'Please enter dictionary code',
                dictLabel: 'Please enter dictionary label',
                dictValue: 'Please enter dictionary value'
            },
            tagDefault: 'Default',
            tagPrimary: 'Primary',
            tagInfo: 'Info',
            tagSuccess: 'Success',
            tagWarning: 'Warning',
            tagError: 'Error'
        },
        config: {
            keyword: 'Keyword',
            configName: 'Config Name',
            configCode: 'Config Code',
            configValue: 'Config Value',
            configType: 'Config Type',
            sensitive: 'Sensitive',
            builtIn: 'Built-in',
            status: 'Status',
            remark: 'Remark',
            updateTime: 'Update Time',
            addTitle: 'Add Config',
            editTitle: 'Edit Config',
            detailTitle: 'Config Detail',
            deleteConfirm: 'Are you sure you want to delete config "{name}"?',
            form: {
                configName: 'Please enter config name',
                configCode: 'Please enter config code',
                configValue: 'Please enter config value'
            },
            typeString: 'String',
            typeNumber: 'Number',
            typeBoolean: 'Boolean',
            typeJson: 'JSON'
        },
        message: {
            title: 'Title',
            messageType: 'Message Type',
            recipientType: 'Recipient Type',
            status: 'Status',
            creator: 'Creator',
            recipientCount: 'Recipients',
            readCount: 'Read Count',
            unreadCount: 'Unread Count',
            readRate: 'Read Rate',
            createTime: 'Create Time',
            sendTime: 'Send Time',
            contentHtml: 'Content',
            recipientRule: 'Recipient Rule',
            includeChildren: 'Include Child Departments',
            departments: 'Departments',
            roles: 'Roles',
            users: 'Users',
            previewRecipients: 'Preview',
            send: 'Send',
            withdraw: 'Withdraw',
            recipientDetail: 'Recipients',
            addTitle: 'Add Message Draft',
            editTitle: 'Edit Message Draft',
            detailTitle: 'Message Detail',
            recipientTitle: 'Recipient Detail',
            deleteConfirm: 'Are you sure you want to delete message "{name}"?',
            sendConfirm: 'Are you sure you want to send message "{name}"?',
            withdrawConfirm: 'Are you sure you want to withdraw message "{name}"?',
            form: {
                title: 'Please enter message title',
                contentHtml: 'Please enter message content'
            },
            typeAnnouncement: 'Announcement',
            typeNotice: 'Notice',
            typeReminder: 'Reminder',
            recipientAll: 'All Users',
            recipientDepartment: 'By Department',
            recipientRole: 'By Role',
            recipientUser: 'By User',
            statusDraft: 'Draft',
            statusSent: 'Sent',
            statusWithdrawn: 'Withdrawn',
            readUnread: 'Unread',
            readRead: 'Read'
        },
        monitor: {
            keyword: 'Keyword',
            username: 'Username',
            ip: 'IP',
            status: 'Status',
            loginType: 'Login Type',
            loginTime: 'Login Time',
            failureReason: 'Failure Reason',
            module: 'Module',
            operationType: 'Operation Type',
            operator: 'Operator',
            requestMethod: 'Request Method',
            requestUri: 'Request URI',
            requestIp: 'Request IP',
            durationMs: 'Duration(ms)',
            operationTime: 'Operation Time',
            detailTitle: 'Log Detail',
            sessionId: 'Session ID',
            tenantName: 'Tenant',
            departmentName: 'Department',
            userAgent: 'User-Agent',
            lastActivityTime: 'Last Activity Time',
            kickConfirm: 'Are you sure you want to kick user "{name}"?',
            cacheCode: 'Cache Code',
            cacheName: 'Cache Name',
            cacheType: 'Cache Type',
            cacheModule: 'Module',
            cacheScope: 'Scope',
            cacheStatus: 'Cache Status',
            defaultTtlSeconds: 'TTL(s)',
            clearable: 'Clearable',
            refreshable: 'Refreshable',
            description: 'Description',
            redisStatus: 'Redis Status',
            clear: 'Clear',
            refreshCache: 'Refresh Cache',
            clearConfirm: 'Are you sure you want to clear cache "{name}"?',
            refreshConfirm: 'Are you sure you want to refresh cache "{name}"?'
        },
        file: {
            fileName: 'File Name',
            contentType: 'Content Type',
            storageType: 'Storage Type',
            status: 'Status',
            fileSize: 'File Size',
            sha256: 'SHA-256',
            ownerUserId: 'Owner User',
            createTime: 'Create Time',
            upload: 'Upload File',
            preview: 'Preview',
            download: 'Download',
            deleteConfirm: 'Are you sure you want to delete file "{name}"?',
            batchDeleteConfirm: 'Are you sure you want to delete selected files?'
        }
    },
    form: {
        required: 'Cannot be empty',
        userName: {
            required: 'Please enter user name',
            invalid: 'User name format is incorrect'
        },
        phone: {
            required: 'Please enter phone number',
            invalid: 'Phone number format is incorrect'
        },
        pwd: {
            required: 'Please enter password',
            invalid: '6-32 characters and must not contain whitespace'
        },
        confirmPwd: {
            required: 'Please enter password again',
            invalid: 'The two passwords are inconsistent'
        },
        code: {
            required: 'Please enter verification code',
            invalid: 'Verification code format is incorrect'
        },
        email: {
            required: 'Please enter email',
            invalid: 'Email format is incorrect'
        }
    },
    dropdown: {
        closeCurrent: 'Close Current',
        closeOther: 'Close Other',
        closeLeft: 'Close Left',
        closeRight: 'Close Right',
        closeAll: 'Close All',
        pin: 'Pin Tab',
        unpin: 'Unpin Tab'
    },
    icon: {
        themeConfig: 'Theme Configuration',
        themeSchema: 'Theme Schema',
        lang: 'Switch Language',
        fullscreen: 'Fullscreen',
        fullscreenExit: 'Exit Fullscreen',
        reload: 'Reload Page',
        collapse: 'Collapse Menu',
        expand: 'Expand Menu',
        pin: 'Pin',
        unpin: 'Unpin'
    },
    datatable: {
        itemCount: 'Total {total} items',
        fixed: {
            left: 'Left Fixed',
            right: 'Right Fixed',
            unFixed: 'Unfixed'
        }
    }
};

export default local;
