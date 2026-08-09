declare namespace Api {
    namespace Dashboard {
        interface TrendPoint {
            date: string;
            count: number;
        }

        interface StatusPoint {
            name: string;
            value: number;
        }

        interface RecentOperation {
            operator: string;
            description: string;
            time: string;
        }

        interface TenantDashboard {
            currentSpace: string;
            department: string;
            userCount: number;
            departmentCount: number;
            roleCount: number;
            onlineUserCount: number;
            loginTrend: TrendPoint[];
            userStatus: StatusPoint[];
            recentOperations: RecentOperation[];
        }

        interface PlatformDashboard {
            tenantCount: number;
            enabledTenantCount: number;
            disabledTenantCount: number;
            platformUserCount: number;
            todayLoginCount: number;
        }
    }
}
