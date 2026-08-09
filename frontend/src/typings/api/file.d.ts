declare namespace Api {
    namespace File {
        interface Item {
            id: number;
            originalName: string;
            extension: string | null;
            contentType: string | null;
            fileSize: number;
            sha256: string | null;
            storageType: string | null;
            status: number;
            ownerUserId: number | null;
            createTime: string;
        }

        interface PageParams extends Api.Common.PageParams {
            fileName: string | null;
            contentType: string | null;
            storageType: string | null;
            status: number | null;
        }
    }
}
