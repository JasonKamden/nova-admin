declare namespace Api {
    namespace Dictionary {
        interface TypeItem {
            id: number;
            dictName: string;
            dictCode: string;
            builtIn: boolean;
            status: number;
            remark: string | null;
            dataCount: number;
        }

        interface DataItem {
            id: number;
            dictTypeId: number;
            dictLabel: string;
            dictValue: string;
            tagType: string | null;
            sort: number | null;
            status: number;
            remark: string | null;
        }

        interface DataPageParams extends Api.Common.PageParams {
            label: string | null;
            value: string | null;
            status: number | null;
        }

        interface TypeCreateReq {
            dictName: string;
            dictCode: string;
            status: number;
            remark: string | null;
        }

        interface TypeUpdateReq {
            dictName: string;
            status: number;
            remark: string | null;
        }

        interface DataCreateReq {
            dictLabel: string;
            dictValue: string;
            tagType: string | null;
            sort: number | null;
            status: number;
            remark: string | null;
        }

        interface DataUpdateReq extends DataCreateReq {
        }
    }
}
