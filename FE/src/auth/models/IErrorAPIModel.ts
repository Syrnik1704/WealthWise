export interface IErrorAPIModel {
    type: string;
    code?: string;
    name?: string;
    field?: string;
    error?: string;
    msg?: string;
    method?: string;
    headers?: string;
    userAgent?: string;
    requestId?: string;
    statusCode?: number;
    timestamp?: string;
    path?: string;
}