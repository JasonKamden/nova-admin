declare namespace Api {
    namespace Config {
        type ConfigType = 'STRING' | 'NUMBER' | 'BOOLEAN' | 'JSON';

        interface Item {
            id: number;
            configName: string;
            configCode: string;
            configValue: string;
            configType: ConfigType;
            sensitive: boolean;
            builtIn: boolean;
            status: number;
            remark: string | null;
            updateTime: string;
        }

        interface PageParams extends Api.Common.PageParams {
            keyword: string | null;
            configType: ConfigType | null;
            status: number | null;
        }

        interface CreateReq {
            configName: string;
            configCode: string;
            configValue: string;
            configType: ConfigType;
            sensitive: boolean;
            status: number;
            remark: string | null;
        }

        interface UpdateReq {
            configName: string;
            configValue: string;
            configType: ConfigType;
            sensitive: boolean;
            status: number;
            remark: string | null;
        }
    }
}
