declare namespace Api {
    namespace Department {
        interface Item {
            id: number;
            parentId: number | null;
            departmentCode: string;
            departmentName: string;
            leaderUserId: number | null;
            leaderName: string | null;
            phone: string | null;
            email: string | null;
            sort: number | null;
            status: number;
            children: Item[];
        }

        interface QueryParams {
            keyword: string | null;
            status: number | null;
        }

        interface CreateReq {
            parentId: number | null;
            departmentCode: string;
            departmentName: string;
            leaderUserId: number | null;
            phone: string | null;
            email: string | null;
            sort: number | null;
            status: number;
        }

        interface UpdateReq {
            parentId: number | null;
            departmentName: string;
            leaderUserId: number | null;
            phone: string | null;
            email: string | null;
            sort: number | null;
            status: number;
        }

        interface TreeOption {
            label: string;
            key: number;
            value: number;
            disabled?: boolean;
            children?: TreeOption[];
        }
    }
}
