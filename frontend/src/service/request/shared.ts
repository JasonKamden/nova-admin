import {localStg} from '@/utils/storage';
import {normalizeBusinessText} from '@/utils/context';

export function getAuthorization() {
    const token = localStg.get('token');
    const Authorization = token || null;

    return Authorization;
}

export function showErrorMsg(state: { errMsgStack: string[] }, message: string) {
    const normalizedMessage = normalizeBusinessText(message);

    if (!state.errMsgStack?.length) {
        state.errMsgStack = [];
    }

    const isExist = state.errMsgStack.includes(normalizedMessage);

    if (!isExist) {
        state.errMsgStack.push(normalizedMessage);

        window.$message?.error(normalizedMessage, {
            onLeave: () => {
                state.errMsgStack = state.errMsgStack.filter(msg => msg !== normalizedMessage);

                setTimeout(() => {
                    state.errMsgStack = [];
                }, 5000);
            }
        });
    }
}
